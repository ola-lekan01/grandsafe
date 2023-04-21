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
public class DepositRequest {
    @DecimalMin("100.0") private BigDecimal amount;
    @Min(value = 0, message = "This value must be greater than zero") private String cardId;
}
