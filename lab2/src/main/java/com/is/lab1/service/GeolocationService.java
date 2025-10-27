package com.is.lab1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class GeolocationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeolocationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<CityCoordinates> getCityCoordinates(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty() || isLocalAddress(ipAddress)) {
            return Optional.empty();
        }

        try {
            String url = "http://ip-api.com/json/" + ipAddress + "?fields=status,lat,lon,city,timezone";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode json = objectMapper.readTree(response);
                
                if ("success".equals(json.get("status").asText())) {
                    double lat = json.get("lat").asDouble();
                    double lon = json.get("lon").asDouble();
                    String city = json.get("city").asText();
                    String timezone = json.hasNonNull("timezone") ? json.get("timezone").asText() : null;
                    
                    return Optional.of(new CityCoordinates(lat, lon, city, timezone));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get geolocation for IP " + ipAddress + ": " + e.getMessage());
        }
        
        return Optional.empty();
    }

    private boolean isLocalAddress(String ip) {
        return ip.equals("127.0.0.1") || 
               ip.equals("localhost") || 
               ip.startsWith("192.168.") || 
               ip.startsWith("10.") || 
               ip.startsWith("172.");
    }

    public static class CityCoordinates {
        private final double latitude;
        private final double longitude;
        private final String cityName;
        private final String timezone;

        public CityCoordinates(double latitude, double longitude, String cityName, String timezone) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.cityName = cityName;
            this.timezone = timezone;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCityName() { return cityName; }
        public String getTimezone() { return timezone; }

        @Override
        public String toString() {
            return String.format("CityCoordinates{lat=%.6f, lon=%.6f, city='%s', timezone='%s'}", latitude, longitude, cityName, timezone);
        }
    }
}
