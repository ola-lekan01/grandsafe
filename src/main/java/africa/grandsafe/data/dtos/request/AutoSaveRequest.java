package africa.grandsafe.data.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AutoSaveRequest {
    @DecimalMin("100.0") private BigDecimal amount;
    @NotBlank(message="This field is required") private String cardId;
    @NotBlank(message="This field is required") private LocalDateTime currentDate;
    private String email;
}
