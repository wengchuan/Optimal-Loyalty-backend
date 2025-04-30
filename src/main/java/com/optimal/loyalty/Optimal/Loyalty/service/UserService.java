package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.dto.CreateUserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserLoginDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(CreateUserDTO createUserDTO);
    void userLogin(UserLoginDTO userLoginDTO);
    Optional<User> findByEmail(String email);
    String generateResetToken(String email);
    void resetPassword(String token, String newPassword);
    User findByUsername (String username);
    void saveUser(User user);
}
