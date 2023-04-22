package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.LoginRequest;
import africa.grandsafe.data.dtos.request.PasswordRequest;
import africa.grandsafe.data.dtos.request.TokenRefreshRequest;
import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.JwtTokenResponse;
import africa.grandsafe.data.dtos.response.TokenResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.TokenRepository;
import africa.grandsafe.exceptions.AuthException;
import africa.grandsafe.exceptions.TokenException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.JwtTokenProvider;
import africa.grandsafe.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static africa.grandsafe.data.enums.Role.USER;
import static africa.grandsafe.data.enums.TokenType.*;
import static africa.grandsafe.utils.Utils.*;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AppUserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AppUser registerNewUserAccount(UserRequest userRequest) throws AuthException {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRequest.getEmail()))) {
            throw new AuthException(String.format("%s is already in use", userRequest.getEmail()));
        }
        AppUser user = modelMapper.map(userRequest, AppUser.class);
        user.setRole(USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return saveAUser(user);
    }

    @Override
    public AppUser saveAUser(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public void confirmVerificationToken(String verificationToken) throws TokenException {
        Token vToken = getAToken(verificationToken, VERIFICATION.toString());

        if (!isValidToken(vToken.getExpiryDate()))
            throw new TokenException("Token has expired");

        AppUser user = vToken.getUser();
        user.setEmailVerified(true);
        user.setWalletNumber(extractSubstring(user.getPhoneNumber()));
        saveAUser(user);
        tokenRepository.delete(vToken);
    }

    @Override
    public Token createVerificationToken(AppUser user, String token, String tokenType) {
        Token verificationToken = new Token(token, user, tokenType);
        return tokenRepository.save(verificationToken);
    }

    private Token getAToken(String token, String tokenType) throws TokenException {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }


    @Transactional
    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) throws UserException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = tokenProvider.generateToken(loginRequest.getEmail());
        AppUser user = internalFindUserByEmail(loginRequest.getEmail());
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();

        Optional<Token> optionalToken = tokenRepository.findByUser(user);

        if (optionalToken.isPresent() && isValidToken(optionalToken.get().getExpiryDate())) {
            jwtTokenResponse.setRefreshToken(optionalToken.get().getToken());
        } else if (optionalToken.isPresent() && !isValidToken(optionalToken.get().getExpiryDate())) {
            Token token = optionalToken.get();
            token.updateToken(UUID.randomUUID().toString(), REFRESH.name());
            jwtTokenResponse.setRefreshToken(tokenRepository.save(token).getToken());
        } else {
            Token refreshToken = new Token(user);
            jwtTokenResponse.setRefreshToken(tokenRepository.save(refreshToken).getToken());
        }
        jwtTokenResponse.setJwtToken(jwtToken);
        jwtTokenResponse.setEmail(user.getEmail());
        return jwtTokenResponse;
    }

    @Override
    public AppUser internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new UserException(format("user not found with email %s", email)));
    }

    @Override
    public void confirmResetPasswordToken(String token) throws TokenException {
        Token vToken = getAToken(token, PASSWORD_RESET.toString());
        if (!isValidToken(vToken.getExpiryDate()))
            throw new TokenException("Token has expired");
    }

    @Override
    public TokenResponse resendVerificationToken(String email) throws TokenException, UserException {
        AppUser user = internalFindUserByEmail(email);
        Optional<Token> token = tokenRepository.findByUser(user);

        if (token.isPresent() && isValidToken(token.get().getExpiryDate()))
            return modelMapper.map(token.get(), TokenResponse.class);

        else{Token newToken = token.get();
            newToken.updateToken(UUID.randomUUID().toString(), PASSWORD_RESET.toString());
            return modelMapper.map(tokenRepository.save(newToken), TokenResponse.class);
        }
    }


    @Override
    public TokenResponse createPasswordResetTokenForUser(String email) throws AuthException {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthException("No user found with email " + email));

        Optional<Token> optionalToken = tokenRepository.findByUser(user);

        if (optionalToken.isPresent() && isValidToken(optionalToken.get().getExpiryDate()))
            return modelMapper.map(optionalToken.get(), TokenResponse.class);

        else if (optionalToken.isPresent() && !isValidToken(optionalToken.get().getExpiryDate())) {
            Token token = optionalToken.get();
            token.updateToken(UUID.randomUUID().toString(), PASSWORD_RESET.toString());
            return modelMapper.map(tokenRepository.save(token), TokenResponse.class);
        }

        Token createdToken = createVerificationToken(user, UUID.randomUUID().toString(), PASSWORD_RESET.toString());
        return modelMapper.map(createdToken, TokenResponse.class);
    }


    @Override
    public JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException {
        String requestRefreshToken = request.getRefreshToken();
        Optional<Token> refreshToken = tokenRepository.findByTokenAndTokenType(requestRefreshToken, REFRESH.toString());
        if (refreshToken.isPresent()) {
            Token token = getRefreshToken(refreshToken.get());
            String jwtToken = tokenProvider.generateToken(refreshToken.get().getUser().getEmail());
            return new JwtTokenResponse(jwtToken, requestRefreshToken, token.getUser().getEmail());
        } else throw new TokenException("Invalid refresh token");
    }

    private Token getRefreshToken(Token token) throws TokenException {
        if (isValidToken(token.getExpiryDate()))
            return token;
        else throw new TokenException("Refresh token was expired. Please make a new sign in request");
    }

    @Override
    public void saveResetPassword(PasswordRequest request) throws TokenException, AuthException {
        if (isNullOrEmpty(request.getToken())) throw new AuthException("Token must cannot be blank");
        Token pToken = getAToken(request.getToken(), PASSWORD_RESET.toString());
        AppUser userToChangePassword = pToken.getUser();
        userToChangePassword.setPassword(passwordEncoder.encode(request.getPassword()));
        saveAUser(userToChangePassword);
        tokenRepository.delete(pToken);
    }
}