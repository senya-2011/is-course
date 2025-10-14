package com.is.lab1.controller;

import com.is.lab1.service.ImportService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

  private final ImportService importService;

  public ImportController(ImportService importService) {
    this.importService = importService;
  }

  @PostMapping("/humans")
  public ResponseEntity<?> importHumans(@RequestParam("file") MultipartFile file) {
    int imported = importService.importHumans(file);
    return ResponseEntity.ok(Map.of("imported", imported));
  }
}


