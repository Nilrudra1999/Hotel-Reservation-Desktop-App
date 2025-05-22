/***********************************************************************************************************************
 * Status Enum Class: Representing the status of reservations and rooms, available and booked symbolize the status of
 * a room, while open and closed symbolize a reservation, when a guest checks-out and pays the reservation is closed.
 **********************************************************************************************************************/
package utils;

public enum Status {
    AVAILABLE,
    BOOKED,
    OPEN,
    CLOSED
}
