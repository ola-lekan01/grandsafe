package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    Boolean existsByEmail(String email);
}
