package com.cengiz.crm.config;

import com.cengiz.crm.security.CustomUserDetailsService;
import com.cengiz.crm.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Güvenlik Yapılandırması
 * Redis oturum yönetimi ile Spring Security'yi yapılandırır
 * 
 * @author Cengiz
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeRequests(authorize -> authorize
                        // Public resources
                        .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .antMatchers("/login", "/register", "/forgot-password").permitAll()
                        .antMatchers("/actuator/health").permitAll()

                        // Admin only
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/users/**").hasAnyRole("ADMIN", "MANAGER")

                        // Manager and above
                        .antMatchers("/reports/**").hasAnyRole("ADMIN", "MANAGER")
                        .antMatchers("/settings/**").hasAnyRole("ADMIN", "MANAGER")

                        // Authenticated users
                        .antMatchers("/dashboard/**").authenticated()
                        .antMatchers("/customers/**").authenticated()
                        .antMatchers("/leads/**").authenticated()
                        .antMatchers("/opportunities/**").authenticated()
                        .antMatchers("/activities/**").authenticated()
                        .antMatchers("/contacts/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform-login")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .csrf(csrf -> csrf
                        .ignoringAntMatchers("/api/**"))
                .headers(headers -> headers
                        .frameOptions().sameOrigin());

        return http.build();
    }
}
