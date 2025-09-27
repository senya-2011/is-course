package com.is.lab1.controller;

import com.is.lab1.data.Car;
import com.is.lab1.repository.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Test
    @DisplayName("POST /cars creates car and redirects")
    void createCar_redirects() throws Exception {
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Volga")
                        .param("cool", "true"))
                .andExpect(status().is3xxRedirection());
        assertThat(carRepository.findAll()).extracting(Car::getName).contains("Volga");
    }
}


