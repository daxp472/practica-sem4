package com.hello.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldInjectionNotificationService {

    @Autowired
    private EmailService emailService;

    public String sendEmail(String msg) {
        return emailService.sendEmail(msg);
    }
}
