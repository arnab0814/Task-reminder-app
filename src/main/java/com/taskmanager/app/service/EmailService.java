package com.taskmanager.app.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("OTP Verification - Task Manager");
        message.setText(
                "Your OTP is: "+ otp +
                        "\n\nThis OTP is valid for 5 minutes.\n\n"+
                        "Do not share this OTP to anyone"

        );
        mailSender.send(message);
    }
}
