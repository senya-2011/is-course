package com.is.lab1.service;

import com.is.lab1.data.Car;
import com.is.lab1.data.HumanBeing;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
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

  @PersistenceContext
  private EntityManager em;

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
    // Используем блокировку для предотвращения lost updates
    HumanBeing existing = em.find(HumanBeing.class, id, 
        jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    if (existing == null) {
      throw new NoSuchElementException("HumanBeing not found: " + id);
    }
    
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
        .orElseThrow(() -> new NoSuchElementException("HumanBeing not found: " + id));
    humanRepo.delete(hb);
    sseService.broadcast("data_changed");
  }

  @Transactional
  public boolean deleteOneByImpactSpeed(float value) {
    final double epsilon = 1e-6;
    
    // Используем JPQL с блокировкой для атомарной операции
    List<HumanBeing> candidates = em.createQuery(
        "SELECT h FROM HumanBeing h WHERE ABS(h.impactSpeed - :value) <= :epsilon",
        HumanBeing.class
    )
        .setParameter("value", value)
        .setParameter("epsilon", epsilon)
        .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
        .setMaxResults(1) // Берем только первого
        .getResultList();
    
    if (!candidates.isEmpty()) {
      humanRepo.delete(candidates.get(0));
      sseService.broadcast("data_changed");
      return true;
    }
    return false;
  }

  public double sumImpactSpeed() {
    return humanRepo.findAll().stream().mapToDouble(HumanBeing::getImpactSpeed).sum();
  }

  public List<HumanBeing> findByNameSubstring(String substring) {
    if (substring == null) {
      return Collections.emptyList();
    }
    return humanRepo.findByNameContainingIgnoreCase(substring);
  }

  @Transactional
  public int deleteHeroesWithoutToothpick() {
    // Используем JPQL с блокировкой для атомарной операции
    List<HumanBeing> toDelete = em.createQuery(
        "SELECT h FROM HumanBeing h WHERE h.realHero = true AND "
            + "(h.hasToothpick IS NULL OR h.hasToothpick = false)",
        HumanBeing.class
    ).setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE).getResultList();
    
    if (!toDelete.isEmpty()) {
      List<Long> ids = toDelete.stream().map(HumanBeing::getId).toList();
      humanRepo.deleteAllById(ids);
      sseService.broadcast("data_changed");
    }
    return toDelete.size();
  }

  @Transactional
  public int reassignHeroesWithoutCarToLada() {
    Car lada = carRepo.findByName("Lada Kalina (red)")
        .orElseGet(() -> carRepo.save(new Car("Lada Kalina (red)", false)));

    // Используем JPQL с блокировкой для атомарной операции
    List<HumanBeing> heroesWithoutCar = em.createQuery(
        "SELECT h FROM HumanBeing h WHERE h.realHero = true AND "
            + "(h.car IS NULL OR h.car.name IS NULL OR TRIM(h.car.name) = '')",
        HumanBeing.class
    ).setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE).getResultList();
    
    List<HumanBeing> changed = new ArrayList<>();
    for (HumanBeing h : heroesWithoutCar) {
      h.setCar(lada);
      HumanBeing saved = humanRepo.save(h);
      changed.add(saved);
    }
    
    if (!changed.isEmpty()) {
      sseService.broadcast("data_changed");
    }
    return changed.size();
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
