package com.is.lab1.data;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "human_beings")
@Check(constraints = "coord_y <= 818") // проверка на уровне БД для поля y
public class HumanBeing {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть
  // уникальным, Значение этого поля должно генерироваться автоматически

  @NotBlank
  @Column(nullable = false)
  private String name; //Поле не может быть null, Строка не может быть пустой

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "coordX", column = @Column(name = "coord_x", nullable = false)),
    @AttributeOverride(name = "coordY", column = @Column(name = "coord_y", nullable = false))
  })
  private Coordinates coordinates; //Поле не может быть null

  @Column(name = "creation_date", nullable = false, updatable = false)
  private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно
  // генерироваться автоматически

  @Column(name = "real_hero", nullable = false)
  private boolean realHero;

  @Column(name = "has_toothpick")
  private Boolean hasToothpick; //Поле может быть null

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "car_id", nullable = false)
  private Car car; //Поле не может быть null

  @Enumerated(EnumType.STRING)
  private Mood mood; //Поле может быть null

  @Column(name = "impact_speed", nullable = false)
  @DecimalMin("0.0")
  private float impactSpeed;

  @NotBlank
  @Column(name = "soundtrack_name", nullable = false)
  private String soundtrackName; //Поле не может быть null

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "weapon_type", nullable = false)
  private WeaponType weaponType; //Поле не может быть null

  public HumanBeing(String name, Coordinates coordinates, boolean realHero, Boolean hasToothpick,
      Car car, Mood mood, float impactSpeed, String soundtrackName, WeaponType weaponType) {
    this.name = name;
    this.coordinates = coordinates;
    this.realHero = realHero;
    this.hasToothpick = hasToothpick;
    this.car = car;
    this.mood = mood;
    this.impactSpeed = impactSpeed;
    this.soundtrackName = soundtrackName;
    this.weaponType = weaponType;
  }

  @PrePersist
  public void prePersist() {
    this.creationDate = LocalDateTime.now();
  }
}