package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    Optional<Card> findByCardNumberIgnoreCase(String cardNumber);
}
