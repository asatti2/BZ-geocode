package com.ops.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.ops.managers.OptimalPathManager;
import com.ops.utils.CommonUtility;
import com.ops.utils.HttpConnectorUtil;

public class TripMgmtService {

	private static final Logger logger = LoggerFactory.getLogger(TripMgmtService.class);
	private String dealerLastPointAddress = null;
	
	
	public boolean isJSONValid(String reqStr) {
	    try {
	        new JSONObject(reqStr);
	    } catch (JSONException ex) {
	        // edited, to include @Arthur's comment
	        // e.g. in case JSONArray is valid as well...
	        try {
	            new JSONArray(reqStr);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}

	private String getOptimalRoute(WaypointTO waypointTO) throws BusinessException, IOException {

		logger.info("Fetching route..." + waypointTO + " with enabled optimization");
		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append("origin=").append(waypointTO.getOrigin()).append("&destination=")
				.append(waypointTO.getDestination()).append("&key=").append(new CommonUtility().getApplicationProperties("geoKeyForWaypoints")).append("&avoid=highways")
				.append("&waypoints=optimize:true");
		waypointTO.getWaypoints().forEach(waypoint -> paramBuilder.append("|").append(waypoint));

		String resp = HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		
		if(isJSONValid(resp)){		
			JSONObject jsonObj = new JSONObject(resp);
			if (!"OK".equals(jsonObj.get("status").toString())) {
				throw new BusinessException(analyzeStatusCode(jsonObj.get("status").toString()));
			}
		} else {
			throw new BusinessException(ApplicationConstants.RESP_ERROR);
		}
		return resp;
	}
	
	public JSONArray getDistance(String origin, String destination) throws BusinessException, IOException {
		
		StringBuilder paramBuilder = new StringBuilder();
		
		paramBuilder.append("origins=").append(origin)
					.append("&destinations=").append(destination)
					.append("&key=").append(new CommonUtility().getApplicationProperties("geoKeyForDistance"))
					.append("&avoid=highways");
		
		String resp = HttpConnectorUtil.callAPI(URLConstants.DISTANCE_MATRIX_URL, paramBuilder.toString());
		JSONObject jsonObj = null;
		if(isJSONValid(resp)){
			jsonObj = new JSONObject(resp);
			if (!"OK".equals(jsonObj.get("status").toString())) {
				throw new BusinessException(analyzeStatusCode(jsonObj.get("status").toString()));
			}
		} else { 
			throw new BusinessException(ApplicationConstants.RESP_ERROR);
		}
		
		return jsonObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
	}

	public String analyzeStatusCode(String statusCode) {

		String errorConstant;

		switch (statusCode) {
		case "NOT_FOUND":
			errorConstant = ApplicationConstants.NOT_FOUND;
			break;
		case "ZERO_RESULTS":
			errorConstant = ApplicationConstants.ZERO_RESULTS;
			break;
		case "MAX_WAYPOINTS_EXCEEDED":
			errorConstant = ApplicationConstants.MAX_WAYPOINTS_EXCEEDED;
			break;
		case "MAX_ROUTE_LENGTH_EXCEEDED":
			errorConstant = ApplicationConstants.MAX_ROUTE_LENGTH_EXCEEDED;
			break;
		case "UNKNOWN_ERROR":
			errorConstant = ApplicationConstants.UNKNOWN_ERROR;
			break;
		case "INVALID_REQUEST":
			errorConstant = ApplicationConstants.INVALID_REQUEST;
			break;
		case "OVER_QUERY_LIMIT":
			errorConstant = ApplicationConstants.OVER_QUERY_LIMIT;
			break;
		case "REQUEST_DENIED":
			errorConstant = ApplicationConstants.REQUEST_DENIED;
			break;
		case "MAX_ELEMENTS_EXCEEDED":
			errorConstant = ApplicationConstants.MAX_ELEMENTS_EXCEEDED;
			break;
		default:
			errorConstant = null;
		}
		return errorConstant;
	}

	private int calculateDistance(JSONArray waypointsArr) {

		logger.info("Calculating the distance among waypoints...");
		int optimizedDistanceBetweenWaypoints = 0;
		for (int i = 0; i < waypointsArr.length(); i++) {
			optimizedDistanceBetweenWaypoints += (int) waypointsArr.getJSONObject(i).getJSONObject("distance")
					.get("value");
		}
		return optimizedDistanceBetweenWaypoints;
	}

	public double calculateDealersWaypointDistance(List<DealerTO> dealersList) throws JSONException, BusinessException, IOException {

		WaypointTO dealersWaypoints = new WaypointTO();
		dealersWaypoints.setOrigin(dealersList.get(0).getAddress());
		dealersWaypoints.setDestination(dealersList.get(dealersList.size() - 1).getAddress());
		List<String> wayPointsList = new ArrayList<String>();
		dealersList.forEach(dealer -> wayPointsList.add(dealer.getAddress()));
		dealersWaypoints.setWaypoints(wayPointsList);

		JSONObject jsonObj = new JSONObject(getOptimalRoute(dealersWaypoints));
		JSONArray orderedWaypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

		JSONObject endPointLocation = orderedWaypointsArr.getJSONObject(orderedWaypointsArr.length() - 1)
				.getJSONObject("end_location");
		dealerLastPointAddress = endPointLocation.getDouble("lat") + "," + endPointLocation.getDouble("lng");

		double dealersWaypointsDistance = calculateDistance(orderedWaypointsArr);
		logger.info("Distance Between Dealers Waypoints = " + dealersWaypointsDistance + " km");

		return dealersWaypointsDistance / 1000;
	}

	public double calculateOrdersWaypointDistance(DealerDeliveryTO dealerDeliverTO) throws JSONException, BusinessException {		
		return dealerDeliverTO.getOrderTripDistance() / 1000;
	}
	
	public double calculateOrdersWaypointDistance(DealerDeliveryTO ddto, List<OrderTO> ordersList) throws JSONException, BusinessException, IOException {

		WaypointTO orderWaypoints = new WaypointTO();
		orderWaypoints.setOrigin(dealerLastPointAddress);
		//orderWaypoints.setDestination(ordersList.get(ordersList.size() - 1).getAddress());
		orderWaypoints.setDestination(dealerLastPointAddress);
		List<String> wayPointsList = new ArrayList<String>();
		ordersList.forEach(order -> wayPointsList.add(order.getAddress()));
		orderWaypoints.setWaypoints(wayPointsList);

		JSONObject jsonObj = new JSONObject(getOptimalRoute(orderWaypoints));
		JSONArray waypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
		double ordersWaypointsDistance = calculateDistance(waypointsArr);
		logger.info("Distance Between orders Waypoints = " + ordersWaypointsDistance + " km");
		
		Map<Integer, Double> distanceMatrixMap = new HashMap<Integer,Double>();
		
		distanceMatrixMap.put(0, 0.0);
		for (int i = 0; i < waypointsArr.length(); i++) {
			distanceMatrixMap.put(i+1,waypointsArr.getJSONObject(i).getJSONObject("distance").getDouble("value") / 1000);
		}
		
		ddto.setDistanceMap(distanceMatrixMap);
		return ordersWaypointsDistance / 1000;
	}

	public double calculateTotalTripTime(DealerDeliveryTO dataDto, double dealersWaypointsDistance,
			double ordersWaypointsDistance) {

		logger.info("Calculating time with all inclusions...");

		double totalDistanceToCover = dealersWaypointsDistance + ordersWaypointsDistance;
		int avgVehicleSpeed = dataDto.getAvgVehicleSpeed();
		double lunchTime = dataDto.getLunchTime();
		double breakTimePerDelivery = (double) dataDto.getBreakTimePerDeliveryInMinute() / (double) 60;

		double timeSpentInDeliveries = (breakTimePerDelivery * dataDto.getOrderList().size());
		double totalTimeToCoverTheDistance = totalDistanceToCover / avgVehicleSpeed;

		logger.info(
				"Estimated trip time: " + (timeSpentInDeliveries + totalTimeToCoverTheDistance + lunchTime) + " hours");
		return timeSpentInDeliveries + totalTimeToCoverTheDistance + lunchTime;
	}

	public void reduceOrder(double timeDifference, DealerDeliveryTO dataDto, List<OrderTO> removedOrdersPool,
			List<DealerTO> removedDealersPool, int[][] masterOrderMatrix, Map<Integer, Integer> masterOrdersMap, List<Integer> removedIndixes) {

		logger.info("Removing the order from the list if time is exceeding than working hours...");

		List<OrderTO> ordersList = dataDto.getOrderList();
		System.out.println("ORDERLISTSIZE#"+ordersList.size());
		int minOrderValue = getLeastOrderValue(ordersList);
		System.out.println("MINORDERVALUE"+minOrderValue);
		OrderTO minValOrderObj = ordersList.stream().filter(order -> order.getOrderAmount() == minOrderValue).collect(Collectors.toList()).get(0);
		
		List<OrderTO> ordersToBeRemoved  = fetchOrdersOfSameDistance(minValOrderObj, ordersList, masterOrderMatrix, masterOrdersMap, removedIndixes);
		System.out.println("ORDERREMOVEDLISTSIZE#"+ordersToBeRemoved.size());
		ordersList.removeAll(ordersToBeRemoved);
		dataDto.setOrderList(ordersList);		
		removedOrdersPool.addAll(ordersToBeRemoved);
		masterOrdersMap.clear();

	}
	
	public void reduceOrder(DealerDeliveryTO dataDto, List<OrderTO> removedOrdersPool,
			List<DealerTO> removedDealersPool) {

		logger.info("Removing the order from the list if time is exceeding than working hours...");

		List<OrderTO> ordersList = dataDto.getOrderList();
		int minOrderValue = getLeastOrderValue(ordersList);
		OrderTO minValOrderObj = ordersList.stream().filter(order -> order.getOrderAmount() == minOrderValue).collect(Collectors.toList()).get(0);
		ordersList.remove(minValOrderObj);
		dataDto.setOrderList(ordersList);
		removedOrdersPool.add(minValOrderObj);
	}
	
	private List<OrderTO> fetchOrdersOfSameDistance(OrderTO minValOrderObj, List<OrderTO> ordersList, int[][] masterOrderMatrix, Map<Integer,Integer> masterOrdersMap, List<Integer> removedIndixes){
		
		int minValOrderDistanceFromOrigin = masterOrderMatrix[1][masterOrdersMap.get(minValOrderObj.getOrderId())+2];
		System.out.println("MINORDERDISTANCEFROMORIGIN"+minValOrderDistanceFromOrigin);
		removedIndixes.clear();
		Set<Integer> indexesList = new HashSet<Integer>();
		Set<Integer> rIndexes = new HashSet<Integer>();
		
		for(int i=0; i<masterOrderMatrix.length; i++){
			
			for(int j=0; j<masterOrderMatrix.length; j++){
				
				if(masterOrderMatrix[i][j] == minValOrderDistanceFromOrigin && j > i && i!=0){
					indexesList.add(j-2);
					rIndexes.add(j);
				}
				
			}
		}
		
		removedIndixes.addAll(rIndexes);
		
		System.out.println("REMOVEDINDEXES"+removedIndixes);
				
		Set<Integer> orderIdsToBeRemoved = masterOrdersMap.entrySet().stream().filter(map -> indexesList.contains(map.getValue())).collect(Collectors.toMap(p->p.getKey(), p->p.getValue())).keySet();
		
		
		return ordersList.stream().filter(order -> orderIdsToBeRemoved.contains(order.getOrderId())).collect(Collectors.toList());
		
		
	}
	
	public int[][] updateMatrix(int[][] masterOrderMatrix, List<Integer> indexesList ) {

	        int destinationarr[][] = new int[masterOrderMatrix.length - indexesList.size()][masterOrderMatrix.length - indexesList.size()];

	        int p = 0;
	        
	        
	        for( int i = 0; i < masterOrderMatrix.length; ++i)
	        {
	            if (indexesList.contains(i))
	                continue;

	            int q = 0;
	            for( int j = 0; j < masterOrderMatrix.length; ++j)
	            {
	                if (indexesList.contains(j))
	                    continue;

	                destinationarr[p][q] = masterOrderMatrix[i][j];
	                ++q;
	            }

	            ++p;
	        }
	        return destinationarr;
	}

	private int getLeastOrderValue(List<OrderTO> ordersList) {
		ArrayList<Integer> orderValuesList = new ArrayList<Integer>();
		ordersList.forEach(order -> orderValuesList.add(order.getOrderAmount()));
		return Collections.min(orderValuesList);
	}

	public void generateTripRoute(TripTO tripWaypoints, int[][] constantMatrixMap) throws JSONException, BusinessException, InterruptedException, IOException {

		List<String> finalRouteData = new LinkedList<String>();
		List<DealerTO> dealersList = tripWaypoints.getDealersList();
		List<OrderTO> ordersList = tripWaypoints.getOrdersList();

		WaypointTO waypointTo = new WaypointTO();
		waypointTo.setOrigin(dealersList.get(0).getAddress());
		waypointTo.setDestination(dealersList.get(dealersList.size() - 1).getAddress());
		List<String> wayPointsList = new ArrayList<String>();

		for (int i = 1; i < dealersList.size() - 1; i++) {
			wayPointsList.add(dealersList.get(i).getAddress());
		}
		if (wayPointsList.isEmpty()) {
			wayPointsList.add(dealersList.get(0).getAddress());
		}
		waypointTo.setWaypoints(wayPointsList);
		JSONObject jsonObj = new JSONObject(getOptimalRoute(waypointTo));
		JSONArray dealerOrderedWaypointsArr = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

		for (int i = 0; i < dealerOrderedWaypointsArr.length(); i++) {
			JSONObject loc = dealerOrderedWaypointsArr.getJSONObject(i).getJSONObject("start_location");
			finalRouteData.add(loc.getDouble("lat") + "," + loc.getDouble("lng"));
		}
		JSONObject endLoc = dealerOrderedWaypointsArr.getJSONObject(dealerOrderedWaypointsArr.length() - 1)
				.getJSONObject("end_location");
		finalRouteData.add(endLoc.getDouble("lat") + "," + endLoc.getDouble("lng"));
		String sourceAddress = endLoc.getDouble("lat") + "," + endLoc.getDouble("lng");
		
		if(tripWaypoints.getOrdersList().size() > 22) {
			OptimalPathManager manager = new OptimalPathManager();
			manager.setConstantMatrixMap(constantMatrixMap);			
			DealerDeliveryTO to = manager.processDijakstra(sourceAddress, tripWaypoints.getOrdersList());
			tripWaypoints.setOrdersList(to.getOrderList());
			tripWaypoints.setInterDeliveryPointDistance(to.getOrderTripDistance()/1000);
		} else {
			waypointTo = new WaypointTO();
			waypointTo.setOrigin(endLoc.getDouble("lat") + "," + endLoc.getDouble("lng"));
			waypointTo.setDestination(endLoc.getDouble("lat") + "," + endLoc.getDouble("lng"));
			wayPointsList = new ArrayList<String>();
			for (int i = 0; i <= ordersList.size() - 1; i++) {
				wayPointsList.add(ordersList.get(i).getAddress());
			}
			if (wayPointsList.isEmpty()) {
				wayPointsList.add(ordersList.get(0).getAddress());
			}
			waypointTo.setWaypoints(wayPointsList);
			jsonObj = new JSONObject(getOptimalRoute(waypointTo));

			List<OrderTO> sortedOrdersList = new LinkedList<OrderTO>();
			JSONArray waypointsOrders = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("waypoint_order");
			for (int m = 0; m < waypointsOrders.length(); m++) {
				sortedOrdersList.add(ordersList.get(waypointsOrders.getInt(m)));
			}
			/*if (sortedOrdersList.size() > 1) {
				sortedOrdersList.add(ordersList.get(waypointsOrders.getInt(0)));
			}*/
			tripWaypoints.setOrdersList(sortedOrdersList);
		}
	}

	public void generateTripRoutes(List<TripTO> generatedTripsList, int[][] constantMatrixMap) throws BusinessException {
		generatedTripsList.forEach(generatedTrip -> {
			try {
				try {
					try {
						new TripMgmtService().generateTripRoute(generatedTrip, constantMatrixMap);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (BusinessException be) {
				try {
					throw be;
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void prepareTripData(DealerDeliveryTO dealerDeliveryTO, double currentTripTotalTime,
			double dealersWaypointsDistance, double ordersWaypointDistance, List<TripTO> generatedTripsList,
			int index) {

		logger.info("Generating Trip " + index+1 + " data...");
		TripTO generatedTrip = new TripTO();
		generatedTrip.setDealersList(dealerDeliveryTO.getDealerList());
		generatedTrip.setOrdersList(dealerDeliveryTO.getOrderList());
		generatedTrip.setTotalTripTime(currentTripTotalTime);
		generatedTrip.setDealerPointDistance(dealersWaypointsDistance);
		generatedTrip.setInterDeliveryPointDistance(ordersWaypointDistance);
		generatedTrip.setTotalTripDisplayTime(calculateDisplayTime(currentTripTotalTime));
		generatedTrip.setDistanceMap(dealerDeliveryTO.getDistanceMap());
		generatedTripsList.add(generatedTrip);
		
		index++;

	}

	private String calculateDisplayTime(double currentTripTotalTime) {
		double timeInMinutes = currentTripTotalTime * 60.00;
		return (int) timeInMinutes / 60 + " hours " + Math.round(timeInMinutes % 60) + " minutes";
	}

}
