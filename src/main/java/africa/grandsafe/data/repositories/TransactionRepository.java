package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
