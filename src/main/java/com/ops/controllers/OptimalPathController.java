package com.ops.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ops.constants.ApplicationConstants;
import com.ops.dto.WaypointTO;
import com.ops.exceptions.ApplicationException;
import com.ops.managers.OptimalPathManager;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(ApplicationConstants.SECURE_URI)
public class OptimalPathController extends ResponseController {
	private static final Logger logger = LoggerFactory.getLogger(OptimalPathController.class);

	@RequestMapping(value = ApplicationConstants.WAYPOINT_URL, method = RequestMethod.POST)
	public String getWaypointLocation(@RequestBody WaypointTO waypointTO) throws ApplicationException {
		String response = null;
		try {
			logger.info("-- Calling Waypoint API --");
			/*WaypointTO waypointTO = new WaypointTO();
			waypointTO.setOrigin("Sukhrali,+Gurgaon,+Haryana");
			waypointTO.setDestination("Chakkarpur,+Gurgaon,+Haryana");
			String[] waypoints = new String[] { "MGF+Metropolitan+Mall,+Gurgaon,+Haryana", "IFFCO+Colony,+Gurgaon,+Haryana" };
			waypointTO.setWaypoints(waypoints);*/
			response = new OptimalPathManager().getWaypointLocation(waypointTO);
			logger.info("Response - " + response.toString());
		} catch (Exception ae) {
			logger.error("Exception occurred in getWaypointLocation() at controller layer - "+ae);
			throw ae;
		}

		return response;
	}
}