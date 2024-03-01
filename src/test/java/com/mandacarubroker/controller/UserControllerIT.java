package com.mandacarubroker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService service;

    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "LaraSoU";
    private final String validPassword = "#pass555";
    private final String validFirstName = "Lara";
    private final String validLastName = "Souza";
    private final LocalDate validBirthDate = LocalDate.of(2006,2,28);
    private final double validBalance = 90.50;
    private final RequestUserDTO invalidEmailUserDTO = new RequestUserDTO(
            "marcosloiola@.yahoo.com",
            "Marcos22",
            "passmarco123",
            "Marcos",
            "Loiola",
            LocalDate.of(2002, 2, 26),
            0.25
    );
    private final RequestUserDTO invalidPasswordUserDTO = new RequestUserDTO(
            "marcosloiola@yahoo.com",
            "Marcos23",
            "pass123",
            "Marcos",
            "Loiola",
            LocalDate.of(2002, 2, 26),
            0.25
    );
    private final RequestUserDTO invalidAgeUserDTO = new RequestUserDTO(
            "marcosloiola@yahoo.com",
            "Marcos23",
            "passmarco123",
            "Marcos",
            "Loiola",
            LocalDate.of(2006, 3, 2),
            0.25
    );

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
    private ResponseUserDTO responseUserDTO;

    @BeforeEach
    void setUp() {
        user = userRepository.findAll().get(0);
        userId = user.getId();
        urlRequestUserById = "/users/" + userId;
        responseUserDTO = new ResponseUserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getBalance()
        );
    }

    @AfterEach
    void tearDown() {
        User alreadyExistentUser = userRepository.findByUsername(validUsername);
        if(alreadyExistentUser != null){
            service.deleteUser(alreadyExistentUser.getId());
        }
    }

    void assertResponseUserDTO(final RequestUserDTO userRequestDTO, final ResponseUserDTO receivedUser){
        assertEquals(userRequestDTO.firstName(), receivedUser.firstName());
        assertEquals(userRequestDTO.lastName(), receivedUser.lastName());
        assertEquals(userRequestDTO.birthDate(), receivedUser.birthDate());
        assertEquals(userRequestDTO.balance(), receivedUser.balance());
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
        String responseUserDTOJsonString = objectMapper.writeValueAsString(responseUserDTO);

        RequestBuilder requestBuilder = get(urlRequestUserById);
        ResultMatcher matchResponse = content().json(responseUserDTOJsonString);

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
        ResponseUserDTO createdUser = objectMapper.readValue(content, ResponseUserDTO.class);

        assertResponseUserDTO(validUserDTO, createdUser);
    }

    @Test
    void itShouldReturnUserDataAfterSucessfulPut() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = put(urlRequestUserById)
                .contentType("application/json")
                .content(userJsonString);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseUserDTO updatedUser = objectMapper.readValue(content, ResponseUserDTO.class);

        assertResponseUserDTO(validUserDTO, updatedUser);
    }

    @Test
    void itShouldReturnOkStatusAfterSucessfulPut() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = put(urlRequestUserById)
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isOk();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNotFoundWhenPutUserDoesNotExists() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = put(urlRequestInvalidUser)
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isNotFound();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldBeAbleToDeleteUser() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestUserById);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
        assertEquals(Optional.empty(), service.getUserById(userId));
    }

    @Test
    void itShouldReturnNoContentStatusWhenDeleteUser() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestUserById);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNoContentStatusWhenDeleteUserDoesNotExists() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestInvalidUser);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePostInvalidUserEmail() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(invalidEmailUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnConflictStatusWhenPostDuplicatedUsername() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(validUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);

        ResultMatcher matchStatus = status().isConflict();
        mockMvc.perform(requestBuilder);
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePostInvalidUserPassword() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(invalidPasswordUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePostInvalidUserAge() throws Exception {
        String userJsonString = objectMapper.writeValueAsString(invalidAgeUserDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType("application/json")
                .content(userJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

}