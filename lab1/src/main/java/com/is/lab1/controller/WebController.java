package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.service.CarService;
import com.is.lab1.service.HumanBeingService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

  private final HumanBeingService humanService;
  private final CarService carService;

  public WebController(HumanBeingService humanService, CarService carService) {
    this.humanService = humanService;
    this.carService = carService;
  }

  @GetMapping("/")
  public String index(Model model,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q) {
    Page<HumanBeing> humans = humanService.listAll(page, size, null, "DESC", q);
    List<Car> cars = carService.findAll();

    model.addAttribute("humans", humans.getContent());
    model.addAttribute("cars", cars);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", humans.getTotalPages());
    model.addAttribute("hasNext", humans.hasNext());
    model.addAttribute("hasPrev", humans.hasPrevious());
    model.addAttribute("searchQuery", q);

    return "index";
  }
}
