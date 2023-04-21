package africa.grandsafe.utils.banks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BankList {
    private List<BankListData> data;
    @JsonIgnore
    private boolean status;
    @JsonIgnore private String message;
}