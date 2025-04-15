package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Override
    public void sendResetPasswordEmail(String contextPath, Locale locale, String token, User user) {
        String url = "http://localhost:3000/reset-password?token=" + token; // Frontend URL
        String message = "Click the link below to reset your password:" + url;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Reset Password");
        email.setText(message);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));

        mailSender.send(email);
    }


}
