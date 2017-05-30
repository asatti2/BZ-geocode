package com.ops.dto;

import java.io.Serializable;

public class DealerTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String address;
	private int dealerId;
	private String dealerName;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getDealerId() {
		return dealerId;
	}
	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	
}
