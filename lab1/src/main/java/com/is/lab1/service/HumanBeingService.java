package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.exception.HumanBeingNotFoundException;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import jakarta.persistence.criteria.JoinType;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HumanBeingService {

  private final HumanBeingRepository humanRepo;
  private final CarRepository carRepo;
  private final SseService sseService;

  public HumanBeingService(HumanBeingRepository humanRepo, CarRepository carRepo,
      SseService sseService) {
    this.humanRepo = humanRepo;
    this.carRepo = carRepo;
    this.sseService = sseService;
  }

  public HumanBeing create(HumanBeing hb) {
    HumanBeing saved = humanRepo.save(hb);
    sseService.broadcast("data_changed");
    return saved;
  }

  public Optional<HumanBeing> findById(Long id) {
    return humanRepo.findById(id);
  }

  public Page<HumanBeing> listAll(int page, int size, String sortBy, String dir, String q) {
    Sort sort = Sort.by(Sort.Direction.fromString(Optional.ofNullable(dir).orElse("ASC")),
        Optional.ofNullable(sortBy).orElse("id"));
    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
    if (q == null || q.trim().isEmpty()) {
      return humanRepo.findAll(pageable);
    } else {
      Specification<HumanBeing> spec = containsInStringFields(q);
      return humanRepo.findAll(spec, pageable);
    }
  }

  @Transactional
  public HumanBeing update(Long id, HumanBeing updated) {
    HumanBeing existing = humanRepo.findById(id)
        .orElseThrow(() -> new HumanBeingNotFoundException("HumanBeing with id " + id + " not found"));
    
    existing.setName(updated.getName());
    existing.setCoordinates(updated.getCoordinates());
    existing.setRealHero(updated.isRealHero());
    existing.setHasToothpick(updated.getHasToothpick());
    existing.setCar(updated.getCar());
    existing.setMood(updated.getMood());
    existing.setImpactSpeed(updated.getImpactSpeed());
    existing.setSoundtrackName(updated.getSoundtrackName());
    existing.setWeaponType(updated.getWeaponType());
    HumanBeing saved = humanRepo.save(existing);
    sseService.broadcast("data_changed");
    return saved;
  }

  @Transactional
  public void delete(Long id) {
    HumanBeing hb = humanRepo.findById(id)
        .orElseThrow(() -> new HumanBeingNotFoundException("HumanBeing with id " + id + " not found"));
    humanRepo.delete(hb);
    sseService.broadcast("data_changed");
  }

  @Transactional
  public boolean deleteOneByImpactSpeed(float value) {
    final double epsilon = 1e-6;
    
    List<HumanBeing> candidates = humanRepo.findByImpactSpeedWithEpsilon(value, epsilon);
    
    if (!candidates.isEmpty()) {
      humanRepo.delete(candidates.get(0));
      sseService.broadcast("data_changed");
      return true;
    }
    return false;
  }

  public double sumImpactSpeed() {
    Double sum = humanRepo.sumImpactSpeed();
    return sum != null ? sum : 0.0;
  }

  public List<HumanBeing> findByNameSubstring(String substring) {
    if (substring == null) {
      return Collections.emptyList();
    }
    return humanRepo.findByNameContainingIgnoreCase(substring);
  }

  @Transactional
  public int deleteHeroesWithoutToothpick() {
    List<HumanBeing> toDelete = humanRepo.findHeroesWithoutToothpick();
    
    if (!toDelete.isEmpty()) {
      List<Long> ids = toDelete.stream().map(HumanBeing::getId).toList();
      humanRepo.deleteAllByIds(ids);
      sseService.broadcast("data_changed");
    }
    return toDelete.size();
  }

  @Transactional
  public int reassignHeroesWithoutCarToLada() {
    Car lada = carRepo.findByName("Lada Kalina (red)")
        .orElseGet(() -> carRepo.save(new Car("Lada Kalina (red)", false)));

    List<HumanBeing> heroesWithoutCar = humanRepo.findHeroesWithoutCar();
    
    if (!heroesWithoutCar.isEmpty()) {
      List<Long> ids = heroesWithoutCar.stream().map(HumanBeing::getId).toList();
      humanRepo.updateCarForIds(lada, ids);
      sseService.broadcast("data_changed");
    }
    return heroesWithoutCar.size();
  }

  private Specification<HumanBeing> containsInStringFields(String q) {
    return (root, query, cb) -> {
      String like = "%" + q.toLowerCase(Locale.ROOT) + "%";
      var namePath = cb.lower(root.get("name"));
      var soundtrackPath = cb.lower(root.get("soundtrackName"));
      var carJoin = root.join("car", JoinType.LEFT);
      var carNamePath = cb.lower(carJoin.get("name"));
      return cb.or(
          cb.like(namePath, like),
          cb.like(soundtrackPath, like),
          cb.like(carNamePath, like)
      );
    };
  }
}
