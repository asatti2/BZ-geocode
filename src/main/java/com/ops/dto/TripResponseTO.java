package com.ops.dto;

import java.util.List;

public class TripResponseTO {
	
	private List<TripTO> trips;
	private int[][] distanceMatrixMap;
	public List<TripTO> getTrips() {
		return trips;
	}
	public void setTrips(List<TripTO> trips) {
		this.trips = trips;
	}
	public int[][] getDistanceMatrixMap() {
		return distanceMatrixMap;
	}
	public void setDistanceMatrixMap(int[][] distanceMatrixMap) {
		this.distanceMatrixMap = distanceMatrixMap;
	}

}
