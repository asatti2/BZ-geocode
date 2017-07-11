package com.ops.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
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
import com.ops.exceptions.ApplicationException;
import com.ops.exceptions.BusinessException;
import com.ops.services.TripMgmtService;
import com.ops.utils.HttpConnectorUtil;
import com.ops.utils.dijakstra.Edge;
import com.ops.utils.dijakstra.TSPAlgo;
import com.ops.utils.dijakstra.Vertex;

public class OptimalPathManager {
	private static final Logger logger = LoggerFactory.getLogger(OptimalPathManager.class);

	private double currentTripTotalTime = 0;
	private double previousTripTotalTime = 0;
	private List<OrderTO> removedOrdersPool = new LinkedList<OrderTO>();
	private List<DealerTO> removedDealersPool = new LinkedList<DealerTO>();
	private List<TripTO> generatedTripsList = new LinkedList<TripTO>();
	private List<DealerTO> originalDealersPool = null;
	private TripMgmtService tripService = new TripMgmtService();
	private int recursionIndex = 0;
	
	public String getWaypointLocation(WaypointTO waypointTO) throws BusinessException {
		logger.info("waypointTO - " + waypointTO);

		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append("origin=").append(waypointTO.getOrigin()).append("&destination=")
				.append(waypointTO.getDestination()).append("&key=")
				.append("AIzaSyBKMEIVosvAjOibv1o-DdnHiXsl2uVEORk")
				.append("&avoid=highways")
				.append("&waypoints=optimize:true");		
		
		for (String waypoint : waypointTO.getWaypoints())
			paramBuilder.append("|").append(waypoint);

		String resp = HttpConnectorUtil.callAPI(URLConstants.WAYPOINT_URL, paramBuilder.toString());
		
		JSONObject jsonObj = new JSONObject(resp);
		if (!"OK".equals(jsonObj.get("status").toString())) {
			throw new BusinessException(new TripMgmtService().analyzeStatusCode(jsonObj.get("status").toString()));
		}

		return resp;
	}

	
	public List<TripTO> processTrips(DealerDeliveryTO dealerDeliveryTO) throws BusinessException, ApplicationException {
		logger.info("Request recieved to fetch trips...");
		validateDealersOrdersIds(dealerDeliveryTO);
		originalDealersPool = dealerDeliveryTO.getDealerList();		
		getOptimizedTrips(dealerDeliveryTO);
		tripService.generateTripRoutes(generatedTripsList);
		return generatedTripsList;
	}
	
	public void validateDealersOrdersIds(DealerDeliveryTO dealerDeliveryTO) throws BusinessException{
				
		List<DealerTO> dealersList = dealerDeliveryTO.getDealerList();
		List<OrderTO> ordersList = dealerDeliveryTO.getOrderList();
		
		Set<Integer> dealerIds  = new HashSet<Integer>();
		Set<Integer> orderDealerIds  = new HashSet<Integer>();
		
		dealersList.forEach(dealerObj -> {
			dealerIds.add(dealerObj.getDealerId());
		});
		
		ordersList.forEach(orderObj -> {
			orderDealerIds.addAll(orderObj.getDealerId());
		});
		
		Iterator<Integer> itr = orderDealerIds.iterator();
		while(itr.hasNext()){
			if(!dealerIds.contains(itr.next())){
				throw new BusinessException(ApplicationConstants.DEALER_ID_MISMATCH);
			}
		}		
	}

