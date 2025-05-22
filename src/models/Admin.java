/***********************************************************************************************************************
 * Admin Model Class: Contains the admin login information and uses methods to compare provided strings with the admin
 * credentials present within the class upon instantiation.
 * name01, password01
 * name02, password02
 **********************************************************************************************************************/
package models;

public class Admin {
    private final int id;
    private final String username;
    private final String password;

    public Admin(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }


    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }


    public boolean correctUsername(String usernameStr) {
        return usernameStr.equals(username);
    }


    public boolean correctPassword(String passwordStr) {
        return passwordStr.equals(password);
    }
}
