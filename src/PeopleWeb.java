import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PeopleWeb {




    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Person> peopleList = readFromCsv();

        System.out.println("check it out dawg");


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
