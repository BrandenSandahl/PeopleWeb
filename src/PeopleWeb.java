
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {


    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS person");
        stmt.execute("CREATE TABLE IF NOT EXISTS person (person_id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip varchar)");
        stmt.close();
    }


    public static int populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        int affected = 0;
        File f = new File("people.csv");
        Scanner s = new Scanner(f);

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO person VALUES (null, ?, ?, ?, ?, ?)");
        s.nextLine(); //skip a line

        while (s.hasNext()) {
            String[] lineSplit = s.nextLine().split(",");
            stmt.setString(1, lineSplit[1]);
            stmt.setString(2, lineSplit[2]);
            stmt.setString(3, lineSplit[3]);
            stmt.setString(4, lineSplit[4]);
            stmt.setString(5, lineSplit[5]);
            affected = affected + stmt.executeUpdate();  //returns 1 each time and adds it.
        }
        stmt.close();
        return affected;  //returns the total number of values added to DB
    }

   public static ArrayList<Person> selectPersons(Connection conn, int offset) throws SQLException {
       PreparedStatement stmt = conn.prepareStatement("SELECT * FROM person LIMIT 20 OFFSET ? ");
       stmt.setInt(1, offset);
       ResultSet results = stmt.executeQuery();

       ArrayList<Person> personList = new ArrayList<>();

       while (results.next()) {
           personList.add(buildPerson(results));
       }
       stmt.close();
       return personList;
   }


    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM person WHERE person_id = ?");
        stmt.setInt(1, id);

        ResultSet results = stmt.executeQuery();

        Person p = new Person();

        if (results.next()) {
            p = buildPerson(results);
        }
        stmt.close();
        return p;
    }

    public static int getSize(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        int size = 0;
        ResultSet results =  stmt.executeQuery("SELECT COUNT(person_id) AS size FROM person");
        if (results.next()) size = results.getInt("size");
        return size;
    }

    public static Person buildPerson(ResultSet results) throws SQLException {
        int id = results.getInt(1);
        String firstName = results.getString(2);
        String lastName = results.getString(3);
        String email = results.getString(4);
        String country = results.getString(5);
        String ip = results.getString(6);
        Person p = new Person(id, firstName, lastName, email, country, ip);
        return p;
    }


    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        int records = populateDatabase(conn); //populate the database. This returns a int of the amount of records added.

        Spark.externalStaticFileLocation("public");
        Spark.init();


        Spark.get(
                "/",
                ((request, response) -> {
                    String nextString = request.queryParams("offset"); //this only sends when user directly interacts with something that causes it to send.

                    int offset = 0; //0 if it's nothing else
                    if (nextString != null) {   //this starts out null because it's not sending anything until you click one of the two.
                        offset = Integer.parseInt(nextString);
                    }

                    ArrayList<Person> personList = new ArrayList<>(selectPersons(conn,offset));

                    HashMap m = new HashMap();


                    //this is some maths that gets the current page number. I don't know really know how this is working, so don't ask.
                    double pageCurrent = ((getSize(conn)/20) * (double)offset/(double)getSize(conn));  //ugh. Math.
                    pageCurrent = Math.round(pageCurrent);

                    //math for total number of pages. This sucks. This really sucks.
                   int a = 0;
                    if ((double)getSize(conn)/20 != Math.round(getSize(conn)/20)) {
                        a = (getSize(conn)/20 + 1);
                    } else {
                       a = getSize(conn)/20;
                    }


                    m.put("pageCurrent", ((int)(pageCurrent) + 1));
                    m.put("pageMax", a);
                    m.put("people", personList); //put the part of the array to show
                    m.put("next", (personList.get(personList.size() - 1).getId() != getSize(conn)) ? offset + 20 : null);
                    m.put("previous", (personList.get(0).getId() > 1) ? offset - 20 : null );

                    return new ModelAndView(m, "home.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",  //So you have to tell the browser to go here at some point to pick this up.
                ((request, response) -> {
                    int id = (Integer.valueOf(request.queryParams("id")));  //place in the Array

                    Person p = selectPerson(conn, id);

                    HashMap m = new HashMap();
                    m.put("person", p);

                    return new ModelAndView(m, "person.html");

                }),
                new MustacheTemplateEngine()
        );

    }

}
