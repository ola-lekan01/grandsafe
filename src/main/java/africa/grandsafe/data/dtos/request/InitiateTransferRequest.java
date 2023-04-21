package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateTransferRequest {
    @NotBlank(message = "This field must not be empty")
    private String bankCode;
    @NotBlank(message = "This field must not be empty")
    private String accountNumber;
    @NotBlank(message = "This field must not be empty")
    private String description;
    @NotBlank(message = "This field must not be empty")
    private Integer amount;
}
