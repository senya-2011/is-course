package com.is.lab1.controller;

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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Bad request from HumanController due to invalid Y > 818 maps to redirect with error")
    void invalidY_redirectsWithError() throws Exception {
        mockMvc.perform(post("/humans")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("coordinatesX", "1")
                        .param("coordinatesY", "1000")
                        .param("realHero", "true")
                        .param("hasToothpick", "false")
                        .param("mood", "CALM")
                        .param("impactSpeed", "3")
                        .param("weaponType", "AXE")
                        .param("soundtrackName", "OST"))
                .andExpect(status().is3xxRedirection());
    }
}


