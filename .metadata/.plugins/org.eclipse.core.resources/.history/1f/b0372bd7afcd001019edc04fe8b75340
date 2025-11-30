package com.booking.service.client;

import org.springframework.stereotype.Component;

import com.booking.service.dto.UserDto;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDto getUserById(String id) {
        UserDto dto = new UserDto();
        dto.setId("N/A");
        dto.setName("User Service Down");
        dto.setEmail("N/A");
        return dto;
    }
}
