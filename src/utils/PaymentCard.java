/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Payment card utility class: Contains every possible type of payment card currently supported within the application
 * and by extension within the system. An easy-to-access area if payment cards needed to be changed app wide.
 **********************************************************************************************************************/
package utils;

public class PaymentCard {
    private final String[] cards = {
            "Master Card",
            "VISA",
            "American Express",
            "Discover",
            "UnionPay",
            "JCB",
            "Diners Club",
            "TD Debit",
            "Scotiabank Debit",
            "RBC Debit"
    };

    public String[] getCards() { return cards; }
}
