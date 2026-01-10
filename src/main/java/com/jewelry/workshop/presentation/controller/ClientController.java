package com.jewelry.workshop.presentation.controller;

import com.jewelry.workshop.domain.model.dto.client.ClientProfileDTO;
import com.jewelry.workshop.security.auth.UserDetailsImpl;
import com.jewelry.workshop.servise.impl.ClientServiceImpl;
import com.jewelry.workshop.servise.interfaces.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@Tag(name = "Клиенты", description = "Управление профилем клиента")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/me")
    @Operation(summary = "Просмотреть свой профиль")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileDTO> getOwnProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        Long userId = userDetails.getId();
        ClientProfileDTO profile = clientService.getOwnProfile(userId);
        return ResponseEntity.ok(profile);
    }

}
