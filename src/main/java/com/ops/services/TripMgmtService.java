package com.ops.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ops.constants.ApplicationConstants;
import com.ops.constants.URLConstants;
import com.ops.dto.DealerDeliveryTO;
import com.ops.dto.DealerTO;
import com.ops.dto.OrderTO;
import com.ops.dto.TripTO;
import com.ops.dto.WaypointTO;
import com.ops.exceptions.BusinessException;
import com.ops.utils.HttpConnectorUtil;

public class TripMgmtService {
	
	private static final Logger logger = LoggerFactory.getLogger(TripMgmtService.class);
	private String dealerLastPointAddress = null;
	private final String geoKey = "AIzaSyBKMEIVosvAjOibv1o-DdnHiXsl2uVEORk";
		
	private String getOptimalRoute(WaypointTO waypointTO) throws BusinessException{
		
		logger.info("Fetching route..."+waypointTO +" with enabled optimization");
		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append("origin=").append(waypointTO.getOrigin())
			.append("&destination=").append(waypointTO.getDestination())
			.append("&key=").append(geoKey)
			.append("&avoid=highways")
			.append("&waypoints=optimize:true");
		waypointTO.getWaypoints().forEach(waypoint -> paramBuilder.append("|").append(waypoint));

		String resp = HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		if(resp.length() <= 0){
			throw new BusinessException(ApplicationConstants.INCORRECT_ADDRESS);
		}
		return resp;
	}
	
	private String getCustomizedOptimalRoute(WaypointTO waypointTO) throws BusinessException{
		
		logger.info("Fetching route..."+waypointTO +" with disabled optimization");
		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append("origin=").append(waypointTO.getOrigin())
			.append("&destination=").append(waypointTO.getDestination())
			.append("&key=").append(geoKey)
			.append("&avoid=highways")
			.append("&waypoints=optimize:false");
		waypointTO.getWaypoints().forEach(waypoint -> paramBuilder.append("|").append(waypoint));

		String resp =  HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		if(resp.length() <= 0){
			throw new BusinessException(ApplicationConstants.INCORRECT_ADDRESS);
		}
		return resp;
	}
	
	private int calculateDistance(JSONArray waypointsArr){
		
		logger.info("Calculating the distance among waypoints...");
		int optimizedDistanceBetweenWaypoints = 0;		
		for(int i=0; i<waypointsArr.length(); i++){
			optimizedDistanceBetweenWaypoints += (int) waypointsArr.getJSONObject(i).getJSONObject("distance").get("value");
		}		
		return optimizedDistanceBetweenWaypoints;		
	}
	
	public double calculateDealersWaypointDistance(List<DealerTO> dealersList) throws JSONException, BusinessException{
		
		WaypointTO dealersWaypoints = new WaypointTO();
		dealersWaypoints.setOrigin(dealersList.get(0).getAddress());
		dealersWaypoints.setDestination(dealersList.get(dealersList.size() -1).getAddress());		
		List<String> wayPointsList = new ArrayList<String>();
		dealersList.forEach(dealer -> wayPointsList.add(dealer.getAddress()));
		dealersWaypoints.setWaypoints(wayPointsList);
		
		JSONObject jsonObj = new JSONObject(getOptimalRoute(dealersWaypoints));
		JSONArray orderedWaypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");	
		
		JSONObject endPointLocation = orderedWaypointsArr.getJSONObject(orderedWaypointsArr.length() - 1).getJSONObject("end_location");
		dealerLastPointAddress = endPointLocation.getDouble("lat")+","+endPointLocation.getDouble("lng");
		
		double dealersWaypointsDistance = calculateDistance(orderedWaypointsArr);		
		logger.info("Distance Between Dealers Waypoints = "+dealersWaypointsDistance+" km");		
		
		return dealersWaypointsDistance/1000;
	}
	
