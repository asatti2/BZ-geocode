package com.ops.dto;

import java.io.Serializable;
import java.util.List;

public class DealerDeliveryTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private int avgVehicleSpeed;
	private int breakTimePerDeliveryInMinute;
	private List<DealerTO> dealerList;
	private List<OrderTO> orderList;
	private int foCount;	
	private double 	workingHours;
	private double lunchTime;
	
	
	
	public double getLunchTime() {
		return lunchTime;
	}
	public void setLunchTime(double lunchTime) {
		this.lunchTime = lunchTime;
	}
	public int getAvgVehicleSpeed() {
		return avgVehicleSpeed;
	}
	public void setAvgVehicleSpeed(int avgVehicleSpeed) {
		this.avgVehicleSpeed = avgVehicleSpeed;
	}
	public int getBreakTimePerDeliveryInMinute() {
		return breakTimePerDeliveryInMinute;
	}
	public void setBreakTimePerDeliveryInMinute(int breakTimePerDeliveryInMinute) {
		this.breakTimePerDeliveryInMinute = breakTimePerDeliveryInMinute;
	}
	public int getFoCount() {
		return foCount;
	}
	public void setFoCount(int foCount) {
		this.foCount = foCount;
	}
	public double getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(double workingHours) {
		this.workingHours = workingHours;
	}
	public List<DealerTO> getDealerList() {
		return dealerList;
	}
	public void setDealerList(List<DealerTO> dealerList) {
		this.dealerList = dealerList;
	}
	public List<OrderTO> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<OrderTO> orderList) {
		this.orderList = orderList;
	}
	
	
}