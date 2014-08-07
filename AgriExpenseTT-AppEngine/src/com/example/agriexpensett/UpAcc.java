package com.example.agriexpensett;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class UpAcc {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	private String keyrep;
	private long lastUpdated;
	private String acc;
	private int signedIn;
	private String county;
	private String address;
	
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public UpAcc() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UpAcc(Key key, String keyrep, long lastUpdated, String acc) {
		super();
		this.key = key;
		this.keyrep = keyrep;
		this.lastUpdated = lastUpdated;
		this.acc = acc;
	}
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public String getKeyrep() {
		return keyrep;
	}
	public void setKeyrep(String keyrep) {
		this.keyrep = keyrep;
	}
	public long getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public int getSignedIn() {
		return signedIn;
	}
	public void setSignedIn(int signedIn) {
		this.signedIn = signedIn;
	}
	
}