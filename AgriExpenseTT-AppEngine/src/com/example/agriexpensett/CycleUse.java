package com.example.agriexpensett;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class CycleUse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	private String Account;
	private String keyrep;
	private int id;
	private int cycleid;
	private int purchaseId;
	private double amount;
	private double cost;
	private String resource;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public CycleUse() {
		super();
	}

	public CycleUse(int cycleid, int purchaseId, double amount, String resource) {
		super();
		this.cycleid = cycleid;
		this.purchaseId = purchaseId;
		this.amount = amount;
		this.resource = resource;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		String m = "id: " + this.id + " cycleid:" + this.cycleid
				+ " purchaseid:" + this.purchaseId + " resource:"
				+ this.resource + " quantity:" + this.amount + "  cost:"
				+ this.cost;
		return m;
	}

	public String getKeyrep() {
		return keyrep;
	}

	public void setKeyrep(String keyrep) {
		this.keyrep = keyrep;
	}

}
