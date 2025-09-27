package com.is.lab1.repository;

import com.is.lab1.data.HumanBeing;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HumanBeingRepository extends JpaRepository<HumanBeing, Long>,
    JpaSpecificationExecutor<HumanBeing> {
  List<HumanBeing> findByNameContainingIgnoreCase(String substring);

  Page<HumanBeing> findAll(Pageable pageable);

  boolean existsByCar_Id(Long carId);
}
