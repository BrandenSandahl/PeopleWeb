import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {

    static ArrayList<Person> peopleList = new ArrayList<>();



    public static void main(String[] args) throws FileNotFoundException {
        Spark.externalStaticFileLocation("public");
        Spark.init();
        //read in my file into an ArrayList
        peopleList = readFromCsv();

        Spark.get(
                "/",
                ((request, response) -> {
                    String nextString = request.queryParams("offset"); //this only sends when user directly interacts with something that causes it to send.
                    int subStart = 0; //0 if it's nothing else
                    if (nextString != null) {   //this starts out null because it's not sending anything until you click one of the two.
                        subStart = Integer.parseInt(nextString);
                    }
                    int subTo = subStart + 20;

                    //this will keep the array from going out of bounds. I'm just constraining the sub method to the size of the array
                    if (subTo > peopleList.size()) {
                        subTo = peopleList.size();
                    }


                    HashMap m = new HashMap();
                    m.put("people", peopleList.subList(subStart, subTo)); //put the part of the array to show
                    m.put("next", ((subStart != (peopleList.size() - 20)) ? subStart + 20 : null));  //adjust next link, hide if we need to.
                    m.put("previous", (subStart != 0) ? subStart - 20 : null); //adjust previous link, hide if it's at 0.

                    return new ModelAndView(m, "home.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",  //So you have to tell the browser to go here at some point to pick this up.
                ((request, response) -> {
                    int id = (Integer.valueOf(request.queryParams("id")) - 1);  //place in the Array

                    Person person = peopleList.get(id);
                    HashMap m = new HashMap();
                    m.put("person", person);

                    return new ModelAndView(m, "person.html");

                }),
                new MustacheTemplateEngine()
        );

    }





    static ArrayList<Person> readFromCsv() throws FileNotFoundException {
        File f = new File("people.csv");
        Scanner s = new Scanner(f);

        ArrayList<Person> peopleList = new ArrayList<>();

        s.nextLine(); //skip a line

        while (s.hasNext()) {
            String[] lineSplit = s.nextLine().split(",");
            Person p = new Person(Integer.parseInt(lineSplit[0]), lineSplit[1], lineSplit[2], lineSplit[3], lineSplit[4], lineSplit[5]);
            peopleList.add(p);
        }

        return peopleList;

    }
}
