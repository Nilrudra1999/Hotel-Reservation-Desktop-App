/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Status enum utility class: Represents the status of reservations and rooms, available and booked symbolize the
 * status of a room, open and closed symbolize reservations, if a guest checks-out/pays the reservation is closed.
 **********************************************************************************************************************/
package utils;

public enum Status {
    AVAILABLE,
    BOOKED,
    OPEN,
    CLOSED
}
