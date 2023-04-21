package africa.grandsafe.data.repositories;

import africa.grandsafe.data.enums.SavingPlan;
import africa.grandsafe.data.enums.Status;
import africa.grandsafe.data.models.Account;
import africa.grandsafe.data.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByAppUser(AppUser appuser);

    List<Account> findByStatusAndSavingPlanAndNextDebitDateIsBefore(Status status,
                                                                    SavingPlan savingPlan,
                                                                    LocalDateTime currentTime);

    List<Account> findByStatus(Status scheduled);
}
