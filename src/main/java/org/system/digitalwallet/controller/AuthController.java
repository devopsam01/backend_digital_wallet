package org.system.digitalwallet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.system.digitalwallet.dto.*;
import org.system.digitalwallet.model.User;
import org.system.digitalwallet.service.AuthService;
import org.system.digitalwallet.service.AuditService;
import org.system.digitalwallet.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final AuditService auditService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody UserRegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER") // support admin registration
                .build();

        User registeredUser = userService.register(user);

        auditService.log(
                registeredUser,
                "REGISTER",
                "USER",
                registeredUser.getId(),
                "SUCCESS",
                "User registered successfully",
                httpRequest
        );

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest
    ) {
        try {
            String token = authService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            User user = userService.findByUsername(request.getUsername());

            auditService.log(
                    user,
                    "LOGIN",
                    "AUTH",
                    user.getId(),
                    "SUCCESS",
                    "User logged in",
                    httpRequest
            );

            // <-- return token + role
            return new AuthResponse(token, user.getRole());

        } catch (Exception ex) {
            auditService.logAnonymous(
                    "LOGIN",
                    "FAILED",
                    "Invalid username or password",
                    httpRequest
            );
            throw ex;
        }
    }

    // ✅ LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        auditService.log(
                user,
                "LOGOUT",
                "AUTH",
                user.getId(),
                "SUCCESS",
                "User logged out",
                httpRequest
        );

        return ResponseEntity.ok("Logged out successfully");
    }

    // ✅ PROFILE
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        auditService.log(
                user,
                "VIEW_PROFILE",
                "USER",
                user.getId(),
                "SUCCESS",
                "User viewed profile",
                httpRequest
        );

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
