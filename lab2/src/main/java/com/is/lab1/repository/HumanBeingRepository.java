package com.is.lab1.repository;

import com.is.lab1.data.HumanBeing;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface HumanBeingRepository extends JpaRepository<HumanBeing, Long>,
    JpaSpecificationExecutor<HumanBeing> {
  List<HumanBeing> findByNameContainingIgnoreCase(String substring);

  Page<HumanBeing> findAll(Pageable pageable);

  boolean existsByCar_Id(Long carId);

  @Query("SELECT h FROM HumanBeing h WHERE ABS(h.impactSpeed - :value) <= :epsilon")
  List<HumanBeing> findByImpactSpeedWithEpsilon(@Param("value") float value, @Param("epsilon") double epsilon);

  @Query("SELECT SUM(h.impactSpeed) FROM HumanBeing h")
  Double sumImpactSpeed();

  @Query("SELECT COUNT(h) > 0 FROM HumanBeing h WHERE LOWER(h.name) = LOWER(:name)")
  boolean existsByNameIgnoreCase(@Param("name") String name);
  @Query("SELECT COUNT(h) > 0 FROM HumanBeing h WHERE LOWER(h.soundtrackName) = LOWER(:soundtrackName)")
  boolean existsBySoundtrackNameIgnoreCase(@Param("soundtrackName") String soundtrackName);
  @Query("SELECT COUNT(h) FROM HumanBeing h WHERE h.coordinates.coordX = :x AND h.coordinates.coordY = :y")
  long countByCoordinates(@Param("x") Float x, @Param("y") Float y);
  
  @Query("SELECT h FROM HumanBeing h WHERE LOWER(h.name) = LOWER(:name)")
  List<HumanBeing> findByNameIgnoreCase(@Param("name") String name);
  @Query("SELECT h FROM HumanBeing h WHERE LOWER(h.soundtrackName) = LOWER(:soundtrackName)")
  List<HumanBeing> findBySoundtrackNameIgnoreCase(@Param("soundtrackName") String soundtrackName);
  @Query("SELECT h FROM HumanBeing h WHERE h.coordinates.coordX = :x AND h.coordinates.coordY = :y")
  List<HumanBeing> findByCoordinates(@Param("x") Float x, @Param("y") Float y);

  @Query("SELECT h FROM HumanBeing h WHERE h.realHero = true AND " +
         "(h.hasToothpick IS NULL OR h.hasToothpick = false)")
  List<HumanBeing> findHeroesWithoutToothpick();

  @Modifying
  @Transactional
  @Query("DELETE FROM HumanBeing h WHERE h.id IN :ids")
  void deleteAllByIds(@Param("ids") List<Long> ids);

  @Query("SELECT h FROM HumanBeing h WHERE h.realHero = true AND " +
         "(h.car IS NULL OR h.car.name IS NULL OR TRIM(h.car.name) = '')")
  List<HumanBeing> findHeroesWithoutCar();

  @Modifying
  @Transactional
  @Query("UPDATE HumanBeing h SET h.car = :car WHERE h.id IN :ids")
  void updateCarForIds(@Param("car") com.is.lab1.data.Car car, @Param("ids") List<Long> ids);

  @Query("SELECT COUNT(h) FROM HumanBeing h WHERE LOWER(h.car.name) = LOWER(:carName)")
  long countByCarName(@Param("carName") String carName);
}
