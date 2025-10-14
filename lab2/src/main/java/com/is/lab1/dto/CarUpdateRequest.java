package com.is.lab1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarUpdateRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("cool")
    private Boolean cool;
    
    public CarUpdateRequest() {}
    
    public CarUpdateRequest(String name, Boolean cool) {
        this.name = name;
        this.cool = cool;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getCool() {
        return cool;
    }
    
    public void setCool(Boolean cool) {
        this.cool = cool;
    }
}
