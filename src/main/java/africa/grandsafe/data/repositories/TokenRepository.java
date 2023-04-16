package africa.grandsafe.data.repositories;

import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByTokenAndTokenType(String verificationCode, String tokenType);
    @Modifying
    @Query(nativeQuery = true, value = "delete from token t where CURRENT_TIMESTAMP > t.expiry_date")
    void deleteExpiredToken();
    Optional<Token> findByUser(AppUser user);
}
