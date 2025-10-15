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
  private final ValidationService validationService;
  private final LockService lockService;

  public HumanBeingService(HumanBeingRepository humanRepo, CarRepository carRepo,
      SseService sseService, ValidationService validationService, LockService lockService) {
    this.humanRepo = humanRepo;
    this.carRepo = carRepo;
    this.sseService = sseService;
    this.validationService = validationService;
    this.lockService = lockService;
  }

  public HumanBeing create(HumanBeing hb) {
    if (hb.getName() != null) lockService.lockKey("human:name:" + hb.getName().toLowerCase());
    if (hb.getSoundtrackName() != null) lockService.lockKey("human:soundtrack:" + hb.getSoundtrackName().toLowerCase());
    if (hb.getCoordinates() != null) lockService.lockKey("human:coords:" + hb.getCoordinates().getCoordX() + ":" + hb.getCoordinates().getCoordY());
    if (hb.getCar() != null && hb.getCar().getName() != null) lockService.lockKey("car:name:" + hb.getCar().getName().toLowerCase());
    validationService.validateHuman(hb);
    HumanBeing saved = humanRepo.save(hb);
    sseService.broadcast("data_changed");
    return saved;
  }

  public Optional<HumanBeing> findById(Long id) {
    return humanRepo.findById(id);
  }

  public Page<HumanBeing> listAll(int page, int size, Optional<String> sortBy, Optional<String> dir, Optional<String> q) {
    Sort sort = Sort.by(Sort.Direction.fromString(dir.orElse("ASC")),
        sortBy.orElse("id"));
    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
    
    return q
        .filter(query -> !query.trim().isEmpty())
        .map(this::containsInStringFields)
        .map(spec -> humanRepo.findAll(spec, pageable))
        .orElse(humanRepo.findAll(pageable));
  }

  @Transactional
  public HumanBeing update(Long id, HumanBeing updated) {
    HumanBeing existing = humanRepo.findById(id)
        .orElseThrow(() -> new HumanBeingNotFoundException("HumanBeing with id " + id + " not found"));
    
    if (updated.getName() != null) lockService.lockKey("human:name:" + updated.getName().toLowerCase());
    if (updated.getSoundtrackName() != null) lockService.lockKey("human:soundtrack:" + updated.getSoundtrackName().toLowerCase());
    if (updated.getCoordinates() != null) lockService.lockKey("human:coords:" + updated.getCoordinates().getCoordX() + ":" + updated.getCoordinates().getCoordY());
    if (updated.getCar() != null && updated.getCar().getName() != null) lockService.lockKey("car:name:" + updated.getCar().getName().toLowerCase());

    validationService.validateHumanUpdate(existing, updated);
    
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
    
    var candidates = humanRepo.findByImpactSpeedWithEpsilon(value, epsilon);
    
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

  public List<HumanBeing> findByNameSubstring(Optional<String> substring) {
    return substring
        .filter(s -> !s.trim().isEmpty())
        .map(humanRepo::findByNameContainingIgnoreCase)
        .orElse(Collections.emptyList());
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
    String pattern = "%" + q.toLowerCase(Locale.ROOT) + "%";
    return (root, query, cb) -> {
      var nameLike = cb.like(cb.lower(root.get("name")), pattern);
      var soundtrackLike = cb.like(cb.lower(root.get("soundtrackName")), pattern);
      var carJoin = root.join("car", JoinType.LEFT);
      var carNameLike = cb.like(cb.lower(carJoin.get("name")), pattern);
      return cb.or(nameLike, soundtrackLike, carNameLike);
    };
  }
}
