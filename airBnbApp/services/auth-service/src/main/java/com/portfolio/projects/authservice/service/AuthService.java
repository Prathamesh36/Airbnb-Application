package com.portfolio.projects.authservice.service;

import com.portfolio.projects.common.dto.LoginDto;
import com.portfolio.projects.common.dto.SignUpRequestDto;
import com.portfolio.projects.common.dto.UserDto;
import com.portfolio.projects.common.security.JWTService;
import com.portfolio.projects.authservice.entity.User;
import com.portfolio.projects.common.enums.Role;
import com.portfolio.projects.common.exception.ResourceNotFoundException;
import com.portfolio.projects.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private final org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if (user != null) {
            throw new RuntimeException("User is already present with same email id");
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        try {
            com.portfolio.projects.common.event.UserCreatedEvent event = new com.portfolio.projects.common.event.UserCreatedEvent(
                    newUser.getId(), newUser.getEmail(), newUser.getName(),
                    newUser.getDateOfBirth(), newUser.getGender(), newUser.getRoles()
            );
            kafkaTemplate.send("user-created-topic", newUser.getId().toString(), objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            // Log error but don't fail the signup
            e.printStackTrace();
        }

        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));

        User user = (User) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRoles().toString());
        arr[1] = jwtService.generateRefreshToken(user.getId());

        return arr;
    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
        return jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRoles().toString());
    }

}
