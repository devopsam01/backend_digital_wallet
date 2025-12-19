package org.system.digitalwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String role;
}