	public double calculateOrdersWaypointDistance(List<OrderTO> ordersList) throws JSONException, BusinessException{
		
		WaypointTO orderWaypoints = new WaypointTO();
		orderWaypoints.setOrigin(dealerLastPointAddress);
		orderWaypoints.setDestination(ordersList.get(ordersList.size() -1).getAddress());		
		List<String> wayPointsList = new ArrayList<String>();
		ordersList.forEach(order -> wayPointsList.add(order.getAddress()));
		orderWaypoints.setWaypoints(wayPointsList);
		
		JSONObject jsonObj = new JSONObject(getOptimalRoute(orderWaypoints));
		double ordersWaypointsDistance = calculateDistance(jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs"));		
		logger.info("Distance Between orders Waypoints = "+ordersWaypointsDistance+ " km");
		
		return ordersWaypointsDistance/1000;
	}
	
	public double calculateTotalTripTime(DealerDeliveryTO dataDto, double dealersWaypointsDistance, double ordersWaypointsDistance){
		
		logger.info("Calculating time with all inclusions...");
		
		double totalDistanceToCover = dealersWaypointsDistance + ordersWaypointsDistance;
		int avgVehicleSpeed = dataDto.getAvgVehicleSpeed();		
		double lunchTime = dataDto.getLunchTime();
		double breakTimePerDelivery = (double)dataDto.getBreakTimePerDeliveryInMinute() / (double)60;
		
		double timeSpentInDeliveries = (breakTimePerDelivery * dataDto.getOrderList().size());		
		double totalTimeToCoverTheDistance = totalDistanceToCover / avgVehicleSpeed;
		
		logger.info("Estimated trip time: "+(timeSpentInDeliveries + totalTimeToCoverTheDistance + lunchTime)+" hours");
		return timeSpentInDeliveries + totalTimeToCoverTheDistance + lunchTime;
	}
	
	
	public void reduceOrder(DealerDeliveryTO dataDto, List<OrderTO> removedOrdersPool, List<DealerTO> removedDealersPool){
		
		logger.info("Removing the order from the list if time is exceeding than working hours...");
		
		List<OrderTO> ordersList = dataDto.getOrderList();		
		int minOrderValue = getLeastOrderValue(ordersList);
		OrderTO minValOrderObj = ordersList.stream().filter(order -> order.getOrderAmount() == minOrderValue ).collect(Collectors.toList()).get(0);		
		ordersList.remove(minValOrderObj);
		dataDto.setOrderList(ordersList);		
		removedOrdersPool.add(minValOrderObj);		
	}
	
	private int getLeastOrderValue(List<OrderTO> ordersList){
		ArrayList<Integer> orderValuesList = new ArrayList<Integer>();
		ordersList.forEach(order->orderValuesList.add(order.getOrderAmount()));				
		return Collections.min(orderValuesList);		
	}
	
	public void generateTripRoute(TripTO tripWaypoints) throws JSONException, BusinessException{
		
		List<String> finalRouteData = new LinkedList<String>();
		List<DealerTO> dealersList = tripWaypoints.getDealersList();
		List<OrderTO> ordersList = tripWaypoints.getOrdersList();
		
		WaypointTO waypointTo = new WaypointTO();
		waypointTo.setOrigin(dealersList.get(0).getAddress());
		waypointTo.setDestination(dealersList.get(dealersList.size() - 1).getAddress());
		List<String> wayPointsList = new ArrayList<String>();		
		
		for(int i=1; i<dealersList.size()-1; i++){
			wayPointsList.add(dealersList.get(i).getAddress());
		}
		if(wayPointsList.isEmpty()){
			wayPointsList.add(dealersList.get(0).getAddress());
		}
		waypointTo.setWaypoints(wayPointsList);
		JSONObject jsonObj = new JSONObject(getOptimalRoute(waypointTo));
		JSONArray dealerOrderedWaypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
		for(int i=0; i<dealerOrderedWaypointsArr.length(); i++){
			JSONObject loc = dealerOrderedWaypointsArr.getJSONObject(i).getJSONObject("start_location");
			finalRouteData.add(loc.getDouble("lat")+","+loc.getDouble("lng"));
		}
		JSONObject endLoc = dealerOrderedWaypointsArr.getJSONObject(dealerOrderedWaypointsArr.length()-1).getJSONObject("end_location");
		finalRouteData.add(endLoc.getDouble("lat")+","+endLoc.getDouble("lng"));
		
		
		waypointTo = new WaypointTO();
		waypointTo.setOrigin(endLoc.getDouble("lat")+","+endLoc.getDouble("lng"));
		waypointTo.setDestination(ordersList.get(ordersList.size() - 1).getAddress());
		wayPointsList = new ArrayList<String>();
		for(int i=0; i<ordersList.size()-1; i++){
			wayPointsList.add(ordersList.get(i).getAddress());
		}
		if(wayPointsList.isEmpty()){
			wayPointsList.add(ordersList.get(0).getAddress());
		}
		waypointTo.setWaypoints(wayPointsList);
		jsonObj = new JSONObject(getOptimalRoute(waypointTo));
		JSONArray ordersOrderedWaypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
		for(int i=0; i<ordersOrderedWaypointsArr.length(); i++){
			JSONObject loc = ordersOrderedWaypointsArr.getJSONObject(i).getJSONObject("end_location");
			finalRouteData.add(loc.getDouble("lat")+","+loc.getDouble("lng"));
		}
		
		waypointTo = new WaypointTO();
		waypointTo.setOrigin(finalRouteData.get(0));
		waypointTo.setDestination(finalRouteData.get(finalRouteData.size()-1));
		finalRouteData.remove(0);
		finalRouteData.remove(finalRouteData.size()-1);
		waypointTo.setWaypoints(finalRouteData);
		logger.info("Generating Trip Route for the final data: "+finalRouteData);
		tripWaypoints.setOptimizeRouteData(getCustomizedOptimalRoute(waypointTo));
		
		List<String> deliveryPointsOrders = new ArrayList<String>();
		jsonObj = new JSONObject(tripWaypoints.getOptimizeRouteData());
		JSONArray arr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
		for(int k = tripWaypoints.getDealersList().size(); k<arr.length(); k++){
			deliveryPointsOrders.add(arr.getJSONObject(k).getString("end_address"));
		}
		
		tripWaypoints.setOrdersDirections(deliveryPointsOrders);
		
		List<String> dealerPointOrders = new ArrayList<String>();
		for(int k = 0; k<tripWaypoints.getDealersList().size(); k++){
			dealerPointOrders.add(arr.getJSONObject(k).getString("end_address"));
		}
		
		tripWaypoints.setDealersDirections(dealerPointOrders);
		
	}
	
	public void generateTripRoutes(List<TripTO> generatedTripsList) throws BusinessException {
		generatedTripsList.forEach(generatedTrip -> {
			try{
				new TripMgmtService().generateTripRoute(generatedTrip);
			}catch(BusinessException be){
				try {
					throw be;
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void prepareTripData(DealerDeliveryTO dealerDeliveryTO, double currentTripTotalTime, double dealersWaypointsDistance, double ordersWaypointDistance, List<TripTO> generatedTripsList, int index){
		
		logger.info("Generating Trip "+index+" data...");
		TripTO generatedTrip = new TripTO();
		generatedTrip.setDealersList(dealerDeliveryTO.getDealerList());
		generatedTrip.setOrdersList(dealerDeliveryTO.getOrderList());
		generatedTrip.setTotalTripTime(currentTripTotalTime);
		generatedTrip.setDealerPointDistance(dealersWaypointsDistance);
		generatedTrip.setInterDeliveryPointDistance(ordersWaypointDistance);
		generatedTrip.setTotalTripDisplayTime(calculateDisplayTime(currentTripTotalTime));
		generatedTripsList.add(generatedTrip);
		
	}
	
	private String calculateDisplayTime(double currentTripTotalTime){
		double timeInMinutes = currentTripTotalTime * 60.00 ;
		return (int)timeInMinutes/60 + " hours "+Math.round(timeInMinutes%60) +" minutes";
	}

}
