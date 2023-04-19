package africa.grandsafe.data.dtos.response;

import lombok.Data;

@Data
public class CardResponse {
    private Long id;
    private String nameOnCard;
    private String cardNumber;
}