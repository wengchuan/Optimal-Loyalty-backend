package com.optimal.loyalty.Optimal.Loyalty.config;


import com.optimal.loyalty.Optimal.Loyalty.service.impl.UserDetailsServiceImpl;
import com.optimal.loyalty.Optimal.Loyalty.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Configuration
public class AuthConfig {
    private  final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthConfig(
            UserRepository userRepository,
            UserDetailsServiceImpl userDetailsService)
    {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Bean
   public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }


    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                //vvvvvvvv---- THIS IS THE CRUCIAL MAPPING PART ----vvvvvvvv
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),         // username for UserDetails
                        user.getPassword(),      // password for UserDetails
                        // authorities (e.g., roles) - provide an empty list if none
                        new ArrayList<>()
                        // You can also add booleans for account status if needed:
                        // true, // enabled
                        // true, // accountNonExpired
                        // true, // credentialsNonExpired
                        // true  // accountNonLocked
                ))
                //^^^^^^^^---- END OF MAPPING PART ----^^^^^^^^
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }




    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }





}
