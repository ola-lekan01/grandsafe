package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.NextOfKin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NextOfKinRepository extends JpaRepository<NextOfKin, String> {
    NextOfKin findByAppUser(AppUser user);
}
