/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Guest model class: Represents the information of a single guest including their name, contact information, and
 * their vin number used to register customers of the hotel.
 **********************************************************************************************************************/
package models;

public class Guest {
    private final int id;
    private String name;
    private int phoneNumber;
    private String email;
    private String address;
    private String vinNumber;

    public Guest(int id, String name, String email, int phoneNumber, String address, String vinNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.vinNumber = vinNumber;
    }


    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setVinNumber(String vinNumber) { this.vinNumber = vinNumber; }


    public int getId() { return id; }
    public String getName() { return name; }
    public int getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getVinNumber() { return vinNumber; }
}
