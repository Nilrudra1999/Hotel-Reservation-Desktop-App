/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Billing model class: Represents the details of a single bill that's assigned to a reservation, each reservation
 * has an auto-generated bill object associated with it.
 **********************************************************************************************************************/
package models;

public class Bill {
    private final int id;
    private double subTotal;
    private double hst;
    private double total;
    private double discount = 0.0;
    private PaymentDetail paymentDetail;

    public Bill(int id, double subTotal, double hst, double total) {
        this.id = id;
        this.subTotal = subTotal;
        this.hst = hst;
        this.total = total;
    }


    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    public void setHst(double hst) { this.hst = hst; }
    public void setTotal(double total) { this.total = total; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setPaymentDetail(PaymentDetail paymentDetail) { this.paymentDetail = paymentDetail; }


    public int getId() { return id; }
    public double getSubTotal() { return subTotal; }
    public double getHst() { return hst; }
    public double getTotal() { return total; }
    public double getDiscount() { return discount; }
    public PaymentDetail getPaymentDetail() { return paymentDetail; }
}
