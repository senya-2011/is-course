package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.data.Mood;
import com.is.lab1.data.WeaponType;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CarServiceTests {

    @Autowired
    private CarService carService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private HumanBeingRepository humanRepository;

    @Test
    @DisplayName("update updates name and cool")
    void update_updatesFields() {
        Car car = carRepository.save(new Car("A", false));
        Car updated = new Car("B", true);
        Car saved = carService.update(car.getId(), updated);
        assertThat(saved.getName()).isEqualTo("B");
        assertThat(saved.isCool()).isTrue();
    }

    @Test
    @DisplayName("update throws when not found")
    void update_throwsWhenMissing() {
        assertThatThrownBy(() -> carService.update(9999L, new Car("X", false)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("deleteIfUnused throws when car referenced by human")
    void deleteIfUnused_blocksWhenReferenced() {
        Car car = carRepository.save(new Car("C1", false));
        HumanBeing h = new HumanBeing("H", new Coordinates(1f,2f), true, false, car, Mood.CALM, 1f, "s", WeaponType.AXE);
        humanRepository.save(h);
        assertThatThrownBy(() -> carService.deleteIfUnused(car.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}


