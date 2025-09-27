package com.is.lab1.repository;

import com.is.lab1.data.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CarRepositoryTests {

    @Autowired
    private CarRepository carRepository;

    @Test
    @DisplayName("findByName returns saved car by name")
    void findByName_returnsSaved() {
        carRepository.save(new Car("Lada", false));
        Optional<Car> found = carRepository.findByName("Lada");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Lada");
    }
}


