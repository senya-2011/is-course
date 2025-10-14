package com.is.lab1;

import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.data.Mood;
import com.is.lab1.data.WeaponType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LombokTest {

    @Test
    public void testCarLombok() {
        Car car = new Car();
        assertNotNull(car);

        Car carWithParams = new Car("BMW", true);
        assertEquals("BMW", carWithParams.getName());
        assertTrue(carWithParams.isCool());

        car.setName("Audi");
        car.setCool(false);
        assertEquals("Audi", car.getName());
        assertFalse(car.isCool());

        Car car1 = new Car("BMW", true);
        Car car2 = new Car("BMW", true);
        assertEquals(car1, car2);
        assertEquals(car1.hashCode(), car2.hashCode());

        String carString = car1.toString();
        assertTrue(carString.contains("BMW"));
        assertTrue(carString.contains("true"));
    }

    @Test
    public void testCoordinatesLombok() {
        Coordinates coords = new Coordinates();
        assertNotNull(coords);

        Coordinates coordsWithParams = new Coordinates(10.5f, 20.3f);
        assertEquals(10.5f, coordsWithParams.getCoordX());
        assertEquals(20.3f, coordsWithParams.getCoordY());

        coords.setCoordX(15.0f);
        coords.setCoordY(25.0f);
        assertEquals(15.0f, coords.getCoordX());
        assertEquals(25.0f, coords.getCoordY());

        Coordinates coords1 = new Coordinates(10.0f, 20.0f);
        Coordinates coords2 = new Coordinates(10.0f, 20.0f);
        assertEquals(coords1, coords2);
        assertEquals(coords1.hashCode(), coords2.hashCode());
    }

    @Test
    public void testHumanBeingLombok() {
        Car car = new Car("Test Car", true);
        Coordinates coords = new Coordinates(10.0f, 20.0f);

        HumanBeing human = new HumanBeing();
        assertNotNull(human);

        HumanBeing humanWithParams = new HumanBeing(
            "Test Human", coords, true, Boolean.TRUE, car, 
            Mood.CALM, 100.0f, "Test Soundtrack", WeaponType.HAMMER
        );
        
        assertEquals("Test Human", humanWithParams.getName());
        assertEquals(coords, humanWithParams.getCoordinates());
        assertTrue(humanWithParams.isRealHero());
        assertTrue(humanWithParams.getHasToothpick());
        assertEquals(car, humanWithParams.getCar());
        assertEquals(Mood.CALM, humanWithParams.getMood());
        assertEquals(100.0f, humanWithParams.getImpactSpeed());
        assertEquals("Test Soundtrack", humanWithParams.getSoundtrackName());
        assertEquals(WeaponType.HAMMER, humanWithParams.getWeaponType());

        human.setName("New Name");
        human.setCoordinates(coords);
        human.setRealHero(false);
        human.setHasToothpick(Boolean.FALSE);
        human.setCar(car);
        human.setMood(Mood.RAGE);
        human.setImpactSpeed(200.0f);
        human.setSoundtrackName("New Soundtrack");
        human.setWeaponType(WeaponType.AXE);
        
        assertEquals("New Name", human.getName());
        assertEquals(coords, human.getCoordinates());
        assertFalse(human.isRealHero());
        assertFalse(human.getHasToothpick());
        assertEquals(car, human.getCar());
        assertEquals(Mood.RAGE, human.getMood());
        assertEquals(200.0f, human.getImpactSpeed());
        assertEquals("New Soundtrack", human.getSoundtrackName());
        assertEquals(WeaponType.AXE, human.getWeaponType());

        HumanBeing human1 = new HumanBeing("Test", coords, true, Boolean.TRUE, car, 
            Mood.CALM, 100.0f, "Soundtrack", WeaponType.HAMMER);
        HumanBeing human2 = new HumanBeing("Test", coords, true, Boolean.TRUE, car, 
            Mood.CALM, 100.0f, "Soundtrack", WeaponType.HAMMER);
        
        assertEquals(human1, human2);
        assertEquals(human1.hashCode(), human2.hashCode());
    }
}
