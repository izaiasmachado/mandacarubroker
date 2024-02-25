package com.mandacarubroker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "LaraS";
    private final String validPassword = "pass555";
    private final String validFirstName = "Lara";
    private final String validLastName = "Souza";
    private final LocalDate validBirthDate = LocalDate.of(1997,4,5);
    private final double validBalance = 90.50;

    private final RequestUserDTO validUserDTO = new RequestUserDTO(
            validEmail,
            validUsername,
            validPassword,
            validFirstName,
            validLastName,
            validBirthDate,
            validBalance
    );

    private final String urlRequestInvalidUser = "/users/dummy-user-id";
    private User user;
    private String userId;
    private String urlRequestUserById;

    @BeforeEach
    void setUp() {
        user = service.getAllUsers().get(0);
        userId = user.getEmail();
        urlRequestUserById = "/users/" + userId;
    }

    @AfterEach
    void tearDown() {
    }

    void assertRequestDTOEqualsUser(final RequestUserDTO userDTO, final User receivedUser) {
        assertEquals(userDTO.email(), receivedUser.getEmail());
        assertEquals(userDTO.username(), receivedUser.getUsername());
        assertEquals(userDTO.password(), receivedUser.getPassword());
        assertEquals(userDTO.firstName(), receivedUser.getFirstName());
        assertEquals(userDTO.lastName(), receivedUser.getLastName());
        assertEquals(userDTO.birthDate(), receivedUser.getBirthDate());
        assertEquals(userDTO.balance(), receivedUser.getBalance());
    }


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

    @Test
    void itShouldBeAbleToGetUserById() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(user);

        RequestBuilder requestBuilder = get(urlRequestUserById);
        ResultMatcher matchResponse = content().json(userJsonString);

        mockMvc.perform(requestBuilder).andExpectAll(matchResponse);
    }

    @Test
    void itShouldReturnOkStatusWhenGetUserById() throws Exception {
        RequestBuilder requestBuilder = get(urlRequestUserById);
        ResultMatcher matchStatus = status().isOk();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNotFoundWhenGetUserDoesNotExists() throws Exception {
        RequestBuilder requestBuilder = get(urlRequestInvalidUser);
        ResultMatcher matchStatus = status().isNotFound();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnCreatedStatusAfterSucessfulPost() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isCreated();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnUserDataAfterSucessfulPost() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        User createdUser = objectMapper.readValue(content, User.class);

        assertRequestDTOEqualsUser(validUserDTO, createdUser);
    }

}