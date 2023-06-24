package africa.grandsafe.service.impl;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "This field cannot be empty")
    private String name;

    @NotBlank(message = "This field cannot be empty")
    private String description;

    @NotBlank(message = "This field cannot be empty")
    private String amount;

    private String redirect_url;

    private boolean collect_phone;
}