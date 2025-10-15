package com.is.lab1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GeolocationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeolocationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get city coordinates by IP address using free IP geolocation API
     */
    public Optional<CityCoordinates> getCityCoordinates(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty() || isLocalAddress(ipAddress)) {
            return Optional.empty();
        }

        try {
            // Using ip-api.com (free, no API key required)
            String url = "http://ip-api.com/json/" + ipAddress + "?fields=status,lat,lon,city";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode json = objectMapper.readTree(response);
                
                if ("success".equals(json.get("status").asText())) {
                    double lat = json.get("lat").asDouble();
                    double lon = json.get("lon").asDouble();
                    String city = json.get("city").asText();
                    
                    return Optional.of(new CityCoordinates(lat, lon, city));
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to get geolocation for IP " + ipAddress + ": " + e.getMessage());
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

        public CityCoordinates(double latitude, double longitude, String cityName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.cityName = cityName;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCityName() { return cityName; }

        @Override
        public String toString() {
            return String.format("CityCoordinates{lat=%.6f, lon=%.6f, city='%s'}", latitude, longitude, cityName);
        }
    }
}
