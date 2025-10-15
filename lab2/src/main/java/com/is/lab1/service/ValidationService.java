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
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  private final HumanBeingRepository humanRepo;

  public ValidationService(HumanBeingRepository humanRepo) {
    this.humanRepo = humanRepo;
  }

  public void validateHuman(HumanBeing hb) {
    if (hb == null) throw new BusinessValidationException("human is null");
    ensureUniqueHumanName(hb.getName());
    ensureUniqueSoundtrack(hb.getSoundtrackName());
    ensureUniqueCoordinates(hb.getCoordinates());
    ensureCarOwnersLimit(hb.getCar());
  }

  public void validateHumanUpdate(HumanBeing existing, HumanBeing updated) {
    if (existing == null || updated == null) throw new BusinessValidationException("human is null");
    if (!equalsIgnoreCaseTrim(existing.getName(), updated.getName())) {
      ensureUniqueHumanName(updated.getName());
    }
    if (!equalsIgnoreCaseTrim(existing.getSoundtrackName(), updated.getSoundtrackName())) {
      ensureUniqueSoundtrack(updated.getSoundtrackName());
    }
    if (!coordsEqual(existing.getCoordinates(), updated.getCoordinates())) {
      ensureUniqueCoordinates(updated.getCoordinates());
    }
    String oldCarName = existing.getCar() != null ? existing.getCar().getName() : null;
    String newCarName = updated.getCar() != null ? updated.getCar().getName() : null;
    if (!equalsIgnoreCaseTrim(oldCarName, newCarName)) {
      ensureCarOwnersLimit(updated.getCar());
    }
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
}


