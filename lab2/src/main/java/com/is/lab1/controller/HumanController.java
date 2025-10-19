package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.data.Coordinates;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.data.Mood;
import com.is.lab1.data.WeaponType;
import com.is.lab1.exception.CarNotFoundException;
import com.is.lab1.exception.HumanBeingNotFoundException;
import com.is.lab1.service.CarService;
import com.is.lab1.service.HumanBeingService;
import com.is.lab1.service.GeolocationService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final GeolocationService geolocationService;

  public HumanController(HumanBeingService humanService, CarService carService, GeolocationService geolocationService) {
    this.humanService = humanService;
    this.carService = carService;
    this.geolocationService = geolocationService;
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
      @RequestParam String soundtrackName,
      HttpServletRequest request) {
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
            .orElseThrow(() -> new CarNotFoundException("Car with id " + carId + " not found"));
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

      System.out.println("[IP DEBUG] X-Forwarded-For=" + request.getHeader("X-Forwarded-For")
          + ", X-Real-IP=" + request.getHeader("X-Real-IP")
          + ", remoteAddr=" + request.getRemoteAddr());
      String userIp = getClientIpAddress(request);
      System.out.println("[IP DEBUG] resolved client IP=" + userIp);
      Optional<GeolocationService.CityCoordinates> cityCoords = geolocationService.getCityCoordinates(userIp);
      if (cityCoords.isPresent()) {
        GeolocationService.CityCoordinates cc = cityCoords.get();
        System.out.println("[CREATE] ip=" + userIp + ", userCity=(lat=" + cc.getLatitude() + ", lon=" + cc.getLongitude() + ", name=" + cc.getCityName() + ")" +
            ", submittedXY=(" + coordinatesX + ", " + coordinatesY + ")");
      } else {
        System.out.println("[CREATE] ip=" + userIp + ", userCity=unknown (local or lookup failed), submittedXY=(" + coordinatesX + ", " + coordinatesY + ")");
      }
      humanService.create(human, userIp);
      return "redirect:/";
    } catch (CarNotFoundException | HumanBeingNotFoundException ex) {
      String msg = ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    } catch (Exception ex) {
      String msg = ex.getMessage() == null ? "Create failed" : ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteHumanApi(@PathVariable Long id) {
    try {
      humanService.delete(id);
      return ResponseEntity.ok().body(Map.of("message", "HumanBeing deleted successfully"));
    } catch (HumanBeingNotFoundException ex) {
      return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error occurred"));
    }
  }

  @PostMapping("/{id}/delete")
  public String deleteHuman(@PathVariable Long id) {
    try {
      humanService.delete(id);
      return "redirect:/";
    } catch (HumanBeingNotFoundException ex) {
      String msg = ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    } catch (Exception ex) {
      String msg = ex.getMessage() == null ? "Delete failed" : ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    }
  }

  @GetMapping("/{id}/edit")
  public String editHumanForm(@PathVariable Long id, Model model) {
    HumanBeing human = humanService.findById(id)
        .orElseThrow(() -> new HumanBeingNotFoundException("HumanBeing with id " + id + " not found"));
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
      @RequestParam(name = "coordinates.coordX") Float coordinatesX,
      @RequestParam(name = "coordinates.coordY") Float coordinatesY,
      @RequestParam(defaultValue = "false") Boolean realHero,
      @RequestParam(required = false) Boolean hasToothpick,
      @RequestParam(name = "car.id", required = false) Long carId,
      @RequestParam(required = false) String mood,
      @RequestParam Float impactSpeed,
      @RequestParam String weaponType,
      @RequestParam String soundtrackName) {
    try {

      HumanBeing updatedHuman = new HumanBeing();
      updatedHuman.setName(name);
      updatedHuman.setCoordinates(new Coordinates(coordinatesX, coordinatesY));
      updatedHuman.setRealHero(realHero);
      updatedHuman.setHasToothpick(hasToothpick);

      Car car;
      if (carId != null) {
        car = carService.findById(carId)
            .orElseThrow(() -> new CarNotFoundException("Car with id " + carId + " not found"));
      } else {
        car = new Car();
        car.setName("Default Car");
        car.setCool(false);
        car = carService.create(car);
      }
      updatedHuman.setCar(car);

      updatedHuman.setMood(mood != null && !mood.isEmpty() ? Mood.valueOf(mood) : null);
      updatedHuman.setImpactSpeed(impactSpeed);
      updatedHuman.setWeaponType(WeaponType.valueOf(weaponType));
      updatedHuman.setSoundtrackName(soundtrackName);

      humanService.update(id, updatedHuman);
      return "redirect:/";
    } catch (CarNotFoundException | HumanBeingNotFoundException ex) {
      String msg = ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    } catch (Exception ex) {
      String msg = ex.getMessage() == null ? "Update failed" : ex.getMessage();
      String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
      return "redirect:/?error=" + encoded;
    }
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

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return stripPort(xForwardedFor.split(",")[0].trim());
    }
    
    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return stripPort(xRealIp);
    }
    
    return stripPort(request.getRemoteAddr());
  }

  private String stripPort(String ip) {
    if (ip == null) return null;
    if (ip.startsWith("[")) {
      int end = ip.indexOf(']');
      if (end > 0) return ip.substring(1, end);
    }
    int colon = ip.lastIndexOf(':');
    if (colon > -1 && ip.contains(".")) {
      return ip.substring(0, colon);
    }
    return ip;
  }
}
