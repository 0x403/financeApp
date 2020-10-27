package com.example.UserService.service;

import com.example.UserService.dto.UserDto;
import com.example.UserService.entity.UserEntity;
import com.example.UserService.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if(userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Record already exists.");
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        // alternative way with beanutils
//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(user, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
//        UserDto returnValue = new UserDto();
//        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) throw new UsernameNotFoundException(username);

        return new User(userEntity.getUsername(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

}
