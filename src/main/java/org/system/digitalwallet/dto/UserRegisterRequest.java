package org.system.digitalwallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String fullName;
    private String email;

    // ✅ Add role for admin/user registration
    private String role;
}
