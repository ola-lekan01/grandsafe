package africa.grandsafe.utils.cardentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardEntity {
    private Data data;
    @JsonIgnore
    private boolean status;
    @JsonIgnore private String message;
}
