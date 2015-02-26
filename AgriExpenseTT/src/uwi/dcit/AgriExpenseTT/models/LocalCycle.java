package uwi.dcit.AgriExpenseTT.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalCycle implements Parcelable{
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
    private String cycleName;
	
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
	public LocalCycle() {
		super();
	}
	public LocalCycle(int cropId, String landType, double landQty,long dte) {
		super();
		this.cropId = cropId;
		this.landType = landType;
		this.landQty = landQty;
		this.time=dte;
		totalSpent=0;
	}

    public LocalCycle(int cropId, String name, String landType, double landQty,long dte) {
        super();
        this.cropId = cropId;
        this.cycleName = name;
        this.landType = landType;
        this.landQty = landQty;
        this.time=dte;
        totalSpent=0;
    }

    public LocalCycle(int id, int cropId, String landType, double landQty, long time, double totalSpent, double harvestAmt, String harvestType, double costPer, String cropName, String cycleName) {
        this.id = id;
        this.cropId = cropId;
        this.landType = landType;
        this.landQty = landQty;
        this.time = time;
        this.totalSpent = totalSpent;
        this.harvestAmt = harvestAmt;
        this.harvestType = harvestType;
        this.costPer = costPer;
        this.cropName = cropName;
        this.cycleName = cycleName;
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

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    @Override
	public String toString() {
		return "cycleId:"+id+" cropId:"+cropId+" landType:"+landType+" landQty"+landQty+"";
	}

	public LocalCycle(Parcel dest){
		id=dest.readInt();
		cropId=dest.readInt();
		landType=dest.readString();
		landQty=dest.readDouble();
		time=dest.readLong();
		totalSpent=dest.readDouble();
		harvestAmt=dest.readDouble();
		harvestType=dest.readString();
		costPer=dest.readDouble();
        cycleName =dest.readString();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(id);
		dest.writeInt(cropId);
		dest.writeString(landType);
		dest.writeDouble(landQty);
		dest.writeLong(time);
		dest.writeDouble(totalSpent);
		dest.writeDouble(harvestAmt);
		dest.writeString(harvestType);
		dest.writeDouble(costPer);
        dest.writeString(cycleName);
	}
	
	public static final Parcelable.Creator<LocalCycle> CREATOR = new Parcelable.Creator<LocalCycle>() {

		@Override
		public LocalCycle createFromParcel(Parcel source) {
			return new LocalCycle(source);
		}

		@Override
		public LocalCycle[] newArray(int size) {
			return new LocalCycle[size];
		}
	};
	@Override
	public int describeContents() {
		
		return 0;
	}
	public double getHarvestAmt() {
		return harvestAmt;
	}
	public void setHarvestAmt(double harvestAmt) {
		this.harvestAmt = harvestAmt;
	}
	
	
}
