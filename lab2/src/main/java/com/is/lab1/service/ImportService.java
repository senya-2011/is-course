package com.is.lab1.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.data.Mood;
import com.is.lab1.data.WeaponType;
import com.is.lab1.dto.HumanImportItem;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.exception.ImportProcessingException;
import com.is.lab1.exception.ImportValidationException;
import com.is.lab1.repository.HumanBeingRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {

  private final ObjectMapper objectMapper;
  private final HumanBeingRepository humanRepo;
  private final CarRepository carRepo;

  public ImportService(HumanBeingRepository humanRepo, CarRepository carRepo) {
    this.objectMapper = new ObjectMapper();
    this.humanRepo = humanRepo;
    this.carRepo = carRepo;
  }

  @Transactional(rollbackFor = Exception.class)
  public int importHumans(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ImportValidationException("file is empty");
    }

    final List<HumanImportItem> items;
    try {
      items = objectMapper.readValue(
          file.getInputStream(), new TypeReference<List<HumanImportItem>>() {});
    } catch (IOException e) {
      throw new ImportProcessingException("failed to parse json", e);
    }

    if (items == null || items.isEmpty()) {
      return 0;
    }

    List<HumanBeing> toSave = new ArrayList<>();
    for (HumanImportItem item : items) {
      HumanBeing hb = mapToHuman(item);
      toSave.add(hb);
    }

    humanRepo.saveAll(toSave);
    return toSave.size();
  }

  private HumanBeing mapToHuman(HumanImportItem item) {
    if (item == null) {
      throw new ImportValidationException("item is null");
    }

    String name = requireNonBlank(item.getName(), "name");
    HumanImportItem.CoordinatesPayload cp = requireNonNull(item.getCoordinates(), "coordinates");
    Float x = requireNonNull(cp.getX(), "coordinates.x");
    Float y = requireNonNull(cp.getY(), "coordinates.y");

    HumanImportItem.CarPayload carPayload = requireNonNull(item.getCar(), "car");
    Boolean cool = carPayload.getCool() != null ? carPayload.getCool() : Boolean.FALSE;
    String carName = carPayload.getName();

    Car car = resolveOrCreateCar(carName, cool);

    Float impact = requireNonNull(item.getImpactSpeed(), "impactSpeed");
    if (impact < 0f) {
      throw new ImportValidationException("impactSpeed must be >= 0");
    }

    String soundtrack = requireNonBlank(item.getSoundtrack(), "soundtrack");

    String weaponTypeStr = requireNonBlank(item.getWeaponType(), "weaponType");
    WeaponType weaponType = WeaponType.valueOf(weaponTypeStr);

    Mood mood = null;
    if (item.getMood() != null && !item.getMood().isEmpty()) {
      try {
        mood = Mood.valueOf(item.getMood());
      } catch (IllegalArgumentException e) {
        throw new ImportValidationException("invalid mood: " + item.getMood());
      }
    }

    boolean realHero = Boolean.TRUE.equals(item.getRealHero());
    Boolean hasToothpick = item.getHasToothpick();

    HumanBeing hb = new HumanBeing();
    hb.setName(name);
    hb.setCoordinates(new Coordinates(x, y));
    hb.setRealHero(realHero);
    hb.setHasToothpick(hasToothpick);
    hb.setCar(car);
    hb.setMood(mood);
    hb.setImpactSpeed(impact);
    hb.setSoundtrackName(soundtrack);
    hb.setWeaponType(weaponType);
    return hb;
  }

  private Car resolveOrCreateCar(String name, boolean cool) {
    if (name == null || name.trim().isEmpty()) {
      return carRepo.save(new Car(null, cool));
    }
    Optional<Car> existing = carRepo.findByName(name);
    if (existing.isPresent()) {
      Car car = existing.get();
      car.setCool(cool);
      return carRepo.save(car);
    }
    return carRepo.save(new Car(name, cool));
  }

  private static <T> T requireNonNull(T value, String field) {
    if (value == null) {
      throw new ImportValidationException(field + " is required");
    }
    return value;
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.trim().isEmpty()) {
      throw new ImportValidationException(field + " is required");
    }
    return value;
  }
}


