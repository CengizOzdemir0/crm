package com.cengiz.crm.security;

import com.cengiz.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.stereotype.Component;

/**
 * Özel Giriş Başarı İşleyicisi
 * Giriş bilgilerini kaydeder ve gösterge paneline yönlendirir
 * 
 * @author Cengiz
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        String ipAddress = getClientIP(request);

        // Update user login information
        userRepository.findByEmailAndIsDeletedFalse(email).ifPresent(user -> {
            user.recordSuccessfulLogin(ipAddress);
            userRepository.save(user);
            log.info("User {} logged in successfully from IP: {}", email, ipAddress);
        });

        // Redirect to dashboard
        response.sendRedirect("/crm/dashboard");
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
