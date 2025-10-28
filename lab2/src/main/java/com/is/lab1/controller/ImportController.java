package com.is.lab1.controller;

import com.is.lab1.data.ImportOperation;
import com.is.lab1.service.ImportHistoryService;
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
  private final ImportHistoryService historyService;

  public ImportController(ImportService importService, ImportHistoryService historyService) {
    this.importService = importService;
    this.historyService = historyService;
  }

  @GetMapping
  public String importPage(Model model) {
    model.addAttribute("history", historyService.list());
    return "import";
  }

  @PostMapping("/humans")
  public ResponseEntity<?> importHumans(@RequestParam("file") MultipartFile file) {
    String filename = file != null ? file.getOriginalFilename() : null;
    ImportOperation op = historyService.start(filename);
    try {
      int imported = importService.importHumans(file);
      historyService.succeed(op.getId(), imported);
      return ResponseEntity.ok(Map.of("imported", imported));
    } catch (RuntimeException ex) {
      historyService.fail(op.getId(), ex.getMessage());
      throw ex;
    }
  }
}


