
package com.ops.dto.geodtos;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "lat",
    "lng"
})
public class EndLocation_ {

    @JsonProperty("lat")
    private Float lat;
    @JsonProperty("lng")
    private Float lng;

    /**
     * 
     * @return
     *     The lat
     */
    @JsonProperty("lat")
    public Float getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    @JsonProperty("lat")
    public void setLat(Float lat) {
        this.lat = lat;
    }

    /**
     * 
     * @return
     *     The lng
     */
    @JsonProperty("lng")
    public Float getLng() {
        return lng;
    }

    /**
     * 
     * @param lng
     *     The lng
     */
    @JsonProperty("lng")
    public void setLng(Float lng) {
        this.lng = lng;
    }

}
