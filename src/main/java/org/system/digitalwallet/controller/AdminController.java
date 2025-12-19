package org.system.digitalwallet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.system.digitalwallet.dto.UserRegisterRequest;
import org.system.digitalwallet.model.User;
import org.system.digitalwallet.service.AuditService;
import org.system.digitalwallet.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;

    // ✅ CREATE ADMIN (first setup or protected later)
    @PostMapping("/register")
    public ResponseEntity<User> registerAdmin(
            @RequestBody UserRegisterRequest request,
            HttpServletRequest httpRequest
    ) {

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .build(); // ❌ DO NOT set role here

        // ✅ CORRECT METHOD
        User admin = userService.registerAdmin(user);

        // 🔍 AUDIT
        auditService.log(
                admin,
                "REGISTER_ADMIN",
                "USER",
                admin.getId(),
                "SUCCESS",
                "Admin user registered successfully",
                httpRequest
        );

        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }
}
