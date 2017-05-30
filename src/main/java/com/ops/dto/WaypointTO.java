package com.ops.dto;

import java.io.Serializable;
import java.util.List;

public class WaypointTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String origin;
	private String destination;
	private List<String> waypoints;
	private int distance;
	private int weight;	
	

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public WaypointTO() {
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<String> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<String> waypoints) {
		this.waypoints = waypoints;
	}
	
	
}