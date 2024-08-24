package com.benabbou.microservices.user.service;

import com.benabbou.microservices.user.dto.UserDto;
import com.benabbou.microservices.user.model.User;
import com.benabbou.microservices.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        log.info("Creating a new user with email: {}", userDto.getEmail());
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return convertToDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new RuntimeException("User not found");
        });
        return convertToDto(user);
    }

    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new RuntimeException("User not found");
        });
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddressLine1(userDto.getAddressLine1());
        user.setAddressLine2(userDto.getAddressLine2());
        user.setCity(userDto.getCity());
        user.setState(userDto.getState());
        user.setPostalCode(userDto.getPostalCode());
        user.setCountry(userDto.getCountry());
        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        log.info("User deleted with ID: {}", id);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getState(),
                user.getPostalCode(),
                user.getCountry()
        );
    }

    private User convertToEntity(UserDto userDto) {
        return new User(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPhoneNumber(),
                userDto.getAddressLine1(),
                userDto.getAddressLine2(),
                userDto.getCity(),
                userDto.getState(),
                userDto.getPostalCode(),
                userDto.getCountry()
        );
    }
}