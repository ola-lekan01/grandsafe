package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingRequest {
    @DecimalMin("100") private BigDecimal amountToSave;
    @NotBlank(message="This field is required") private String startTime;
    @NotBlank(message="This field is required") private String cardId;
    @NotBlank(message="This field is required") private String endTime;
    @NotBlank(message="This field is required") private String savingPlan;
}