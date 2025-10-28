package com.is.lab1.service;

import com.is.lab1.data.*;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class HumanBeingServiceTests {

    @Autowired
    private HumanBeingService humanService;

    @Autowired
    private HumanBeingRepository humanRepo;

    @Autowired
    private CarRepository carRepo;

    private HumanBeing saveSample(String name, boolean realHero, Boolean hasToothpick, String carName, float speed) {
        Car car = carRepo.save(new Car(carName, false));
        HumanBeing h = new HumanBeing(name, new Coordinates(1f, 2f), realHero, hasToothpick,
                car, Mood.CALM, speed, "OST", WeaponType.AXE);
        return humanRepo.save(h);
    }

    @BeforeEach
    void cleanDatabase() {
        humanRepo.deleteAll();
        carRepo.deleteAll();
    }

    @Test
    @DisplayName("sumImpactSpeed sums all speeds")
    void sumImpactSpeed_works() {
        saveSample("A", true, false, "Lada", 3f);
        saveSample("B", true, true, "Lada", 7f);
        assertThat(humanService.sumImpactSpeed()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("deleteHeroesWithoutToothpick deletes only heroes without toothpick")
    void deleteHeroesWithoutToothpick_works() {
        saveSample("H1", true, false, "", 1f);
        saveSample("H2", true, true, "", 1f);
        saveSample("H3", false, false, "", 1f);
        int removed = humanService.deleteHeroesWithoutToothpick();
        assertThat(removed).isEqualTo(1);
    }

    @Test
    @DisplayName("reassignHeroesWithoutCarToLada assigns Lada Kalina (red)")
    void reassignHeroesWithoutCarToLada_assigns() {
        Car emptyNameCar = carRepo.save(new Car("", false));
        Car nullNameCar = carRepo.save(new Car(null, false));
        HumanBeing h1 = new HumanBeing("R1", new Coordinates(1f, 2f), true, false, nullNameCar, Mood.CALM, 1f, "s", WeaponType.AXE);
        HumanBeing h2 = new HumanBeing("R2", new Coordinates(1f, 2f), true, false, emptyNameCar, Mood.CALM, 1f, "s", WeaponType.AXE);
        HumanBeing h3 = new HumanBeing("R3", new Coordinates(1f, 2f), false, false, emptyNameCar, Mood.CALM, 1f, "s", WeaponType.AXE);
        humanRepo.save(h1);
        humanRepo.save(h2);
        humanRepo.save(h3);
        int changed = humanService.reassignHeroesWithoutCarToLada();
        assertThat(changed).isEqualTo(2);
        assertThat(humanRepo.findAll()).filteredOn(h -> "Lada Kalina (red)".equals(h.getCar().getName())).hasSize(2);
    }
}


