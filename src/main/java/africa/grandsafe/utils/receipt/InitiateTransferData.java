package africa.grandsafe.utils.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InitiateTransferData {
    private String reference;
    @JsonIgnore
    private String integration;
    @JsonIgnore private String domain;
    private String amount;
    @JsonIgnore private String currency;
    @JsonIgnore private String source;
    @JsonIgnore private String reason;
    private String recipient;
    private String status;
    private String transfer_code;
    @JsonIgnore private String id;
    @JsonIgnore private String createdAt;
    @JsonIgnore private String updatedAt;
}
