
package com.ops.dto.geodtos;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "northeast",
    "southwest"
})
public class Bounds {

    @JsonProperty("northeast")
    private Northeast northeast;
    @JsonProperty("southwest")
    private Southwest southwest;

    /**
     * 
     * @return
     *     The northeast
     */
    @JsonProperty("northeast")
    public Northeast getNortheast() {
        return northeast;
    }

    /**
     * 
     * @param northeast
     *     The northeast
     */
    @JsonProperty("northeast")
    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    /**
     * 
     * @return
     *     The southwest
     */
    @JsonProperty("southwest")
    public Southwest getSouthwest() {
        return southwest;
    }

    /**
     * 
     * @param southwest
     *     The southwest
     */
    @JsonProperty("southwest")
    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

}
