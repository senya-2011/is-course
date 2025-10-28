package com.is.lab1.repository;

import com.is.lab1.data.ImportOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ImportOperationRepository extends JpaRepository<ImportOperation, Long> {
  default List<ImportOperation> findAllLatestFirst() {
    return findAll(Sort.by(Sort.Direction.DESC, "startedAt"));
  }
}


