package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.dto.CarUpdateRequest;
import com.is.lab1.exception.CarCannotBeDeletedException;
import com.is.lab1.exception.CarNotFoundException;
import com.is.lab1.service.CarService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<?> deleteCarApi(@PathVariable Long id) {
    try {
      carService.deleteIfUnused(id);
      return ResponseEntity.ok().body(Map.of("message", "Car deleted successfully"));
    } catch (CarCannotBeDeletedException ex) {
      return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    } catch (CarNotFoundException ex) {
      return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error occurred"));
    }
  }

  @PostMapping("/{id}/delete")
  public String deleteCarWeb(@PathVariable Long id) {
    try {
      carService.deleteIfUnused(id);
      return "redirect:/";
    } catch (CarCannotBeDeletedException ex) {
      String msg = ex.getMessage();
      return "redirect:/?error=" + msg;
    } catch (CarNotFoundException ex) {
      String msg = ex.getMessage();
      return "redirect:/?error=" + msg;
    } catch (Exception ex) {
      String msg = "Internal server error occurred";
      return "redirect:/?error=" + msg;
    }
  }

  @GetMapping("/{id}/edit")
  public String editCarForm(@PathVariable Long id, Model model) {
    Car car = carService.findById(id)
            .orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));
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

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCarApi(@PathVariable Long id, @RequestBody CarUpdateRequest request) {
    try {
      Car car = carService.findById(id)
              .orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));

      if (request.getName() != null) {
        car.setName(request.getName().isEmpty() ? null : request.getName());
      }
      if (request.getCool() != null) {
        car.setCool(request.getCool());
      }

      Car updatedCar = carService.update(id, car);
      return ResponseEntity.ok().body(Map.of("message", "Car updated successfully", "car", updatedCar));
    } catch (CarNotFoundException ex) {
      return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error occurred"));
    }
  }

  @PostMapping("/{id}/update")
  public String updateCarWeb(@PathVariable Long id,
                            @RequestParam(required = false) String name,
                            @RequestParam(defaultValue = "false") Boolean cool) {
    try {
      Car car = carService.findById(id)
              .orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));

      car.setName(name != null && !name.isEmpty() ? name : null);
      car.setCool(cool);

      carService.update(id, car);
      return "redirect:/";
    } catch (CarNotFoundException ex) {
      String msg = ex.getMessage();
      return "redirect:/?error=" + msg;
    } catch (Exception ex) {
      String msg = "Internal server error occurred";
      return "redirect:/?error=" + msg;
    }
  }
}
