package ltd.akhbod.flipkart;

/**
 * Created by ibm on 23-03-2018.
 */

public class ListDetails {

    String image,prductNAME,productID,checkPRICE,shopNAME,selectedSize;

    public ListDetails(){}

    public ListDetails(String image, String prductNAME, String productID, String checkPRICE, String shopNAME,String selectedSize) {
        this.image = image;
        this.prductNAME = prductNAME;
        this.productID = productID;
        this.checkPRICE = checkPRICE;
        this.shopNAME = shopNAME;
        this.selectedSize=selectedSize;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrductNAME() {
        return prductNAME;
    }

    public void setPrductNAME(String prductNAME) {
        this.prductNAME = prductNAME;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCheckPRICE() {
        return checkPRICE;
    }

    public void setCheckPRICE(String checkPRICE) {
        this.checkPRICE = checkPRICE;
    }

    public String getShopNAME() {
        return shopNAME;
    }

    public void setShopNAME(String shopNAME) {
        this.shopNAME = shopNAME;
    }
}
