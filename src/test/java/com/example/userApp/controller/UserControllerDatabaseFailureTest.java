package com.example.userApp.controller;

import com.example.userApp.dto.UserCreateRequest;
import com.example.userApp.exception.GlobalExceptionHandler;
import com.example.userApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerDatabaseFailureTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUser_DatabaseUnavailable_ShouldReturnInternalServerError() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("+1234567890");

        // Mock service to throw database connection exception
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new DataAccessResourceFailureException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Database connection failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createUser_DatabaseTransactionTimeout_ShouldReturnInternalServerError() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("timeoutuser");
        request.setEmail("timeout@example.com");
        request.setFirstName("Timeout");
        request.setLastName("User");

        // Mock service to throw transaction timeout exception
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new org.springframework.transaction.TransactionTimedOutException("Transaction timed out"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Transaction timed out"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createUser_DatabaseConstraintViolation_ShouldReturnInternalServerError() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("constraintuser");
        request.setEmail("constraint@example.com");
        request.setFirstName("Constraint");
        request.setLastName("User");

        // Mock service to throw SQL exception (database-level constraint violation)
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Database constraint violation"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Database constraint violation"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
