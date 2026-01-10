package com.jewelry.workshop.servise.interfaces;

import com.jewelry.workshop.domain.model.dto.auth.AuthResponseDTO;
import com.jewelry.workshop.domain.model.dto.auth.LoginRequestDTO;
import com.jewelry.workshop.domain.model.dto.auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    boolean verifyEmail(String token);

    void initiatePasswordReset(String email);
    boolean resetPassword(String token, String newPassword);

    void logout(String refreshToken);
}
