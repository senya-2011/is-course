package com.is.lab1.service;

import com.is.lab1.data.ImportOperation;
import com.is.lab1.data.ImportStatus;
import com.is.lab1.repository.ImportOperationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportHistoryService {

  private final ImportOperationRepository repo;
  private final SseService sseService;

  public ImportHistoryService(ImportOperationRepository repo, SseService sseService) {
    this.repo = repo;
    this.sseService = sseService;
  }

  @Transactional
  public ImportOperation start(String filename) {
    ImportOperation op = ImportOperation.builder()
        .startedAt(LocalDateTime.now())
        .status(ImportStatus.PENDING)
        .payloadFilename(filename)
        .build();
    return repo.save(op);
  }

  @Transactional
  public void succeed(Long id, int count) {
    repo.findById(id).ifPresent(op -> {
      op.setCompletedAt(LocalDateTime.now());
      op.setStatus(ImportStatus.SUCCESS);
      op.setSuccessCount(count);
      repo.save(op);
      sseService.broadcast("data_changed");
    });
  }

  @Transactional
  public void fail(Long id, String message) {
    repo.findById(id).ifPresent(op -> {
      op.setCompletedAt(LocalDateTime.now());
      op.setStatus(ImportStatus.FAILED);
      op.setErrorMessage(message);
      repo.save(op);
      sseService.broadcast("data_changed");
    });
  }

  public List<ImportOperation> list() {
    return repo.findAllLatestFirst();
  }
}


