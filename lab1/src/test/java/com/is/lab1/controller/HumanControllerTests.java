package com.is.lab1.controller;

import com.is.lab1.data.*;
import com.is.lab1.repository.CarRepository;
import com.is.lab1.repository.HumanBeingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HumanControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HumanBeingRepository humanRepo;

    @Autowired
    private CarRepository carRepo;

    @Test
    @DisplayName("POST /humans creates human and redirects")
    void createHuman_redirects() throws Exception {
        mockMvc.perform(post("/humans")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("coordinatesX", "1")
                        .param("coordinatesY", "2")
                        .param("realHero", "true")
                        .param("hasToothpick", "false")
                        .param("mood", "CALM")
                        .param("impactSpeed", "3")
                        .param("weaponType", "AXE")
                        .param("soundtrackName", "OST"))
                .andExpect(status().is3xxRedirection());

        assertThat(humanRepo.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("POST /humans/sum-impact redirects with sum param")
    void sumImpact_redirects() throws Exception {
        humanRepo.save(new HumanBeing("A", new Coordinates(1f,2f), true, false, carRepo.save(new Car("C", false)), Mood.CALM, 5f, "s", WeaponType.AXE));
        mockMvc.perform(post("/humans/sum-impact").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection());
    }
}


