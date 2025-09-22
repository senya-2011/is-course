package com.is.lab1.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность машины.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name; //Поле может быть null

  @Column(name = "cool", nullable = false)
  private boolean cool; //Поле не может быть null

  /**
   * Конструктор с параметрами.
   *
   * @param name название
   * @param cool крутая ли машина
   */
  public Car(String name, boolean cool) {
    this.name = name;
    this.cool = cool;
  }
}
