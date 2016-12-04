package uwi.dcit.AgriExpenseTT.models;

public class LocalCycleUse {
	private int id;
	private int cycleid;
	private int purchaseId;
	private double amount;
	private String resource;
	private double useCost;
	private String quantifier;
	public LocalCycleUse() {
		super();
	}

    public LocalCycleUse(int id, int cycleid, int purchaseId, double amount, String resource, double useCost, String quantifier) {
        this.id = id;
        this.cycleid = cycleid;
        this.purchaseId = purchaseId;
        this.amount = amount;
        this.resource = resource;
        this.useCost = useCost;
        this.quantifier = quantifier;
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
	public String getQuantifier() {
		return quantifier;
	}
	public void setQuantifier(String quantifier) {
		this.quantifier = quantifier;
	}

    @Override
    public String toString(){
        return "cycleid: " + cycleid + " purchaseid: " + purchaseId + " amount: "+amount + " resource: "+resource +" usecost: "+useCost +"quantifier:"+quantifier;
    }
	
}
