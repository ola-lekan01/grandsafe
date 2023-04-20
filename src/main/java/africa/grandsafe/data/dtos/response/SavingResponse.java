package africa.grandsafe.data.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingResponse {
    private String id;
    private String amountToSave;
    private final String message = "Successful";
}