	private void getOptimizedTrips(DealerDeliveryTO dealerDeliveryTO) throws BusinessException, ApplicationException {
		
		recursionIndex++;
		
		try{
			
			double workingHours = dealerDeliveryTO.getWorkingHours();		
			double dealersWaypointsDistance = tripService.calculateDealersWaypointDistance(dealerDeliveryTO.getDealerList());
			double ordersWaypointDistance = 0.0;
			if(dealerDeliveryTO.getOrderList().size() > 22){
				processDijakstra(dealerDeliveryTO.getDealerList().get(dealerDeliveryTO.getDealerList().size()-1).getAddress(), dealerDeliveryTO);
				ordersWaypointDistance = tripService.calculateOrdersWaypointDistance(dealerDeliveryTO);
			} else {
				ordersWaypointDistance = tripService.calculateOrdersWaypointDistance(dealerDeliveryTO, dealerDeliveryTO.getOrderList());
			}
			double totalTripTime = tripService.calculateTotalTripTime(dealerDeliveryTO, dealersWaypointsDistance,ordersWaypointDistance);
			
			previousTripTotalTime = currentTripTotalTime;
			currentTripTotalTime = totalTripTime;
			
			if(removedOrdersPool.size() > 1){
				removedOrdersPool.get(removedOrdersPool.size()-2).setTimeSpent(previousTripTotalTime-currentTripTotalTime);	
			}	
			

			if (totalTripTime > workingHours) {
				logger.info("Total Trip time is : " + totalTripTime + " which is " + (totalTripTime - workingHours) +" hours greater than working hours.");
				tripService.reduceOrder(dealerDeliveryTO, removedOrdersPool, removedDealersPool);			
				manageDealersAccordingToOrders(dealerDeliveryTO);
				if(!dealerDeliveryTO.getDealerList().isEmpty() && !dealerDeliveryTO.getOrderList().isEmpty()){
					getOptimizedTrips(dealerDeliveryTO);
				}else{
					throw new BusinessException(ApplicationConstants.INVALID_WORKING_HOURS);
				}
			} else {
				logger.info("Total Trip time is : " + totalTripTime + " which is " + (workingHours - totalTripTime) +" hours less than working hours.");
				enhanceTripIfPossible(dealerDeliveryTO);
				manageDealersAccordingToOrders(dealerDeliveryTO);			
				
				tripService.prepareTripData(dealerDeliveryTO, currentTripTotalTime, dealersWaypointsDistance, ordersWaypointDistance, generatedTripsList, recursionIndex);
				
				if(removedOrdersPool.size() > 0){
					removedOrdersPool.forEach(removedOrder -> removedOrder.setTimeSpent(0));
					previousTripTotalTime = 0;
					currentTripTotalTime = 0;				
					dealerDeliveryTO.setOrderList(removedOrdersPool);
					removedOrdersPool = new LinkedList<OrderTO>();
					manageDealersAccordingToOrders(dealerDeliveryTO);
					if(!dealerDeliveryTO.getDealerList().isEmpty() && !dealerDeliveryTO.getOrderList().isEmpty()){
						getOptimizedTrips(dealerDeliveryTO);
					}else{
						throw new BusinessException(ApplicationConstants.INVALID_WORKING_HOURS);
					}
				}			
				
			}
			
		} catch (BusinessException be){
			throw be;
		} catch (Exception e){
			e.printStackTrace();
			throw new ApplicationException();
		}
		
	}
	
	private void manageDealersAccordingToOrders(DealerDeliveryTO dealerDeliveryTO){
		
		logger.info("Fetching dealers related only to the orders...");
		Set<Integer> dealerIds = new HashSet<Integer>();		
		dealerDeliveryTO.getOrderList().forEach(order -> {
			dealerIds.addAll(order.getDealerId());
		});		
		List<DealerTO> dealersList = originalDealersPool.stream().filter(dealer -> dealerIds.contains(dealer.getDealerId())).collect(Collectors.toList());
		dealerDeliveryTO.setDealerList(dealersList);		
	}

	private void enhanceTripIfPossible(DealerDeliveryTO dealerDeliveryTO){
		
		logger.info("Enhancing the trip (trying to utilize the maximum time...)");
		List<OrderTO> orderToBeDeletedFromRemovedPool = new LinkedList<OrderTO>();
		if(currentTripTotalTime < dealerDeliveryTO.getWorkingHours()){
			List<OrderTO> existingOrders = dealerDeliveryTO.getOrderList();
			removedOrdersPool.forEach(removedOrder -> {
				if(removedOrder.getTimeSpent() + currentTripTotalTime < dealerDeliveryTO.getWorkingHours()){					
					existingOrders.add(removedOrder);
					currentTripTotalTime += removedOrder.getTimeSpent();
					orderToBeDeletedFromRemovedPool.add(removedOrder);
				}
			});			
			for(int j=0; j<orderToBeDeletedFromRemovedPool.size(); j++){
				removedOrdersPool.remove(orderToBeDeletedFromRemovedPool.get(j));
			}			
			dealerDeliveryTO.setOrderList(existingOrders);
		}
	}
	
