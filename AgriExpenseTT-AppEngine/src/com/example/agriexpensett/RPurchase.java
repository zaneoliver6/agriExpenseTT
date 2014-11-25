package com.example.agriexpensett;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class RPurchase {
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

	public String getKeyrep() {
		return keyrep;
	}

	public void setKeyrep(String keyrep) {
		this.keyrep = keyrep;
	}

	public RPurchase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RPurchase(int pId, int resourceId, String quantifier, double qty,
			double cost, double qtyRemaining, String type) {
		super();
		this.pId = pId;
		this.resourceId = resourceId;
		this.quantifier = quantifier;
		this.qty = qty;
		this.cost = cost;
		this.qtyRemaining = qtyRemaining;
		this.type = type;
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
		String n = "purchaseId:" + pId + " resourceId:" + resourceId
				+ " quantifier:" + quantifier + " qty:" + qty + " cost:" + cost
				+ " remaining:" + qtyRemaining;
		return n;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

}
