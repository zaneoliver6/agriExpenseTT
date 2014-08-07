package dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

public class localCycle implements Parcelable{
	private int id;
	private int cropId;
	private String landType;
	private double landQty;
	private long time;
	private double totalSpent;
	private double harvestAmt;
	private String harvestType;
	private double costPer;
	private String cropName;
	public String getCropName() {
		return cropName;
	}
	public void setCropName(String cropName) {
		this.cropName = cropName;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public localCycle() {
		super();
	}
	public localCycle(int cropId, String landType, double landQty,long dte) {
		super();
		this.cropId = cropId;
		this.landType = landType;
		this.landQty = landQty;
		this.time=dte;
		totalSpent=0;
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
	@Override
	public String toString() {
		String n="cycleId:"+id+" cropId:"+cropId+" landType:"+landType+" landQty"+landQty+"";
		return n;
	}
	public localCycle(Parcel dest){
		id=dest.readInt();
		cropId=dest.readInt();
		landType=dest.readString();
		landQty=dest.readDouble();
		time=dest.readLong();
		totalSpent=dest.readDouble();
		harvestAmt=dest.readDouble();
		harvestType=dest.readString();
		costPer=dest.readDouble();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeInt(cropId);
		dest.writeString(landType);
		dest.writeDouble(landQty);
		dest.writeLong(time);
		dest.writeDouble(totalSpent);
		dest.writeDouble(harvestAmt);
		dest.writeString(harvestType);
		dest.writeDouble(costPer);
	}
	
	public static Parcelable.Creator<localCycle> CREATOR = new Parcelable.Creator<localCycle>() {

		@Override
		public localCycle createFromParcel(Parcel source) {
			return new localCycle(source);
		}

		@Override
		public localCycle[] newArray(int size) {
			return new localCycle[size];
		}
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public double getHarvestAmt() {
		return harvestAmt;
	}
	public void setHarvestAmt(double harvestAmt) {
		this.harvestAmt = harvestAmt;
	}
	
	
}
