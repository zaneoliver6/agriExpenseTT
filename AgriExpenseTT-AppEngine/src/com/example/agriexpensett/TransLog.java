package com.example.agriexpensett;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class TransLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	private int id;
	private int rowId;
	private String tableKind;
	private String keyrep;
	private Long transTime;
	private String operation;
	private String Account;

	public TransLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransLog(Key key, int id, String tableKind, String keyrep,
			Long transTime, String operation, String account) {
		super();
		this.key = key;
		this.id = id;
		this.tableKind = tableKind;
		this.keyrep = keyrep;
		this.transTime = transTime;
		this.operation = operation;
		this.Account = account;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getTableKind() {
		return tableKind;
	}

	public void setTableKind(String tableKind) {
		this.tableKind = tableKind;
	}

	public String getKeyrep() {
		return keyrep;
	}

	public void setKeyrep(String keyrep) {
		this.keyrep = keyrep;
	}

	public Long getTransTime() {
		return transTime;
	}

	public void setTransTime(Long transTime) {
		this.transTime = transTime;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getAccount() {
		return Account;
	}

	public void setAccount(String account) {
		Account = account;
	}

	@Override
	public String toString() {
		String m = "table " + tableKind + " trans" + operation + " transtime"
				+ transTime;
		return m;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

}
