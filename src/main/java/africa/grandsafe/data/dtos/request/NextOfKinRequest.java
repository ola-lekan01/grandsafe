package africa.grandsafe.data.dtos.request;


import africa.grandsafe.exceptions.GenericException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.regex.Matcher;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class NextOfKinRequest {
    @NotBlank(message = "this field is required")
    private String fullName;
    @NotBlank(message = "this field is required")
    private @Pattern(regexp = "(\\+234|234|0)(701|702|703|704|" +
            "705|706|707|708|709|802|803|804|805|806|807|808|809|" +
            "810|811|812|813|814|815|816|817|818|819|909|908|901|902" +
            "|903|904|905|906|907)([0-9]{7})", message = "Phone Number Invalid") String phoneNumber;
    @NotBlank(message = "this field is required")
    private @Email String email;
    @NotBlank(message = "this field is required")
    private String relationship;

    public NextOfKinRequest(String fullName, String phoneNumber, String email, String relationship) {
        this.fullName = fullName;
        this.email = email;
        this.relationship = relationship;
        Matcher matcher = getMatcher(phoneNumber);
        if(!matcher.matches()) throw new GenericException("Invalid Number Format");
        this.phoneNumber = phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        Matcher matcher = getMatcher(phoneNumber);
        if(!matcher.matches()) throw new GenericException("Invalid Number Format");
        this.phoneNumber = phoneNumber;
    }

    private Matcher getMatcher(String phoneNumber) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(getRegex());
        return pattern.matcher(phoneNumber);
    }

    private static String getRegex() {
        return "(\\+234|234|0)(701|702|703|704|" +
                "705|706|707|708|709|802|803|804|805|806|807|808|809|" +
                "810|811|812|813|814|815|816|817|818|819|909|908|901|902" +
                "|903|904|905|906|907)([0-9]{7})";
    }
}