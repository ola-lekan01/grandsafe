package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "first name can not be blank")
    private String firstName;
    @NotBlank(message = "first name can not be blank")
    private String lastName;

    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    @NotBlank(message = "Email can not be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password, password must be between 6 to 20 characters")
    @NotBlank(message = "Password can not be blank")
    private String password;
}
