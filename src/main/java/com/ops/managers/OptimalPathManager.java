package com.ops.managers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
				.append("&waypoints=optimize:true")
				;
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
		originalDealersPool = dealerDeliveryTO.getDealerList();
		getOptimizedTrips(dealerDeliveryTO);
		tripService.generateTripRoutes(generatedTripsList);
		return generatedTripsList;
	}

	private void getOptimizedTrips(DealerDeliveryTO dealerDeliveryTO) throws BusinessException, ApplicationException {
		
		recursionIndex++;
		
		try{
			
			double workingHours = dealerDeliveryTO.getWorkingHours();		
			double dealersWaypointsDistance = tripService.calculateDealersWaypointDistance(dealerDeliveryTO.getDealerList());
			double ordersWaypointDistance = tripService.calculateOrdersWaypointDistance(dealerDeliveryTO.getOrderList());
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
				getOptimizedTrips(dealerDeliveryTO);						
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
					getOptimizedTrips(dealerDeliveryTO);
				}			
				
			}
			
		} catch (BusinessException be){
			throw be;
		} catch (Exception e){
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
	

}