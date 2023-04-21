package africa.grandsafe.utils.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransferReceiptDataDetail {
    @JsonIgnore
    private String authorization_code;
    private String account_name;
    private String bank_code;
    private String bank_name;
    private String account_number;
}

