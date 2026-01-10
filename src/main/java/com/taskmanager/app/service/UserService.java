package com.taskmanager.app.service;

import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }



    public void register(UserEntity user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }


        user.setVerified(false);
        user.setCreatedAt(LocalDate.now());

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String encoded = passwordEncoder.encode(user.getPassword());
        System.out.println("ENCODED PASSWORD = " + encoded);
        user.setPassword(encoded);


        emailService.sendOtp(user.getEmail(), otp);

    }



    public UserEntity verifyOtp(String email, String otp) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new RuntimeException("Already verified");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
        return user;
    }



    public UserEntity login(String email, String password) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found!!!"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your password");
        }


        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }


    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
