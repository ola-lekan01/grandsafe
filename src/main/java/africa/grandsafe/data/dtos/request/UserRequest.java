package africa.grandsafe.data.dtos.request;

import africa.grandsafe.exceptions.GenericException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "first name can not be blank")
    private String firstName;
    @NotBlank(message = "first name can not be blank")
    private String lastName;

    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    @NotBlank(message = "Email can not be blank")
    private String email;

    @NotBlank(message = "phoneNumber can not be blank")
    @Pattern(regexp = "(\\+234|234|0)(701|702|703|704|" +
            "705|706|707|708|709|802|803|804|805|806|807|808|809|" +
            "810|811|812|813|814|815|816|817|818|819|909|908|901|902" +
            "|903|904|905|906|907)([0-9]{7})", message = "Phone Number Invalid")
    private String phoneNumber;

    @Size(min = 6, max = 20, message = "Invalid password, password must be between 6 to 20 characters")
    @NotBlank(message = "Password can not be blank")
    private String password;

    private String imageURL;


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