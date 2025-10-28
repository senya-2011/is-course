package com.is.lab1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HumanImportItem {

  @JsonProperty("name")
  private String name;

  @JsonProperty("mood")
  private String mood; 

  @JsonProperty("soundtrack")
  private String soundtrack;

  @JsonProperty("impactSpeed")
  private Float impactSpeed;

  @JsonProperty("weaponType")
  private String weaponType; 
  
  @JsonProperty("realHero")
  private Boolean realHero;

  @JsonProperty("hasToothpick")
  private Boolean hasToothpick;

  @JsonProperty("coordinates")
  private CoordinatesPayload coordinates;

  @JsonProperty("car")
  private CarPayload car;

  @Data
  public static class CoordinatesPayload {
    @JsonProperty("x")
    private Float x;

    @JsonProperty("y")
    private Float y;
  }

  @Data
  public static class CarPayload {
    @JsonProperty("name")
    private String name;

    @JsonProperty("cool")
    private Boolean cool;
  }
}


