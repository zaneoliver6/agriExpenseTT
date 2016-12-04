package uwi.dcit.agriexpensesvr;
import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Cycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String Account;
    private int id;
    private int cropId;
    private String landType;
    private String keyrep;
    private double landQty;
    private double totalSpent;
    private double harvestAmt;
    private String harvestType;
    private double costPer;
    private String cropName;
    private long startDate;
    private String county;
    private String closed;

    public double getHarvestAmt() {
        return harvestAmt;
    }
    public void setHarvestAmt(double harvestAmt) {
        this.harvestAmt = harvestAmt;
    }
    public String getHarvestType() {
        return harvestType;
    }
    public void setHarvestType(String harvestType) {
        this.harvestType = harvestType;
    }
    public double getCostPer() {
        return costPer;
    }
    public void setCostPer(double costPer) {
        this.costPer = costPer;
    }
    public double getTotalSpent() {
        return totalSpent;
    }
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
    public String getKeyrep() {
        return keyrep;
    }
    public void setKeyrep(String keyrep) {
        this.keyrep = keyrep;
    }
    public Cycle() {
        super();
    }
    public Cycle(int cropId, String landType, double landQty, String cropName, long startDate, String county, String closed) {
        super();
        this.cropId = cropId;
        this.landType = landType;
        this.landQty = landQty;
        this.cropName = cropName;
        this.startDate = startDate;
        this.county = county;
        this.closed=closed;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCropId() {
        return cropId;
    }
    public void setCropId(int cropId) {
        this.cropId = cropId;
    }
    public String getLandType() {
        return landType;
    }
    public void setLandType(String landType) {
        this.landType = landType;
    }
    public double getLandQty() {
        return landQty;
    }
    public void setLandQty(double landQty) {
        this.landQty = landQty;
    }

    public Key getKey() {
        return key;
    }
    public void setKey(Key key) {
        this.key = key;
    }
    public String getAccount() {
        return Account;
    }
    public void setAccount(String account) {
        Account = account;
    }
    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public long getStartDate() {
        return startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public String getCounty() {
        return county;
    }
    public void setCounty(String county) {
        this.county = county;
    }
    public void setClosed(String closed){
        this.closed=closed;
    }
    public String getClosed(){
        return closed;
    }

    @Override
    public String toString() {
        return "cycleId:" + id + " cropId:" + cropId + " landType:" + landType + " landQty" + landQty + " cropName" + cropName + "closed"+closed;
    }

}