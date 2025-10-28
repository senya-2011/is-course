package com.is.lab1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CarUpdateRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("cool")
    private Boolean cool;
}
