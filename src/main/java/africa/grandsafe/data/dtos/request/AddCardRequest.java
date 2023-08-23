package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddCardRequest {
    @NotBlank(message="This field is required")
    private String cardNumber;
    @NotBlank(message="This field is required")
    private String cvv;
    @NotBlank(message="This field is required")
    private String cardName;
    @NotBlank(message="This field is required")
    private String expiryDate;
}
