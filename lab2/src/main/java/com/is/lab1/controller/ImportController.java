package com.is.lab1.controller;

import com.is.lab1.service.ImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/import")
public class ImportController {

  private final ImportService importService;

  public ImportController(ImportService importService) {
    this.importService = importService;
  }

  @GetMapping
  public String importPage(Model model) {
    return "import";
  }

  @PostMapping("/humans")
  public ResponseEntity<?> importHumans(@RequestParam("file") MultipartFile file) {
    int imported = importService.importHumans(file);
    return ResponseEntity.ok(Map.of("imported", imported));
  }
}


