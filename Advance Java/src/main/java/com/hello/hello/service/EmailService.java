package com.hello.hello.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public String sendEmail(String msg) {
        String output = "Email sent: " + msg;
        System.out.println(output);
        return output;
    }
}
