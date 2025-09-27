package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.data.Mood;
import com.is.lab1.data.WeaponType;
import com.is.lab1.service.CarService;
import com.is.lab1.service.HumanBeingService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/humans")
public class HumanController {

  private final HumanBeingService humanService;
  private final CarService carService;

  public HumanController(HumanBeingService humanService, CarService carService) {
    this.humanService = humanService;
    this.carService = carService;
  }

  @PostMapping
  public String createHuman(@RequestParam String name,
      @RequestParam Float coordinatesX,
      @RequestParam Float coordinatesY,
      @RequestParam(defaultValue = "false") Boolean realHero,
      @RequestParam(required = false) Boolean hasToothpick,
      @RequestParam(required = false) Long carId,
      @RequestParam(required = false) String mood,
      @RequestParam Float impactSpeed,
      @RequestParam String weaponType,
      @RequestParam String soundtrackName) {
    try {
      if (coordinatesY != null && coordinatesY > 818f) {
        throw new IllegalArgumentException("Y must be <= 818");
      }
      if (impactSpeed != null && impactSpeed < 0f) {
        throw new IllegalArgumentException("Impact speed must be >= 0");
      }

      HumanBeing human = new HumanBeing();
      human.setName(name);
      human.setCoordinates(new Coordinates(coordinatesX, coordinatesY));
      human.setRealHero(Boolean.TRUE.equals(realHero));
      human.setHasToothpick(hasToothpick);

      Car car;
      if (carId != null) {
        car = carService.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("Car not found"));
      } else {
        car = new Car();
        car.setName("Default Car");
        car.setCool(false);
        car = carService.create(car);
      }
      human.setCar(car);

      human.setMood(mood != null && !mood.isEmpty() ? Mood.valueOf(mood) : null);
      human.setImpactSpeed(impactSpeed);
      human.setWeaponType(WeaponType.valueOf(weaponType));
      human.setSoundtrackName(soundtrackName);

      humanService.create(human);
      return "redirect:/";
    } catch (Exception ex) {
      String msg = ex.getMessage() == null ? "Create failed" : ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    }
  }

  @PostMapping("/{id}/delete")
  public String deleteHuman(@PathVariable Long id) {
    try {
      humanService.delete(id);
      return "redirect:/";
    } catch (Exception ex) {
      String msg = ex.getMessage() == null ? "Delete failed" : ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    }
  }

  @GetMapping("/{id}/edit")
  public String editHumanForm(@PathVariable Long id, Model model) {
    HumanBeing human = humanService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Human not found"));
    List<Car> cars = carService.findAll();

    model.addAttribute("human", human);
    model.addAttribute("cars", cars);
    model.addAttribute("isEdit", true);
    model.addAttribute("isEditCar", false);
    model.addAttribute("currentPage", 0);
    model.addAttribute("totalPages", 1);
    model.addAttribute("hasNext", false);
    model.addAttribute("hasPrev", false);
    model.addAttribute("searchQuery", "");

    return "index";
  }

  @PostMapping("/{id}/update")
  public String updateHuman(@PathVariable Long id,
      @RequestParam String name,
      @RequestParam Float coordinatesX,
      @RequestParam Float coordinatesY,
      @RequestParam(defaultValue = "false") Boolean realHero,
      @RequestParam(required = false) Boolean hasToothpick,
      @RequestParam(required = false) Long carId,
      @RequestParam(required = false) String mood,
      @RequestParam Float impactSpeed,
      @RequestParam String weaponType,
      @RequestParam String soundtrackName) {

    HumanBeing human = humanService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Human not found"));

    human.setName(name);
    human.setCoordinates(new Coordinates(coordinatesX, coordinatesY));
    human.setRealHero(realHero);
    human.setHasToothpick(hasToothpick);

    Car car;
    if (carId != null) {
      car = carService.findById(carId)
          .orElseThrow(() -> new IllegalArgumentException("Car not found"));
    } else {
      car = new Car();
      car.setName("Default Car");
      car.setCool(false);
      car = carService.create(car);
    }
    human.setCar(car);

    human.setMood(mood != null && !mood.isEmpty() ? Mood.valueOf(mood) : null);
    human.setImpactSpeed(impactSpeed);
    human.setWeaponType(WeaponType.valueOf(weaponType));
    human.setSoundtrackName(soundtrackName);

    humanService.update(id, human);
    return "redirect:/";
  }

  @PostMapping("/sum-impact")
  public String sumImpact(Model model) {
    double sum = humanService.sumImpactSpeed();
    model.addAttribute("sumResult", sum);
    return "redirect:/?sum=" + sum;
  }

  @PostMapping("/delete-heroes-without-toothpick")
  public String deleteHeroesWithoutToothpick(Model model) {
    int removed = humanService.deleteHeroesWithoutToothpick();
    model.addAttribute("removedCount", removed);
    return "redirect:/?removed=" + removed;
  }

  @PostMapping("/assign-lada")
  public String assignLada(Model model) {
    int changed = humanService.reassignHeroesWithoutCarToLada();
    model.addAttribute("changedCount", changed);
    return "redirect:/?changed=" + changed;
  }
}
