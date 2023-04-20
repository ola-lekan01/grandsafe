package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAppUser(AppUser appUser);
}
