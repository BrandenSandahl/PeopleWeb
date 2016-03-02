/**
 * Created by branden on 2/24/16 at 13:31.
 */
public class Person {

    private int id;
    private String firstName, lastName, email, country, ip;

    public Person() {
    }

    public Person(int id, String firstName, String lastName, String email, String country, String ip) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setCountry(country);
        setIp(ip);

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}