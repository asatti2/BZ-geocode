package com.ops.dto;

import java.util.List;

public class TripResponseTO {
	
	private List<TripTO> trips;
	private String[][] distanceMatrixMap;
	public List<TripTO> getTrips() {
		return trips;
	}
	public void setTrips(List<TripTO> trips) {
		this.trips = trips;
	}
	public String[][] getDistanceMatrixMap() {
		return distanceMatrixMap;
	}
	public void setDistanceMatrixMap(String[][] distanceMatrixMap) {
		this.distanceMatrixMap = distanceMatrixMap;
	}
}
