package ltd.akhbod.flipkart;

/**
 * Created by ibm on 16-03-2018.
 */

public class PriceStatus{

    String price,discount;

    public PriceStatus(){}

    public PriceStatus(String price, String discount) {
        this.price = price;
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }


}
