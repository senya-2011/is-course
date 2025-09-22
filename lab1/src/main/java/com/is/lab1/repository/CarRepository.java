package com.is.lab1.repository;

import com.is.lab1.data.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface для Машин.
 */
public interface CarRepository extends JpaRepository<Car, Long> {
  /**
   * Найти по имени.
   *
   * @param name имя
   * @return машину которую нашло
   */
  Optional<Car> findByName(String name);
}
