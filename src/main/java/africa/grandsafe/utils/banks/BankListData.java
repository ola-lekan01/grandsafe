package africa.grandsafe.utils.banks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankListData {
    private String name;
    private String code;
    @JsonIgnore
    private String id;
    @JsonIgnore private String slug;
    @JsonIgnore private String longcode;
    @JsonIgnore private String gateway;
    @JsonIgnore private Boolean pay_with_bank;
    @JsonIgnore private Boolean active;
    @JsonIgnore private String country;
    @JsonIgnore private String currency;
    @JsonIgnore private String type;
    @JsonIgnore private Boolean is_deleted;
    @JsonIgnore private String createdAt;
    @JsonIgnore private String updatedAt;
}