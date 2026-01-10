package com.jewelry.workshop.servise.interfaces;

import com.jewelry.workshop.domain.model.dto.client.ClientProfileDTO;

public interface ClientService {
    ClientProfileDTO getOwnProfile(Long userId);
}
