package africa.grandsafe.data.models;

import africa.grandsafe.data.enums.SavingPlan;
import africa.grandsafe.data.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;
    private BigDecimal amountToSave;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @OneToOne
    @JoinColumn(name = "card_name_id")
    private Card cardName;
    @Enumerated(EnumType.STRING)
    private SavingPlan savingPlan;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextDebitDate;
    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser user;
    @Enumerated(EnumType.STRING)
    private Status status;
}