package africa.grandsafe.utils.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InitiateTransfer {
    @JsonIgnore
    private String status;
    @JsonIgnore
    private String message;
    private InitiateTransferData data;
}
