package com.ops.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ops.constants.URLConstants;
import com.ops.dto.WaypointTO;
import com.ops.utils.HttpConnectorUtil;

public class OptimalPathManager {
	private static final Logger logger = LoggerFactory.getLogger(OptimalPathManager.class);

	public String getWaypointLocation(WaypointTO waypointTO) {
		logger.info("waypointTO - "+waypointTO);

		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append("origin=").append(waypointTO.getOrigin())
			.append("&destination=").append(waypointTO.getDestination())
			.append("&waypoints=optimize:true");
		for(String waypoint : waypointTO.getWaypoints())
			paramBuilder.append("|").append(waypoint);

		String response = HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		return response;
	}
}