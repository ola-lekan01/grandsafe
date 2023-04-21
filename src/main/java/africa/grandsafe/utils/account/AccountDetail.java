package africa.grandsafe.utils.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDetail {
    private String account_name;
    private String account_number;
    @JsonIgnore
    private String bank_id;
}
