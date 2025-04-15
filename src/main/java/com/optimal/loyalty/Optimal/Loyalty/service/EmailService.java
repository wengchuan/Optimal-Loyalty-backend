package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.model.User;

import java.util.Locale;

public interface EmailService {

    void sendResetPasswordEmail(String contextPath, Locale locale, String token, User user);

}
