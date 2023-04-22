package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    private String token;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    private String oldPassword;
}