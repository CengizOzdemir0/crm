package com.cengiz.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Kimlik Doğrulama Controller
 * Giriş ve çıkış sayfalarını yönetir
 * 
 * @author Cengiz
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}
