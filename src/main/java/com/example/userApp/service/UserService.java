package com.example.userApp.service;

import com.example.userApp.dto.UserCreateRequest;
import com.example.userApp.dto.UserResponse;
import com.example.userApp.dto.UserUpdateRequest;
import com.example.userApp.exception.UserAlreadyExistsException;
import com.example.userApp.exception.UserNotFoundException;
import com.example.userApp.mapper.UserMapper;
import com.example.userApp.model.User;
import com.example.userApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserResponse createUser(UserCreateRequest request) {
        log.debug("Creating new user with username: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }
        
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        return userMapper.toResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        
        return userMapper.toResponse(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        
        return userRepository.findAll()
            .stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersPageable(Pageable pageable) {
        log.debug("Fetching users with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        
        return userRepository.findAll(pageable)
            .map(userMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.debug("Fetching active users");
        
        return userRepository.findByIsActive(true)
            .stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsersByName(String name) {
        log.debug("Searching users by name: {}", name);
        
        return userRepository.findByFirstNameOrLastNameContaining(name)
            .stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.debug("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        // Check if username is being changed and if new username already exists
        if (request.getUsername() != null && 
            !request.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        
        // Check if email is being changed and if new email already exists
        if (request.getEmail() != null && 
            !request.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }
        
        userMapper.updateEntityFromRequest(user, request);
        User updatedUser = userRepository.save(user);
        
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }
    
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }
    
    public UserResponse deactivateUser(Long id) {
        log.debug("Deactivating user with id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        
        log.info("User deactivated successfully with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }
    
    public UserResponse activateUser(Long id) {
        log.debug("Activating user with id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        user.setIsActive(true);
        User updatedUser = userRepository.save(user);
        
        log.info("User activated successfully with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }
}
