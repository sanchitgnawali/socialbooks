package io.sanchit.socialbook.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "First name is required")
    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @NotEmpty(message = "Last name is required")
    private String lastName;

    @NotBlank
    @NotEmpty(message = "Email name is required")
    @Email(message = "Email not correctly formatted")
    private String email;

    @NotBlank
    @NotEmpty(message = "Password name is required")
    @Size(min = 8, message = "Password should be at least 8 characters long.")
    private String password;
}
