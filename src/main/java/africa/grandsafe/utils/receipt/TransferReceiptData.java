package africa.grandsafe.utils.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransferReceiptData {
    private String recipient_code;
    @JsonIgnore
    private String active;
    @JsonIgnore
    private String currency;
    @JsonIgnore
    private String createdAt;
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String integration;
    @JsonIgnore
    private String domain;
    @JsonIgnore
    private String metadata;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private String type;
    @JsonIgnore
    private String updatedAt;
    @JsonIgnore
    private String is_deleted;
    @JsonIgnore
    private String isDeleted;
    @JsonIgnore
    private String description;
    @JsonIgnore
    private String email;
    private TransferReceiptDataDetail details;
}