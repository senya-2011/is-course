package com.is.lab1.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для Координат используется в других сущностях.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Coordinates {

  @NotNull
  @Column(name = "coord_x", nullable = false)
  private Float coordX; //Поле не может быть null

  @NotNull
  @DecimalMax("818")
  @Column(name = "coord_y", nullable = false)
  private Float coordY; //Максимальное значение поля: 818, Поле не может быть null
}
