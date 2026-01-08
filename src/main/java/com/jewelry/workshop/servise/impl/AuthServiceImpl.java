package com.jewelry.workshop.servise.impl;

import com.jewelry.workshop.domain.model.dto.auth.AuthResponseDTO;
import com.jewelry.workshop.domain.model.dto.auth.AuthUserDTO;
import com.jewelry.workshop.domain.model.dto.auth.LoginRequestDTO;
import com.jewelry.workshop.domain.model.dto.auth.RegisterRequestDTO;
import com.jewelry.workshop.domain.model.entity.Client;
import com.jewelry.workshop.domain.model.entity.User;
import com.jewelry.workshop.domain.repository.ClientRepository;
import com.jewelry.workshop.domain.repository.UserRepository;
import com.jewelry.workshop.security.auth.UserDetailsImpl;
import com.jewelry.workshop.security.jwt.JwtTokenProvider;
import com.jewelry.workshop.servise.interfaces.AuthService;
import com.jewelry.workshop.util.Constants;
import com.jewelry.workshop.util.PasswordUtil;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordUtil passwordUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email уже зарегистрирован");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Пароли не совпадают");
        }

        String username = generateUniqueUsername(request.getFirstName(), request.getLastName());

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordUtil.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(Constants.ROLE_CLIENT));
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPhone(request.getPhone());
        client.setUser(savedUser);
        client.setCreatedAt(Instant.now());
        client.setUpdatedAt(Instant.now());

        clientRepository.save(client);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        AuthUserDTO authUser = new AuthUserDTO();
        authUser.setId(savedUser.getId());
        authUser.setEmail(savedUser.getEmail());
        authUser.setRole(savedUser.getRole().name());
        authUser.setFullName(client.getFullName());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(Constants.ACCESS_TOKEN_EXPIRE/1000)
                .user(authUser)
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        String fullName = user.getUsername();
        if(user.getRole() == User.Role.CLIENT){
            Client client = clientRepository.findByUserId(user.getId()).orElseThrow();
            fullName = client.getFullName();;
        }

        AuthUserDTO authUser = new AuthUserDTO();
        authUser.setId(user.getId());
        authUser.setEmail(user.getEmail());
        authUser.setRole(user.getRole().name());
        authUser.setFullName(fullName);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(Constants.ACCESS_TOKEN_EXPIRE/1000)
                .user(authUser)
                .build();

    }

    private String generateUniqueUsername(String firstName, String lastName){
        String base = (firstName + "." + lastName).toLowerCase().replace("[^a-z0-9.]", "");
        String username = base;
        int counter = 1;
        while(userRepository.existsByUsername(username)){
            username = base + counter++;
        }
        return username;
    }
}
