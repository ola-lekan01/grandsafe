package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class WithdrawRequest {
    @DecimalMin(value = "100.0", message ="This value must be greater than 100" ) private BigDecimal amount;
    private @Min(value = 0, message = "This value must be greater than zero")Long userId;
    private String emailAddress;
}
