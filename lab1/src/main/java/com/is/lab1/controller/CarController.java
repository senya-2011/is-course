package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.service.CarService;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cars")
public class CarController {

  private final CarService carService;

  public CarController(CarService carService) {
    this.carService = carService;
  }

  @PostMapping
  public String createCar(@RequestParam(required = false) String name,
                         @RequestParam(defaultValue = "false") Boolean cool) {
    Car car = new Car();
    car.setName(name != null && !name.isEmpty() ? name : null);
    car.setCool(cool != null ? cool : false);
    carService.create(car);
    return "redirect:/";
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCar(@PathVariable Long id) {
    try {
      carService.deleteIfUnused(id);
      return ResponseEntity.ok().body(Map.of("message", "Car deleted successfully"));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete car: " + ex.getMessage()));
    } catch (NoSuchElementException ex) {
      return ResponseEntity.status(404).body(Map.of("error", "Car not found with id: " + id));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error occurred"));
    }
  }

  @GetMapping("/{id}/edit")
  public String editCarForm(@PathVariable Long id, Model model) {
    Car car = carService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));
    List<Car> cars = carService.findAll();

    model.addAttribute("car", car);
    model.addAttribute("cars", cars);
    model.addAttribute("isEdit", false);
    model.addAttribute("isEditCar", true);
    model.addAttribute("currentPage", 0);
    model.addAttribute("totalPages", 1);
    model.addAttribute("hasNext", false);
    model.addAttribute("hasPrev", false);
    model.addAttribute("searchQuery", "");

    return "index";
  }

  @PostMapping("/{id}/update")
  public String updateCar(@PathVariable Long id,
                         @RequestParam(required = false) String name,
                         @RequestParam(defaultValue = "false") Boolean cool) {
    Car car = carService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));

    car.setName(name != null && !name.isEmpty() ? name : null);
    car.setCool(cool);

    carService.update(id, car);
    return "redirect:/";
  }
}
