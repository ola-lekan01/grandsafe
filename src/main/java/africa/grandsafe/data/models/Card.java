package africa.grandsafe.data.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;
    private String cardNumber;
    private String cvv;
    private String nameOnCard;
    private String expiryDate;
    @ManyToOne
    @JoinColumn(name="app_user_id")
    private AppUser user;
    private String bin;
    private String brand;
    private String card_type;
    private String bankName;

    @Override
    public String toString() {
        return bankName;
    }
}
