package africa.grandsafe.utils.cardentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Data {
    private String bin;
    private String brand;
    @JsonIgnore
    private String sub_brand;
    @JsonIgnore
    private String country_code;
    @JsonIgnore
    private String country_name;
    private String card_type;
    private String bank;
    @JsonIgnore
    private String linked_bank_id;
}
