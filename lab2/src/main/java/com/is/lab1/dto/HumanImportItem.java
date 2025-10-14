package com.is.lab1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMood() {
    return mood;
  }

  public void setMood(String mood) {
    this.mood = mood;
  }

  public String getSoundtrack() {
    return soundtrack;
  }

  public void setSoundtrack(String soundtrack) {
    this.soundtrack = soundtrack;
  }

  public Float getImpactSpeed() {
    return impactSpeed;
  }

  public void setImpactSpeed(Float impactSpeed) {
    this.impactSpeed = impactSpeed;
  }

  public String getWeaponType() {
    return weaponType;
  }

  public void setWeaponType(String weaponType) {
    this.weaponType = weaponType;
  }

  public Boolean getRealHero() {
    return realHero;
  }

  public void setRealHero(Boolean realHero) {
    this.realHero = realHero;
  }

  public Boolean getHasToothpick() {
    return hasToothpick;
  }

  public void setHasToothpick(Boolean hasToothpick) {
    this.hasToothpick = hasToothpick;
  }

  public CoordinatesPayload getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(CoordinatesPayload coordinates) {
    this.coordinates = coordinates;
  }

  public CarPayload getCar() {
    return car;
  }

  public void setCar(CarPayload car) {
    this.car = car;
  }

  public static class CoordinatesPayload {
    @JsonProperty("x")
    private Float x;

    @JsonProperty("y")
    private Float y;

    public Float getX() {
      return x;
    }

    public void setX(Float x) {
      this.x = x;
    }

    public Float getY() {
      return y;
    }

    public void setY(Float y) {
      this.y = y;
    }
  }

  public static class CarPayload {
    @JsonProperty("name")
    private String name;

    @JsonProperty("cool")
    private Boolean cool;

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
}


