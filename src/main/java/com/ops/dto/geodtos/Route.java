
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
    "bounds",
    "copyrights",
    "legs",
    "overview_polyline",
    "summary",
    "warnings",
    "waypoint_order"
})
public class Route {

    @JsonProperty("bounds")
    private Bounds bounds;
    @JsonProperty("copyrights")
    private String copyrights;
    @JsonProperty("legs")
    private List<Leg> legs = new ArrayList<Leg>();
    @JsonProperty("overview_polyline")
    private OverviewPolyline overviewPolyline;
    @JsonProperty("summary")
    private String summary;
    @JsonProperty("warnings")
    private List<Object> warnings = new ArrayList<Object>();
    @JsonProperty("waypoint_order")
    private List<Integer> waypointOrder = new ArrayList<Integer>();

    /**
     * 
     * @return
     *     The bounds
     */
    @JsonProperty("bounds")
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * 
     * @param bounds
     *     The bounds
     */
    @JsonProperty("bounds")
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    /**
     * 
     * @return
     *     The copyrights
     */
    @JsonProperty("copyrights")
    public String getCopyrights() {
        return copyrights;
    }

    /**
     * 
     * @param copyrights
     *     The copyrights
     */
    @JsonProperty("copyrights")
    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    /**
     * 
     * @return
     *     The legs
     */
    @JsonProperty("legs")
    public List<Leg> getLegs() {
        return legs;
    }

    /**
     * 
     * @param legs
     *     The legs
     */
    @JsonProperty("legs")
    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    /**
     * 
     * @return
     *     The overviewPolyline
     */
    @JsonProperty("overview_polyline")
    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    /**
     * 
     * @param overviewPolyline
     *     The overview_polyline
     */
    @JsonProperty("overview_polyline")
    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    /**
     * 
     * @return
     *     The summary
     */
    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    /**
     * 
     * @param summary
     *     The summary
     */
    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * 
     * @return
     *     The warnings
     */
    @JsonProperty("warnings")
    public List<Object> getWarnings() {
        return warnings;
    }

    /**
     * 
     * @param warnings
     *     The warnings
     */
    @JsonProperty("warnings")
    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    /**
     * 
     * @return
     *     The waypointOrder
     */
    @JsonProperty("waypoint_order")
    public List<Integer> getWaypointOrder() {
        return waypointOrder;
    }

    /**
     * 
     * @param waypointOrder
     *     The waypoint_order
     */
    @JsonProperty("waypoint_order")
    public void setWaypointOrder(List<Integer> waypointOrder) {
        this.waypointOrder = waypointOrder;
    }

}
