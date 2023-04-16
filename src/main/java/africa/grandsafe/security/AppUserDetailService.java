package africa.grandsafe.security;

import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {
    private final AppUserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new UsernameNotFoundException(format("User not found with email %s", email)));
        return UserPrincipal.create(user);
    }
}
