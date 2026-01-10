package com.jewelry.workshop.servise.impl;

import com.jewelry.workshop.domain.model.dto.client.ClientProfileDTO;
import com.jewelry.workshop.domain.model.entity.Client;
import com.jewelry.workshop.domain.model.entity.User;
import com.jewelry.workshop.domain.repository.ClientRepository;
import com.jewelry.workshop.domain.repository.OrderRepository;
import com.jewelry.workshop.domain.repository.UserRepository;
import com.jewelry.workshop.servise.interfaces.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public ClientProfileDTO getOwnProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if(!user.getRole().equals(User.Role.CLIENT)){
            throw new RuntimeException("Только клиенты могут просматривать профиль");
        }

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль клиента не найден"));

        Long orderCount = orderRepository.countOrdersByClientId(client.getId());
        BigDecimal totalSpent = orderRepository.getTotalSpentByClientId(client.getId());
        LocalDateTime lastOrderDate = orderRepository.getLastOrderDateByClientId(client.getId());

        return ClientProfileDTO.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .patronymic(client.getPatronymic())
                .email(user.getEmail())
                .phone(client.getPhone())
                .fullName(client.getFullName())
                .orderCount(orderCount)
                .totalSpent(totalSpent)
                .lastOrderDate(lastOrderDate)
                .build();

    }

}
