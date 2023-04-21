package africa.grandsafe.utils.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class AccountDetails {
    private AccountDetail data;
    @JsonIgnore
    private boolean status;
    @JsonIgnore private String message;
}
