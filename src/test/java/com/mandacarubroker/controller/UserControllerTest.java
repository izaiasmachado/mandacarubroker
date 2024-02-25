package com.mandacarubroker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService service;


    @Test
    void itShouldReturnOkStatusWhenGetAllUsers() throws Exception {
        RequestBuilder requestBuilder = get("/users");
        ResultMatcher resultMatcher = status().isOk();
        mockMvc.perform(requestBuilder).andExpect(resultMatcher);
    }

    @Test
    void itShouldBeAbleToGetAllUsers() throws Exception {
        RequestBuilder requestBuilder = get("/users");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        List<User> users = objectMapper.readValue(content, List.class);

        assertEquals(service.getAllUsers().size(), users.size());
    }



}