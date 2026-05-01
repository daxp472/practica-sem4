package com.hello.hello.service;

import org.springframework.stereotype.Service;

@Service
public class ConstructorInjectionNotificationService {

    private final EmailService emailService;

    public ConstructorInjectionNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String sendEmail(String msg) {
        return emailService.sendEmail(msg);
    }
}
