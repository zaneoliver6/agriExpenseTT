package uwi.dcit.AgriExpenseTT;


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
	public Cycle(int cropId, String landType, double landQty) {
		super();
		this.cropId = cropId;
		this.landType = landType;
		this.landQty = landQty;
		
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
	@Override
	public String toString() {
		String n="cycleId:"+id+" cropId:"+cropId+" landType:"+landType+" landQty"+landQty+"";
		return n;
	}
}
