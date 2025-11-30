package com.booking.service.client;

import org.springframework.stereotype.Component;

import com.booking.service.dto.UserDto;

@Component
class UserClientFallback implements UserClient {
    @Override
    public UserDto getUserById(String id) {
        throw new RuntimeException("User service is down");
    }

    @Override
    public UserDto getUserByEmail(String email) {
        throw new RuntimeException("User service is down");
    }
}