
package com.ops.dto.geodtos;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "points"
})
public class Polyline {

    @JsonProperty("points")
    private String points;

    /**
     * 
     * @return
     *     The points
     */
    @JsonProperty("points")
    public String getPoints() {
        return points;
    }

    /**
     * 
     * @param points
     *     The points
     */
    @JsonProperty("points")
    public void setPoints(String points) {
        this.points = points;
    }

}
