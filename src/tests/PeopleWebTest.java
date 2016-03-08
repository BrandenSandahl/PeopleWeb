import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by branden on 3/2/16 at 12:49.
 */
public class PeopleWebTest {


    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        PeopleWeb.createTables(conn);
        return conn;
    }

    //kill the tables so we have fresh data for new tests
    public  void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE person");
        conn.close();
    }

    @Test
    public void testCreateFromCsv() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();

        int affected = PeopleWeb.populateDatabase(conn);

        endConnection(conn);

        assertTrue(affected > 1000);


    }

    @Test
    public void testSelectPersons() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();

        PeopleWeb.populateDatabase(conn);

        ArrayList<Person> personList = new ArrayList<>(PeopleWeb.selectPersons(conn, 20));

        endConnection(conn);

        assertTrue(personList.size() == 20);


    }

    @Test
    public void testSize() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();

        PeopleWeb.populateDatabase(conn);

        int size = PeopleWeb.getSize(conn);

        endConnection(conn);

        assertTrue(size > 0);



    }


}