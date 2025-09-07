package com.example.userApp.controller;

import com.example.userApp.dto.UserCreateRequest;
import com.example.userApp.dto.UserResponse;
import com.example.userApp.dto.UserUpdateRequest;
import com.example.userApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    
    private final UserService userService;
    
    /**
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Request to create user with username: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Min(1) Long id) {
        log.info("Request to get user by id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("Request to get user by username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {
        
        log.info("Request to get all users - active: {}, search: {}", active, search);
        
        List<UserResponse> response;
        
        if (search != null && !search.trim().isEmpty()) {
            response = userService.searchUsersByName(search.trim());
        } else if (active != null && active) {
            response = userService.getActiveUsers();
        } else {
            response = userService.getAllUsers();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all users with pagination
     */
    @GetMapping("/pageable")
    public ResponseEntity<Page<UserResponse>> getAllUsersPageable(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Request to get users with pagination - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> response = userService.getAllUsersPageable(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        
        log.info("Request to update user with id: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long id) {
        log.info("Request to delete user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Deactivate user
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable @Min(1) Long id) {
        log.info("Request to deactivate user with id: {}", id);
        UserResponse response = userService.deactivateUser(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activate user
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable @Min(1) Long id) {
        log.info("Request to activate user with id: {}", id);
        UserResponse response = userService.activateUser(id);
        return ResponseEntity.ok(response);
    }
}
