/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Reservation model class: Represents the details of a single reservation, this class contains a reference to a
 * guest, bill, and a set of rooms. Each reservation is made under a single guest and contains only one bill.
 **********************************************************************************************************************/
package models;

import utils.RoomType;
import utils.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



public class Reservation {
    private final int id;
    private Guest guest;
    private Collection<Room> rooms;
    private Bill billingInfo;
    private String checkInDate;
    private String checkOutDate;
    private int numOfGuests;
    private Status status;

    public Reservation(int id, String checkInDate, String checkOutDate, int numOfGuests) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numOfGuests = numOfGuests;
        this.status = Status.OPEN;
    }


    public void setGuest(Guest guest) { this.guest = guest; }
    public void setRooms(Collection<Room> rooms) { this.rooms = rooms; }
    public void setBillingInfo(Bill billingInfo) { this.billingInfo = billingInfo; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setNumOfGuests(int numOfGuests) { this.numOfGuests = numOfGuests; }
    public void setStatus(Status status) { this.status = status; }


    public Guest getGuest() { return guest; }
    public Collection<Room> getRooms() { return rooms; }
    public Bill getBillingInfo() { return billingInfo; }
    public int getId() { return id; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public int getNumOfGuests() { return numOfGuests; }
    public Status getStatus() { return status; }


    public String getcompleteDateAsString() { return checkInDate + " | " + checkOutDate; }
    public Room getFirstRoom() {
        if (rooms != null && !rooms.isEmpty()) {
            Iterator<Room> iterator = rooms.iterator();
            return iterator.next();
        }
        return null;
    }


    public Room searchRoomById(int srcId) {
        for (Room room : rooms) {
            if (room.getRoomNum() == srcId) return room;
        }
        return null;
    }


    public Collection<Room> searchRoomByType(RoomType srcType) {
        Collection<Room> rcRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getRoomType() == srcType) rcRooms.add(room);
        }
        return rcRooms;
    }
}
