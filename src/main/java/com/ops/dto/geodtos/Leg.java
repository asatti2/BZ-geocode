
package com.ops.dto.geodtos;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "distance",
    "duration",
    "end_address",
    "end_location",
    "start_address",
    "start_location",
    "steps",
    "traffic_speed_entry",
    "via_waypoint"
})
public class Leg {

    @JsonProperty("distance")
    private Distance distance;
    @JsonProperty("duration")
    private Duration duration;
    @JsonProperty("end_address")
    private String endAddress;
    @JsonProperty("end_location")
    private EndLocation endLocation;
    @JsonProperty("start_address")
    private String startAddress;
    @JsonProperty("start_location")
    private StartLocation startLocation;
    @JsonProperty("steps")
    private List<Step> steps = new ArrayList<Step>();
    @JsonProperty("traffic_speed_entry")
    private List<Object> trafficSpeedEntry = new ArrayList<Object>();
    @JsonProperty("via_waypoint")
    private List<Object> viaWaypoint = new ArrayList<Object>();

    /**
     * 
     * @return
     *     The distance
     */
    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    @JsonProperty("distance")
    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    /**
     * 
     * @return
     *     The duration
     */
    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    @JsonProperty("duration")
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return
     *     The endAddress
     */
    @JsonProperty("end_address")
    public String getEndAddress() {
        return endAddress;
    }

    /**
     * 
     * @param endAddress
     *     The end_address
     */
    @JsonProperty("end_address")
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    /**
     * 
     * @return
     *     The endLocation
     */
    @JsonProperty("end_location")
    public EndLocation getEndLocation() {
        return endLocation;
    }

    /**
     * 
     * @param endLocation
     *     The end_location
     */
    @JsonProperty("end_location")
    public void setEndLocation(EndLocation endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * 
     * @return
     *     The startAddress
     */
    @JsonProperty("start_address")
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * 
     * @param startAddress
     *     The start_address
     */
    @JsonProperty("start_address")
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    /**
     * 
     * @return
     *     The startLocation
     */
    @JsonProperty("start_location")
    public StartLocation getStartLocation() {
        return startLocation;
    }

    /**
     * 
     * @param startLocation
     *     The start_location
     */
    @JsonProperty("start_location")
    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * 
     * @return
     *     The steps
     */
    @JsonProperty("steps")
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * 
     * @param steps
     *     The steps
     */
    @JsonProperty("steps")
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    /**
     * 
     * @return
     *     The trafficSpeedEntry
     */
    @JsonProperty("traffic_speed_entry")
    public List<Object> getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    /**
     * 
     * @param trafficSpeedEntry
     *     The traffic_speed_entry
     */
    @JsonProperty("traffic_speed_entry")
    public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }

    /**
     * 
     * @return
     *     The viaWaypoint
     */
    @JsonProperty("via_waypoint")
    public List<Object> getViaWaypoint() {
        return viaWaypoint;
    }

    /**
     * 
     * @param viaWaypoint
     *     The via_waypoint
     */
    @JsonProperty("via_waypoint")
    public void setViaWaypoint(List<Object> viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }

}