	private void processDijakstra(String sourceAddress, DealerDeliveryTO dealerDeliveryTO) throws BusinessException{
		
		List<OrderTO> orders = dealerDeliveryTO.getOrderList();
		LinkedList<OrderTO> sortedOrders = new LinkedList<OrderTO>();
		List<Vertex> vertexes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		double orderTripDistance = 0.0;
		
		orders.forEach(order -> {
			order.setOrderVertex(new Vertex(order.getOrderId()+"", order.getAddress()));
		});
		
		vertexes.add(new Vertex("vX", sourceAddress));
		JSONArray arr1 = tripService.getDistance(sourceAddress, prepareDestinationAddresses(0, orders));
		for(int j=0; j<orders.size(); j++){			 
			edges.add(new Edge("eIn", new Vertex("vX", sourceAddress), orders.get(j).getOrderVertex(), arr1.getJSONObject(j).getJSONObject("distance").getInt("value")));
		}
		
		for(int i=0; i<orders.size(); i++){		
			vertexes.add(orders.get(i).getOrderVertex());
			int k=0;
			JSONArray arr = null;
			if(i+1 <= orders.size()-1){					
				arr = tripService.getDistance(orders.get(i).getAddress(), prepareDestinationAddresses(i+1, orders));
			}
			for(int j=i+1; j<orders.size(); j++){				
				edges.add(new Edge(i+"", orders.get(i).getOrderVertex(), orders.get(j).getOrderVertex(), arr.getJSONObject(k).getJSONObject("distance").getInt("value")));
				k++;				
			}			
		}
		
		int numberOfNodes = vertexes.size();
		int adjacencyMatrix[][] = new int[numberOfNodes + 1][numberOfNodes + 1];
    	int m=0;
    	for(int i=1; i<=numberOfNodes; i++){
    		for(int j=1; j<=numberOfNodes; j++){
    			
    			if(i == j) {
    				adjacencyMatrix[i][j] = 0;
    			} else if (i > j) {
    				adjacencyMatrix[i][j] = adjacencyMatrix[j][i];
    			} else {
    				adjacencyMatrix[i][j] = edges.get(m).getWeight();
        			m++;
    			}
    		}
    	}
    	
    	for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = 1; j <= numberOfNodes; j++) {
                if (adjacencyMatrix[i][j] == 1 && adjacencyMatrix[j][i] == 0) {
                	adjacencyMatrix[j][i] = 1;
                }
            }
        }
    	
		TSPAlgo algorithm = new TSPAlgo();
		List<Integer> visitNodeIndexes = algorithm.applyTsp(adjacencyMatrix);
		visitNodeIndexes.remove(0);
		visitNodeIndexes.remove(0);
		Collections.reverse(visitNodeIndexes);
	
		visitNodeIndexes.forEach(idx -> {
			sortedOrders.add(orders.get(idx -2));
		});
		
		Map<Integer, Double> distanceMatrixMap = new HashMap<Integer,Double>();
		distanceMatrixMap.put(0, 0.0);
		distanceMatrixMap.put(1, (double) adjacencyMatrix[1][visitNodeIndexes.get(0)]/1000);
		for(int k=0; k<=visitNodeIndexes.size()-2; k++){
				double relativeDistance = adjacencyMatrix[visitNodeIndexes.get(k)][visitNodeIndexes.get(k+1)];
				distanceMatrixMap.put(k+2, relativeDistance/1000);
				orderTripDistance += relativeDistance; 
		}
		
		orderTripDistance += adjacencyMatrix[1][visitNodeIndexes.get(0)];
		
		dealerDeliveryTO.setDistanceMap(distanceMatrixMap);
		dealerDeliveryTO.setOrderList(sortedOrders);
		dealerDeliveryTO.setOrderTripDistance(orderTripDistance);
	}
	
	public DealerDeliveryTO processDijakstra(String sourceAddress, List<OrderTO> orders) throws BusinessException {
		DealerDeliveryTO dealerDeliveryTO = new DealerDeliveryTO();
		dealerDeliveryTO.setOrderList(orders);
		processDijakstra(sourceAddress, dealerDeliveryTO);
		return dealerDeliveryTO;
	}
	
	public String prepareDestinationAddresses(int initIdx, List<OrderTO> orders){
		StringBuilder addressBuilder = new StringBuilder();
		if(initIdx <= orders.size()-1){
			for(int j=initIdx; j<orders.size(); j++){
				addressBuilder.append(orders.get(j).getAddress()).append("|");
			}
			addressBuilder.deleteCharAt(addressBuilder.length()-1);
		}	
		
		return addressBuilder.toString();		
	}
	
}