package africa.grandsafe.data.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

import static africa.grandsafe.data.enums.TokenType.REFRESH;
import static africa.grandsafe.utils.AppConstants.EXPIRATION;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Token extends RepresentationModel<Token> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = AppUser.class)
    @JoinColumn(nullable = false, name = "app_user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private AppUser user;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    private LocalDateTime expiryDate;

    private String tokenType;

    public Token(String token, AppUser user, String tokenType) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate();
    }

    private LocalDateTime calculateExpiryDate(){
        return LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    public void updateToken(String code){
        this.token = code;
        this.tokenType = REFRESH.toString();
        this.expiryDate = calculateExpiryDate();
    }

    public Token(AppUser user){
        this.token = UUID.randomUUID().toString();
        this.tokenType = REFRESH.toString();
        this.expiryDate = calculateExpiryDate();
        this.user = user;
    }

    public void updateToken(String code, String tokenType){
        this.token = code;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate();
    }
}