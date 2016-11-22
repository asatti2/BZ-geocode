package com.ops.dto;

import java.io.Serializable;
import java.util.Arrays;

public class WaypointTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String origin;
	private String destination;
	private String[] waypoints;

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

	public String[] getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public String toString() {
		return "WaypointTO [origin=" + origin + ", destination=" + destination + ", waypoints=" + Arrays.toString(waypoints) + "]";
	}
}