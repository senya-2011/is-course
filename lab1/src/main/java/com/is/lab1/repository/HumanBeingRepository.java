package com.is.lab1.repository;

import com.is.lab1.data.HumanBeing;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface для Человека.
 */
public interface HumanBeingRepository extends JpaRepository<HumanBeing, Long>,
    JpaSpecificationExecutor<HumanBeing> {
  /**
   * Найти по подстроке в имени игнорируя регистр.
   *
   * @param substring подстрока
   * @return лист из людей совпадений
   */
  List<HumanBeing> findByNameContainingIgnoreCase(String substring);

  /**
   * Найти всех людей но в Page.
   *
   * @param pageable постранично
   * @return страница людей
   */
  Page<HumanBeing> findAll(Pageable pageable);

  /**
   * Есть ли люди с машиной по Ид.
   *
   * @param carId ИД машины
   * @return true если есть люди с такой машиной
   */
  boolean existsByCar_Id(Long carId);
}
