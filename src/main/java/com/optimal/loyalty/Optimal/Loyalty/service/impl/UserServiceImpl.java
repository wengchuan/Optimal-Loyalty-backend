package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.dto.CreateUserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserLoginDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.repository.UserRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final Map<String, String> resetTokens = new HashMap<>();



    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }




    @Override
    public User createUser(CreateUserDTO createUserDTO) {

        User user = convertToEntity(createUserDTO);
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public void userLogin(UserLoginDTO userLoginDTO) {

    }

    @Override
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public String generateResetToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, email); // Store token with email
        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String email = resetTokens.get(token);
        if (email == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user.get());
            resetTokens.remove(token); // Remove token after use
        } else {
            throw new RuntimeException("User not found");
        }
    }

     @Override
    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        userRepository.save(user);
    }


    private User convertToEntity(CreateUserDTO createUserDTO){

        return modelMapper.map(createUserDTO,User.class);
    }


}
