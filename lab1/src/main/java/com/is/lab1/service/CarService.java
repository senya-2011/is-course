package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarService {
  private final CarRepository carRepository;
  private final HumanBeingRepository humanBeingRepository;
  private final SseService sseService;

  public CarService(CarRepository carRepository, HumanBeingRepository humanBeingRepository,
      SseService sseService) {
    this.carRepository = carRepository;
    this.humanBeingRepository = humanBeingRepository;
    this.sseService = sseService;
  }

  public Optional<Car> findById(Long id) {
    return carRepository.findById(id);
  }

  public Optional<Car> findByName(String name) {
    return carRepository.findByName(name);
  }

  public Car create(Car car) {
    Car saved = carRepository.save(car);
    sseService.broadcast("data_changed");
    return saved;
  }

  public Car save(Car car) {
    return carRepository.save(car);
  }

  public Car update(Long id, Car updated) {
    Car existing = carRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Car not found: " + id));

    existing.setName(updated.getName());
    existing.setCool(updated.isCool());

    Car saved = carRepository.save(existing);
    sseService.broadcast("data_changed");
    return saved;
  }

  public List<Car> findAll() {
    return carRepository.findAll();
  }

  @Transactional
  public void deleteIfUnused(Long id) {
    Car car = carRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Car not found: " + id));

    boolean used = humanBeingRepository.existsByCar_Id(id);
    if (used) {
      throw new IllegalArgumentException("Cannot delete car with id=" + id
          + ": it is referenced by human beings.");
    }

    carRepository.delete(car);
    sseService.broadcast("data_changed");
  }
}
