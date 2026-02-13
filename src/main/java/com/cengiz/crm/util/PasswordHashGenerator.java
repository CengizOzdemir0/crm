package com.cengiz.crm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String password = "Admin123!";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("\nSQL Update Command:");
        System.out.println(
                "UPDATE users SET password = '" + hash + "', failed_login_attempts = 0 WHERE email = 'admin@crm.com';");
    }
}
