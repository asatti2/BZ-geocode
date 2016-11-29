
package com.ops.dto.geodtos;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "distance",
    "duration",
    "end_location",
    "html_instructions",
    "polyline",
    "start_location",
    "travel_mode",
    "maneuver"
})
public class Step {

    @JsonProperty("distance")
    private Distance_ distance;
    @JsonProperty("duration")
    private Duration_ duration;
    @JsonProperty("end_location")
    private EndLocation_ endLocation;
    @JsonProperty("html_instructions")
    private String htmlInstructions;
    @JsonProperty("polyline")
    private Polyline polyline;
    @JsonProperty("start_location")
    private StartLocation_ startLocation;
    @JsonProperty("travel_mode")
    private String travelMode;
    @JsonProperty("maneuver")
    private String maneuver;

    /**
     * 
     * @return
     *     The distance
     */
    @JsonProperty("distance")
    public Distance_ getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    @JsonProperty("distance")
    public void setDistance(Distance_ distance) {
        this.distance = distance;
    }

    /**
     * 
     * @return
     *     The duration
     */
    @JsonProperty("duration")
    public Duration_ getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    @JsonProperty("duration")
    public void setDuration(Duration_ duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return
     *     The endLocation
     */
    @JsonProperty("end_location")
    public EndLocation_ getEndLocation() {
        return endLocation;
    }

    /**
     * 
     * @param endLocation
     *     The end_location
     */
    @JsonProperty("end_location")
    public void setEndLocation(EndLocation_ endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * 
     * @return
     *     The htmlInstructions
     */
    @JsonProperty("html_instructions")
    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    /**
     * 
     * @param htmlInstructions
     *     The html_instructions
     */
    @JsonProperty("html_instructions")
    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    /**
     * 
     * @return
     *     The polyline
     */
    @JsonProperty("polyline")
    public Polyline getPolyline() {
        return polyline;
    }

    /**
     * 
     * @param polyline
     *     The polyline
     */
    @JsonProperty("polyline")
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    /**
     * 
     * @return
     *     The startLocation
     */
    @JsonProperty("start_location")
    public StartLocation_ getStartLocation() {
        return startLocation;
    }

    /**
     * 
     * @param startLocation
     *     The start_location
     */
    @JsonProperty("start_location")
    public void setStartLocation(StartLocation_ startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * 
     * @return
     *     The travelMode
     */
    @JsonProperty("travel_mode")
    public String getTravelMode() {
        return travelMode;
    }

    /**
     * 
     * @param travelMode
     *     The travel_mode
     */
    @JsonProperty("travel_mode")
    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    /**
     * 
     * @return
     *     The maneuver
     */
    @JsonProperty("maneuver")
    public String getManeuver() {
        return maneuver;
    }

    /**
     * 
     * @param maneuver
     *     The maneuver
     */
    @JsonProperty("maneuver")
    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

}
