package com.ops.managers;

import org.json.JSONObject;
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
			.append("&key=").append("AIzaSyBKMEIVosvAjOibv1o-DdnHiXsl2uVEORk")
			.append("&waypoints=optimize:true");
		for(String waypoint : waypointTO.getWaypoints())
			paramBuilder.append("|").append(waypoint);

		String resp = HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		
		//JSONObject obj = new JSONObject(resp);
		//ResponseGeoDto geoDto = new ResponseGeoDto();
		//geoDto.setGeocoded_waypoints(obj.getJSONArray("geocoded_waypoints").);
		//System.out.println("geoWayPoints: " + obj.getJSONArray("geocoded_waypoints"));
		//System.out.println("routes: " + obj.getJSONArray("routes"));

		return resp;
	}
}