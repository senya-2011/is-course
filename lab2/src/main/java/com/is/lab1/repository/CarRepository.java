package com.is.lab1.repository;

import com.is.lab1.data.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
  Optional<Car> findByName(String name);
  boolean existsByNameIgnoreCase(String name);
}
