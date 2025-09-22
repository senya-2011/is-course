package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service для машин.
 */
@Service
public class CarService {
  private final CarRepository carRepository;
  private final HumanBeingRepository humanBeingRepository;
  private final SseService sseService;

  /**
   * Конструктор.
   *
   * @param carRepository car repository
   * @param humanBeingRepository human being repository
   * @param sseService SSE service
   */
  public CarService(CarRepository carRepository, HumanBeingRepository humanBeingRepository,
      SseService sseService) {
    this.carRepository = carRepository;
    this.humanBeingRepository = humanBeingRepository;
    this.sseService = sseService;
  }

  /**
   * Найти машину по Id.
   *
   * @param id ид
   * @return найденная машина
   */
  public Optional<Car> findById(Long id) {
    return carRepository.findById(id);
  }

  /**
   * Найти машину по имени.
   *
   * @param name имя
   * @return найденная машина
   */
  public Optional<Car> findByName(String name) {
    return carRepository.findByName(name);
  }

  /**
   * Создает машину.
   *
   * @param car машина
   * @return созданную машину
   */
  public Car create(Car car) {
    Car saved = carRepository.save(car);
    sseService.broadcast("data_changed");
    return saved;
  }

  /**
   * Сохраняет машину.
   *
   * @param car машина
   * @return сохраненная машина
   */
  public Car save(Car car) {
    return carRepository.save(car);
  }

  /**
   * Обновляем машину.
   *
   * @param id ид
   * @param updated обновленная машина
   * @return обновленная машина
   */
  public Car update(Long id, Car updated) {
    Car existing = carRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Car not found: " + id));

    existing.setName(updated.getName());
    existing.setCool(updated.isCool());

    Car saved = carRepository.save(existing);
    sseService.broadcast("data_changed");
    return saved;
  }

  /**
   * Найти все машины.
   *
   * @return лист из всех машин
   */
  public List<Car> findAll() {
    return carRepository.findAll();
  }

  /**
   * Удалить машину если она не используется.
   *
   * @param id ид машины для удаления
   */
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
