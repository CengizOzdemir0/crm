# Spring Boot 2.7.18 → 3.5.x Migration Guide

## Overview

This guide provides detailed instructions for migrating from Spring Boot 2.7.18 to Spring Boot 3.5.x.

## Critical Breaking Changes

### 1. Jakarta EE 9+ (Namespace Change)

**THE MOST IMPORTANT CHANGE**: All `javax.*` packages are now `jakarta.*`

**Before (Spring Boot 2.x)**:
```java
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.servlet.*;
```

**After (Spring Boot 3.x)**:
```java
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.servlet.*;
```

**Affected Packages**:
- `javax.persistence` → `jakarta.persistence`
- `javax.validation` → `jakarta.validation`
- `javax.servlet` → `jakarta.servlet`
- `javax.transaction` → `jakarta.transaction`
- `javax.annotation` → `jakarta.annotation`

**Migration Tool**:
```bash
# Use OpenRewrite for automated migration
mvn org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0
```

### 2. Spring Security Changes

**WebSecurityConfigurerAdapter Removed**

**Before (Spring Boot 2.x)**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }
}
```

**After (Spring Boot 3.x)**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
```

**Key Changes**:
- `authorizeRequests()` → `authorizeHttpRequests()`
- `antMatchers()` → `requestMatchers()`
- Lambda DSL is now preferred
- Return `SecurityFilterChain` bean instead of extending class

### 3. Spring Data Changes

**Repository Method Changes**:
```java
// Before (Spring Boot 2.x)
Optional<User> user = userRepository.findById(id);

// After (Spring Boot 3.x) - Same, but stricter
Optional<User> user = userRepository.findById(id);
// But query derivation is stricter
```

### 4. Properties Changes

**application.yml Changes**:

**Before**:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
```

**After** (mostly same, but some changes):
```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
  # Some properties moved
  data:
    redis:
      host: localhost  # Was spring.redis.host
```

## Migration Steps

### Step 1: Update pom.xml

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>  <!-- Update from 2.7.18 -->
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>  <!-- Minimum Java 17 -->
</properties>
```

### Step 2: Update Dependencies

```xml
<!-- Update Hibernate Validator -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.0.Final</version>
</dependency>

<!-- Update Thymeleaf extras -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>  <!-- Was springsecurity5 -->
</dependency>
```

### Step 3: Automated Namespace Migration

**Option 1: Find and Replace (IntelliJ IDEA)**:
1. Press `Ctrl+Shift+R` (Replace in Path)
2. Find: `import javax.`
3. Replace: `import jakarta.`
4. Scope: Project Files

**Option 2: Command Line**:
```bash
# Linux/Mac
find src/main/java -name "*.java" -exec sed -i 's/import javax\./import jakarta\./g' {} +

# Windows PowerShell
Get-ChildItem -Path src\main\java -Filter *.java -Recurse | 
    ForEach-Object { 
        (Get-Content $_.FullName) -replace 'import javax\.', 'import jakarta.' | 
        Set-Content $_.FullName 
    }
```

### Step 4: Update Security Configuration

**Current SecurityConfig.java** needs to be refactored:

```java
package com.cengiz.crm.config;

import com.cengiz.crm.security.CustomUserDetailsService;
import com.cengiz.crm.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)  // Changed
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authorize -> authorize
                // Public resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/register", "/forgot-password").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "MANAGER")
                
                // Manager and above
                .requestMatchers("/reports/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/settings/**").hasAnyRole("ADMIN", "MANAGER")
                
                // Authenticated users
                .requestMatchers("/dashboard/**", "/customers/**", "/leads/**",
                                "/opportunities/**", "/activities/**", "/contacts/**").authenticated()
                
                // All other requests
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform-login")
                .successHandler(loginSuccessHandler())
                .failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            );

        return http.build();
    }
}
```

### Step 5: Update Thymeleaf Templates

**Before**:
```html
<html xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
```

**After** (same, but ensure dependency is updated):
```html
<html xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
```

### Step 6: Test Everything

```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

## Common Issues and Solutions

### Issue 1: NoClassDefFoundError for javax classes

**Error**:
```
java.lang.NoClassDefFoundError: javax/persistence/Entity
```

**Solution**: You missed some `javax` → `jakarta` conversions. Search and replace all occurrences.

### Issue 2: Security Configuration Not Working

**Error**:
```
WebSecurityConfigurerAdapter cannot be resolved
```

**Solution**: Refactor to use `SecurityFilterChain` bean (see Step 4).

### Issue 3: Actuator Endpoints Not Working

**Solution**: Update endpoint configuration:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### Issue 4: Hibernate Validator Issues

**Error**:
```
HV000030: No validator could be found for constraint
```

**Solution**: Update Hibernate Validator to 8.x:
```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.0.Final</version>
</dependency>
```

## Testing Checklist

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Login/logout works
- [ ] Session management works
- [ ] Database operations work
- [ ] Redis session storage works
- [ ] All endpoints accessible
- [ ] Security rules enforced
- [ ] Thymeleaf templates render
- [ ] No deprecation warnings

## Rollback Plan

If migration fails:

1. Revert `pom.xml`:
   ```xml
   <version>2.7.18</version>
   ```

2. Revert all code changes:
   ```bash
   git checkout -- .
   ```

3. Rebuild:
   ```bash
   mvn clean install
   ```

## Performance Improvements in Spring Boot 3.x

1. **Faster Startup**: ~30% faster
2. **Lower Memory**: Better memory management
3. **Native Compilation**: GraalVM support
4. **Better Observability**: Micrometer improvements

## Resources

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Security 6.0 Migration](https://docs.spring.io/spring-security/reference/migration/index.html)
- [Jakarta EE 9 Migration](https://jakarta.ee/specifications/platform/9/)
