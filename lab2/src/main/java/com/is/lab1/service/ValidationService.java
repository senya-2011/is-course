package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.exception.BusinessValidationException;
import com.is.lab1.repository.HumanBeingRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  private final HumanBeingRepository humanRepo;
  private final GeolocationService geolocationService;

  public ValidationService(HumanBeingRepository humanRepo, GeolocationService geolocationService) {
    this.humanRepo = humanRepo;
    this.geolocationService = geolocationService;
  }

  public void validateHuman(HumanBeing hb) {
    if (hb == null) throw new BusinessValidationException("human is null");
    ensureUniqueHumanName(hb.getName());
    ensureUniqueSoundtrack(hb.getSoundtrackName());
    ensureUniqueCoordinates(hb.getCoordinates());
    ensureCarOwnersLimit(hb.getCar());
  }

  public void validateHuman(HumanBeing hb, String userIp) {
    if (hb == null) throw new BusinessValidationException("human is null");
    ensureUniqueHumanName(hb.getName());
    ensureUniqueSoundtrack(hb.getSoundtrackName());
    ensureUniqueCoordinates(hb.getCoordinates());
    ensureCarOwnersLimit(hb.getCar());
    ensureNotUserCityCoordinates(hb.getCoordinates(), userIp);
    ensureRageAllowedByTime(hb, userIp);
  }

  public void validateHumanUpdate(HumanBeing existing, HumanBeing updated) {
    if (existing == null || updated == null) throw new BusinessValidationException("human is null");
    if (!equalsIgnoreCaseTrim(existing.getName(), updated.getName())) {
      ensureUniqueHumanName(updated.getName());
    } else {
      ensureUniqueHumanNameExcluding(updated.getName(), existing.getId());
    }

    if (!equalsIgnoreCaseTrim(existing.getSoundtrackName(), updated.getSoundtrackName())) {
      ensureUniqueSoundtrack(updated.getSoundtrackName());
    } else {
      ensureUniqueSoundtrackExcluding(updated.getSoundtrackName(), existing.getId());
    }
    if (!coordsEqual(existing.getCoordinates(), updated.getCoordinates())) {
      ensureUniqueCoordinates(updated.getCoordinates());
    } else {
      ensureUniqueCoordinatesExcluding(updated.getCoordinates(), existing.getId());
    }
    String oldCarName = existing.getCar() != null ? existing.getCar().getName() : null;
    String newCarName = updated.getCar() != null ? updated.getCar().getName() : null;
    if (!equalsIgnoreCaseTrim(oldCarName, newCarName)) {
      ensureCarOwnersLimit(updated.getCar());
    }

    ensureRageAllowedByTime(updated, null); 
  }

  public void validateBatch(List<HumanBeing> humans) {
    if (humans == null || humans.isEmpty()) return;

    Map<String, Integer> nameCounts = new HashMap<>();
    Map<String, Integer> soundtrackCounts = new HashMap<>();
    Map<String, Integer> coordCounts = new HashMap<>();
    Map<String, Integer> carOwnerCounts = new HashMap<>();

    for (HumanBeing hb : humans) {
      if (hb.getName() != null) {
        String key = hb.getName().toLowerCase(Locale.ROOT).trim();
        nameCounts.put(key, nameCounts.getOrDefault(key, 0) + 1);
      }
      if (hb.getSoundtrackName() != null) {
        String key = hb.getSoundtrackName().toLowerCase(Locale.ROOT).trim();
        soundtrackCounts.put(key, soundtrackCounts.getOrDefault(key, 0) + 1);
      }
      if (hb.getCoordinates() != null) {
        Coordinates c = hb.getCoordinates();
        String key = c.getCoordX() + ":" + c.getCoordY();
        coordCounts.put(key, coordCounts.getOrDefault(key, 0) + 1);
      }
      if (hb.getCar() != null) {
        Car car = hb.getCar();
        String key = car.getName() == null ? ("#noname:" + System.identityHashCode(car)) : car.getName().toLowerCase(Locale.ROOT).trim();
        carOwnerCounts.put(key, carOwnerCounts.getOrDefault(key, 0) + 1);
      }
    }

    String dupNames = nameCounts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.joining(", "));
    if (!dupNames.isEmpty()) throw new BusinessValidationException("duplicate names in file: " + dupNames);

    String dupTracks = soundtrackCounts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.joining(", "));
    if (!dupTracks.isEmpty()) throw new BusinessValidationException("duplicate soundtracks in file: " + dupTracks);

    String dupCoords = coordCounts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.joining(", "));
    if (!dupCoords.isEmpty()) throw new BusinessValidationException("duplicate coordinates in file: " + dupCoords);

    for (HumanBeing hb : humans) {
      ensureUniqueHumanName(hb.getName());
      ensureUniqueSoundtrack(hb.getSoundtrackName());
      ensureUniqueCoordinates(hb.getCoordinates());
      ensureCarOwnersLimit(hb.getCar());
    }
  }

  private void ensureUniqueHumanName(String name) {
    if (name == null || name.trim().isEmpty()) return;
    if (humanRepo.existsByNameIgnoreCase(name)) {
      throw new BusinessValidationException("name already exists: " + name);
    }
  }

  private void ensureUniqueSoundtrack(String soundtrack) {
    if (soundtrack == null || soundtrack.trim().isEmpty()) return;
    if (humanRepo.existsBySoundtrackNameIgnoreCase(soundtrack)) {
      throw new BusinessValidationException("soundtrack already exists: " + soundtrack);
    }
  }

  private void ensureUniqueCoordinates(Coordinates coordinates) {
    if (coordinates == null) return;
    Float x = coordinates.getCoordX();
    Float y = coordinates.getCoordY();
    if (x == null || y == null) return;
    if (humanRepo.countByCoordinates(x, y) > 0) {
      throw new BusinessValidationException("coordinates already exist: (" + x + ", " + y + ")");
    }
  }

  private void ensureCarOwnersLimit(Car car) {
    if (car == null) return;
    String name = car.getName();
    if (name == null || name.trim().isEmpty()) return; 
    long owners = humanRepo.countByCarName(name);
    if (owners >= 4) {
      throw new BusinessValidationException("car already has 4 owners: " + name);
    }
  }

  private static boolean equalsIgnoreCaseTrim(String a, String b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    return a.trim().equalsIgnoreCase(b.trim());
  }

  private static boolean coordsEqual(Coordinates c1, Coordinates c2) {
    if (c1 == null && c2 == null) return true;
    if (c1 == null || c2 == null) return false;
    return safeEq(c1.getCoordX(), c2.getCoordX()) && safeEq(c1.getCoordY(), c2.getCoordY());
  }

  private static boolean safeEq(Float a, Float b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    return a.equals(b);
  }

  private void ensureNotUserCityCoordinates(Coordinates coordinates, String userIp) {
    if (coordinates == null) return;

    Float humanX = coordinates.getCoordX();
    Float humanY = coordinates.getCoordY();
    if (humanX == null || humanY == null) return;

    boolean ipMissing = (userIp == null || userIp.trim().isEmpty());
    Optional<GeolocationService.CityCoordinates> cityCoords = ipMissing ? Optional.empty() : geolocationService.getCityCoordinates(userIp);

    if (cityCoords.isEmpty()) {
      if (Math.abs(humanX) <= 10.0 && Math.abs(humanY) <= 10.0) {
        throw new BusinessValidationException("coordinates near origin are forbidden in local mode (|x|,|y| <= 10)");
      }
      return;
    }

    GeolocationService.CityCoordinates userCity = cityCoords.get();
    double tolerance = 0.001; 
    boolean matchesCity = Math.abs(humanX - userCity.getLatitude()) < tolerance && 
                         Math.abs(humanY - userCity.getLongitude()) < tolerance;
    
    if (matchesCity) {
      throw new BusinessValidationException(
        String.format("Coordinates (%.3f, %.3f) cannot be the same as your city %s (%.3f, %.3f)", 
          humanX, humanY, userCity.getCityName(), userCity.getLatitude(), userCity.getLongitude())
      );
    }
  }

  private void ensureRageAllowedByTime(HumanBeing hb, String userIp) {
    if (hb == null || hb.getMood() == null) return;
    if (!"RAGE".equalsIgnoreCase(hb.getMood().name())) return;

    java.time.ZoneId zoneId = null;
    if (userIp != null && !userIp.trim().isEmpty()) {
      Optional<GeolocationService.CityCoordinates> cityCoords = geolocationService.getCityCoordinates(userIp);
      if (cityCoords.isPresent() && cityCoords.get().getTimezone() != null && !cityCoords.get().getTimezone().isEmpty()) {
        try { zoneId = java.time.ZoneId.of(cityCoords.get().getTimezone()); } catch (Exception ignored) {}
      }
    }
    if (zoneId == null) {
      zoneId = java.time.ZoneId.systemDefault();
    }

    java.time.LocalTime now = java.time.ZonedDateTime.now(zoneId).toLocalTime();
    boolean inForbidden = !now.isBefore(java.time.LocalTime.of(19, 0)) || now.isBefore(java.time.LocalTime.of(10, 0));
    if (inForbidden) {
      throw new BusinessValidationException("mood=RAGE is forbidden between 19:00 and 10:00 local time");
    }
  }

  private void ensureUniqueHumanNameExcluding(String name, Long excludeId) {
    if (name == null || name.trim().isEmpty()) return;
    List<HumanBeing> existing = humanRepo.findByNameIgnoreCase(name);
    if (existing.stream().anyMatch(h -> !h.getId().equals(excludeId))) {
      throw new BusinessValidationException("name already exists: " + name);
    }
  }

  private void ensureUniqueSoundtrackExcluding(String soundtrack, Long excludeId) {
    if (soundtrack == null || soundtrack.trim().isEmpty()) return;
    List<HumanBeing> existing = humanRepo.findBySoundtrackNameIgnoreCase(soundtrack);
    if (existing.stream().anyMatch(h -> !h.getId().equals(excludeId))) {
      throw new BusinessValidationException("soundtrack already exists: " + soundtrack);
    }
  }

  private void ensureUniqueCoordinatesExcluding(Coordinates coordinates, Long excludeId) {
    if (coordinates == null || coordinates.getCoordX() == null || coordinates.getCoordY() == null) return;
    List<HumanBeing> existing = humanRepo.findByCoordinates(coordinates.getCoordX(), coordinates.getCoordY());
    if (existing.stream().anyMatch(h -> !h.getId().equals(excludeId))) {
      throw new BusinessValidationException("coordinates already exist: (" + coordinates.getCoordX() + ", " + coordinates.getCoordY() + ")");
    }
  }
}


