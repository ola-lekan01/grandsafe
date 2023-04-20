package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
