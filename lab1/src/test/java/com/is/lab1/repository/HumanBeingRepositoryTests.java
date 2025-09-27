package com.is.lab1.repository;

import com.is.lab1.data.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class HumanBeingRepositoryTests {

    @Autowired
    private HumanBeingRepository humanRepo;

    @Autowired
    private CarRepository carRepo;

    private HumanBeing createSample(String name, String carName) {
        Car car = carRepo.save(new Car(carName, false));
        HumanBeing h = new HumanBeing(
                name,
                new Coordinates(1f, 2f),
                true,
                false,
                car,
                Mood.CALM,
                10.0f,
                "OST",
                WeaponType.AXE
        );
        return humanRepo.save(h);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase finds matches")
    void findByNameContainingIgnoreCase_finds() {
        createSample("Alice", "Lada");
        createSample("bob", "BMW");
        List<HumanBeing> res = humanRepo.findByNameContainingIgnoreCase("ali");
        assertThat(res).extracting(HumanBeing::getName).contains("Alice");
    }

    @Test
    @DisplayName("existsByCar_Id returns true when referenced")
    void existsByCarId_trueWhenUsed() {
        HumanBeing h = createSample("John", "Audi");
        boolean used = humanRepo.existsByCar_Id(h.getCar().getId());
        assertThat(used).isTrue();
    }
}


