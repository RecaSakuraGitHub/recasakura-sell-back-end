package com.recasakura.sellbackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.recasakura.sellbackend.exception.UserAlreadyExistsException;
import com.recasakura.sellbackend.exception.UserNotFoundException;
import com.recasakura.sellbackend.model.user.*;
import com.recasakura.sellbackend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userExistsByEmailAndPhone(request.getEmail(), request.getPhone())) {
            throw new UserAlreadyExistsException("The email and phone is already exists.");
        } else if (userExitstsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("The email is already exists.");
        } else if (userExitstsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("The phone is already exists.");
        }
        User user = new User(request.getName(), request.getEmail(), request.getPhone());
        UserResponse response = new UserResponse(this.userRepository.save(user));
        return response;
    }

    public UserResponse deleteUser(UserDeleteRequest request) {
        User user = this.userRepository.findByEmailAndPhone(request.getEmail(), request.getPhone()).orElseThrow(
            () -> new UserNotFoundException()
        );
        UserResponse response = new UserResponse(user);
        this.userRepository.delete(user);
        return response;
    }

    public List<UserProjection> getAllUsers() {
        return this.userRepository.findAllBy();
    }

    public UserResponse getUserById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
        return new UserResponse(user);
    }

    public boolean userExistsByEmailAndPhone(String email, String phone) {
        return this.userRepository.findByEmailAndPhone(email, phone).isPresent();
    }
    public boolean userExitstsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean userExitstsByPhone(String phone) {
        return this.userRepository.existsByPhone(phone);
    }
}
