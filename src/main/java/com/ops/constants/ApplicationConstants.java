package com.ops.constants;

public interface ApplicationConstants {

	/* ################URI_CONSTANTS############## */
	String SECURE_URI = "/secure";
	String TEST_URI = "/test";
	String WAYPOINT_URL = "/waypoint";
	String COST_CALCULATE_URL = "/costCalculate";
	String OPTIMAL_PATH = "/optimalTrips";

	String RESP_ERROR = "resp.error";
	String INCORRECT_ADDRESS = "incorrect.address";
	String NOT_FOUND = "address.not.geocoded";
	String ZERO_RESULTS = "no.route.found";
	String MAX_WAYPOINTS_EXCEEDED = "waypoints.exceed";
	String MAX_ROUTE_LENGTH_EXCEEDED = "route.length.exceed";
	String INVALID_REQUEST = "invalid.request";
	String OVER_QUERY_LIMIT = "too.many.requests";
	String REQUEST_DENIED = "request.denied";
	String UNKNOWN_ERROR = "server.error";
	String INVALID_WORKING_HOURS = "invalid.working.hours";
	String DEALER_ID_MISMATCH = "dealer.ids.mismatch";
	String MAX_ELEMENTS_EXCEEDED = "elements.exceed";
}