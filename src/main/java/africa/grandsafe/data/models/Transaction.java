package africa.grandsafe.data.models;

import africa.grandsafe.data.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;
    private BigDecimal transactionAmount;
    private String description;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String toAccount;
    private String fromAccount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
}
