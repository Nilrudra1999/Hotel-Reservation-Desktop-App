/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Payment details model class: Represents the details of a single credit card or some kind of payment card used by
 * the guests to pay their bills upon checkout.
 **********************************************************************************************************************/
package models;

public class PaymentDetail {
    private int cardNum;
    private String cardType;
    private int csc;
    private String expDate;

    public PaymentDetail(int cardNum, String cardType, int csc, String expDate) {
        this.cardNum = cardNum;
        this.cardType = cardType;
        this.csc = csc;
        this.expDate = expDate;
    }


    public int getCardNum() { return cardNum; }
    public String getCardType() { return cardType; }
    public int getCsc() { return csc; }
    public String getExpDate() { return expDate; }


    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setCardNum(int cardNum) { this.cardNum = cardNum; }
    public void setCsc(int csc) { this.csc = csc; }
    public void setExpDate(String expDate) { this.expDate = expDate; }
}
