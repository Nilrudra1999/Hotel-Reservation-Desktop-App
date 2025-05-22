/***********************************************************************************************************************
 * Room Model Class: Contains the room information for any room within the hotel, include things like number of beds,
 * room number, and price. It also shows the status of the room, available or not, which can be changed.
 **********************************************************************************************************************/
package models;

import utils.RoomType;
import utils.Status;

public class Room {
    private final int roomNum;
    private final RoomType roomType;
    private final int numOfBeds;
    private final double price;
    private Status status;

    public Room(int roomNum, RoomType roomType, int numOfBeds, double price, Status status) {
        this.roomNum = roomNum;
        this.roomType = roomType;
        this.numOfBeds = numOfBeds;
        this.price = price;
        this.status = status;
    }


    public int getRoomNum() { return roomNum; }
    public RoomType getRoomType() { return roomType; }
    public int getNumOfBeds() { return numOfBeds; }
    public double getPrice() { return price; }
    public Status getStatus() { return status; }
    public String getStringStatus() {
        if (status == Status.AVAILABLE) {
            return "Available";
        } else {
            return "Booked";
        }
    }


    public void setStatus(Status status) { this.status = status; }
}
