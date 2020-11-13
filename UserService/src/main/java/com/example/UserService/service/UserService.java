package com.example.UserService.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import userServiceModels.UserDto;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto user);

}
