package dataObjects;

import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import android.os.Parcel;
import android.os.Parcelable;

public class localResourcePurchase implements Parcelable{
	private int pId;
	private int resourceId;
	private String quantifier;
	private double qty;
	private double cost;
	private double qtyRemaining;
	private String type;
	
	public localResourcePurchase() {
		super();
		// TODO Auto-generated constructor stub
	}
	public localResourcePurchase(int pId, int resourceId, String quantifier,
			double qty, double cost, double qtyRemaining,String type) {
		super();
		this.pId = pId;
		this.resourceId = resourceId;
		this.quantifier = quantifier;
		this.qty = qty;
		this.cost = cost;
		this.qtyRemaining = qtyRemaining;
		this.type=type;
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
	@Override
	public String toString() {
		String n="purchaseId:"+pId+" resourceId:"+resourceId+" quantifier:"+quantifier+" qty:"+qty+" cost:"+cost+" remaining:"+qtyRemaining;
		return n;
	}
	
	public RPurchase toRPurchase(){
		RPurchase p=new RPurchase();
		p.setPId(this.pId);
		p.setCost(this.cost);
		p.setQty(this.qty);
		p.setQtyRemaining(this.qtyRemaining);
		p.setQuantifier(this.quantifier);
		p.setType(this.type);
		p.setResourceId(this.resourceId);
		return p;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(pId);
		dest.writeInt(resourceId);
		dest.writeString(type);
		dest.writeString(quantifier);
		dest.writeDouble(cost);
		dest.writeDouble(qty);
		dest.writeDouble(qtyRemaining);
	}
	public localResourcePurchase(Parcel dest){
		pId=dest.readInt();
		resourceId=dest.readInt();
		type=dest.readString();
		quantifier=dest.readString();
		cost=dest.readDouble();
		qty=dest.readDouble();
		qtyRemaining=dest.readDouble();
	}
	public static Parcelable.Creator<localResourcePurchase> CREATOR = new Parcelable.Creator<localResourcePurchase>() {

		@Override
		public localResourcePurchase createFromParcel(Parcel source) {
			return new localResourcePurchase(source);
		}

		@Override
		public localResourcePurchase[] newArray(int size) {
			return new localResourcePurchase[size];
		}
	};
	
	
}
