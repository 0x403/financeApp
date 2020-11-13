package com.example.UserService.controller;

import com.example.UserService.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import userServiceModels.UserDto;
import userServiceModels.UserRequestModel;
import userServiceModels.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public UserResponseModel createUser(@RequestBody UserRequestModel userDetails) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        UserResponseModel returnValue = new UserResponseModel();
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

}
