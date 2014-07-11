package dataObjects;

public class localCycleUse {
	private int id;
	private int cycleid;
	private int purchaseId;
	private double amount;
	private String resource;
	private double useCost;
	public localCycleUse() {
		super();
	}
	public localCycleUse(int cycleid, int purchaseId, double amount,String resource) {
		super();
		this.cycleid = cycleid;
		this.purchaseId = purchaseId;
		this.amount = amount;
		this.resource=resource;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public int getCycleid() {
		return cycleid;
	}
	public void setCycleid(int cycleid) {
		this.cycleid = cycleid;
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getUseCost() {
		return useCost;
	}
	public void setUseCost(double useCost) {
		this.useCost = useCost;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
