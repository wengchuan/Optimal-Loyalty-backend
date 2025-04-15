package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.dto.UserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserLoginDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.repository.UserRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.AuthenticateService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AuthenticateServiceImpl implements AuthenticateService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AuthenticateServiceImpl
            (AuthenticationManager authenticationManager,
             UserRepository userRepository, ModelMapper modelMapper)
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public UserDTO authenticate(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("Email does not exist"));

         authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getEmail(),
                        userLoginDTO.getPassword()
                ));

        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user){
        return modelMapper.map(user, UserDTO.class);
    }


}
