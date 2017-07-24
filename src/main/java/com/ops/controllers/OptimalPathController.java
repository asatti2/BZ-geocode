package com.ops.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ops.constants.ApplicationConstants;
import com.ops.constants.ResultType;
import com.ops.dto.DealerDeliveryTO;
import com.ops.dto.ResourceTO;
import com.ops.dto.ResponseTO;
import com.ops.dto.WaypointTO;
import com.ops.exceptions.ApplicationException;
import com.ops.exceptions.BusinessException;
import com.ops.managers.CostCalculatorManager;
import com.ops.managers.OptimalPathManager;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(ApplicationConstants.SECURE_URI)
public class OptimalPathController extends ResponseController {
	private static final Logger logger = LoggerFactory.getLogger(OptimalPathController.class);

	@RequestMapping(value = ApplicationConstants.WAYPOINT_URL, method = RequestMethod.POST)
	public ResponseTO getWaypointLocation(@RequestBody WaypointTO waypointTO) throws ApplicationException, BusinessException, IOException {
		List<String> response = new ArrayList<String>();
			ResponseTO resp = new ResponseTO();
			logger.info("-- Calling Waypoint API --");
			String destination = waypointTO.getDestination();
			List<String> waypoints = waypointTO.getWaypoints();
			
			while(waypointTO.getWaypoints().size() > 22){
				int size = 22;
				int initIndex = 0;
				List<String> splitWaypointsList = waypointTO.getWaypoints().subList(initIndex, size);
				waypointTO.setDestination(splitWaypointsList.get(splitWaypointsList.size()-1));
				waypointTO.setWaypoints(splitWaypointsList);
				response.add(new OptimalPathManager().getWaypointLocation(waypointTO));
				waypointTO.setOrigin(splitWaypointsList.get(splitWaypointsList.size()-1));
				waypointTO.setWaypoints(waypoints);
				waypointTO.getWaypoints().subList(0, 22).clear();
			}
			
			if(waypointTO.getWaypoints().size() <= 22){
				waypointTO.setDestination(destination);
				response.add(new OptimalPathManager().getWaypointLocation(waypointTO));
			}
						
			resp.setResponseData(response);
			resp.setMessage("Optimal path fetched successfully.");
			resp.setStatus(ResultType.SUCCESS);		

		return resp;
	}

	@RequestMapping(value = ApplicationConstants.COST_CALCULATE_URL, method = RequestMethod.POST)
	public String costCalculate(@RequestBody WaypointTO waypointTO) throws ApplicationException {
		String response = null;
		try {
			logger.info("-- Calling Waypoint API --");
			List<ResourceTO> resources = new CostCalculatorManager().costCalculate(waypointTO.getDistance(), waypointTO.getWeight());
			JSONArray resourceArray = new JSONArray();
			JSONObject resourceObject = null;
			for(ResourceTO resource : resources) {
				resourceObject = new JSONObject();
				resourceObject.put("name", resource.getName());
				resourceObject.put("number", resource.getNumber());
				resourceArray.put(resourceObject);
			}
			response = resourceArray.toString();
			logger.info("Response - " + response.toString());
		} catch (Exception ae) {
			logger.error("Exception occurred in getWaypointLocation() at controller layer - "+ae);
			throw ae;
		}

		return response;
	}
	
	@RequestMapping(value = ApplicationConstants.OPTIMAL_PATH, method = RequestMethod.POST)
	public ResponseTO getDealersDeliveryOptimalPath(@RequestBody DealerDeliveryTO dealerDeliveryTO)  throws ApplicationException, BusinessException, IOException, NumberFormatException, InterruptedException {
		
			logger.info("-- Calling Optimal Trips API --");
			
			ResponseTO resp = new ResponseTO();
			resp.setResponseData(new OptimalPathManager().processTrips(dealerDeliveryTO));
			resp.setMessage("Minimum trips fetched successfully. !!");
			resp.setStatus(ResultType.SUCCESS);
  
		return resp;
	}
}