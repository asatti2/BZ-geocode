package com.ops.dto;

import java.util.List;

public class OrderTO {
	
	private String address;
	private List<Integer> dealerId;
	private int orderId;
	private String orderRefNo;
	private int orderAmount;	
	
	private double timeSpent;
	
	
	
	public double getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(double timeSpent) {
		this.timeSpent = timeSpent;
	}
	
	public int getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}	
	public List<Integer> getDealerId() {
		return dealerId;
	}
	public void setDealerId(List<Integer> dealerId) {
		this.dealerId = dealerId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getOrderRefNo() {
		return orderRefNo;
	}
	public void setOrderRefNo(String orderRefNo) {
		this.orderRefNo = orderRefNo;
	}
	
}
