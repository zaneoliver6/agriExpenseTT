package uwi.dcit.agriexpensesvr;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ResourcePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String account;
    private String keyrep;
    private int pId;
    private int resourceId;
    private String quantifier;
    private double qty;
    private double cost;
    private double qtyRemaining;
    private String type;
    private String elementName;
    private long purchaseDate;

    public String getKeyrep() {
        return keyrep;
    }

    public void setKeyrep(String keyrep) {
        this.keyrep = keyrep;
    }

    public ResourcePurchase() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ResourcePurchase(int pId, int resourceId, String quantifier, double qty,
                            double cost, double qtyRemaining, String type, long purchaseDate) {
        super();
        this.pId = pId;
        this.resourceId = resourceId;
        this.quantifier = quantifier;
        this.qty = qty;
        this.cost = cost;
        this.qtyRemaining = qtyRemaining;
        this.type = type;
        this.purchaseDate=purchaseDate;
    }
    public long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }


    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getQtyRemaining() {
        return qtyRemaining;
    }

    public void setQtyRemaining(double qtyRemaining) {
        this.qtyRemaining = qtyRemaining;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "purchaseId:" + pId + " resourceId:" + resourceId
                + " quantifier:" + quantifier + " qty:" + qty + " cost:" + cost
                + " remaining:" + qtyRemaining;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

}
