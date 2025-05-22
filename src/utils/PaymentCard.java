/***********************************************************************************************************************
 * Payment Card Class: Represents the various payment cards that might be available for the customer to use when
 * paying for their hotel room. This class is used as a collections class to hold the card types.
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
