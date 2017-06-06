package com.ops.dto;

import java.util.List;

public class TripTO {
	
	List<DealerTO> dealersList;
	List<OrderTO> ordersList;
	String optimizeRouteData;
	double totalTripTime;	
	double dealerPointDistance;
	double interDeliveryPointDistance;
	String totalTripDisplayTime;	
	
	
	public String getTotalTripDisplayTime() {
		return totalTripDisplayTime;
	}
	public void setTotalTripDisplayTime(String totalTripDisplayTime) {
		this.totalTripDisplayTime = totalTripDisplayTime;
	}
	public double getDealerPointDistance() {
		return dealerPointDistance;
	}
	public void setDealerPointDistance(double dealerPointDistance) {
		this.dealerPointDistance = dealerPointDistance;
	}
	public double getInterDeliveryPointDistance() {
		return interDeliveryPointDistance;
	}
	public void setInterDeliveryPointDistance(double interDeliveryPointDistance) {
		this.interDeliveryPointDistance = interDeliveryPointDistance;
	}
	public double getTotalTripTime() {
		return totalTripTime;
	}
	public void setTotalTripTime(double totalTripTime) {
		this.totalTripTime = totalTripTime;
	}
	public String getOptimizeRouteData() {
		return optimizeRouteData;
	}
	public void setOptimizeRouteData(String optimizeRouteData) {
		this.optimizeRouteData = optimizeRouteData;
	}
	public List<DealerTO> getDealersList() {
		return dealersList;
	}
	public void setDealersList(List<DealerTO> dealersList) {
		this.dealersList = dealersList;
	}
	public List<OrderTO> getOrdersList() {
		return ordersList;
	}
	public void setOrdersList(List<OrderTO> ordersList) {
		this.ordersList = ordersList;
	}

}
