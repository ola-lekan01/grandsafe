package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.LoginRequest;
import africa.grandsafe.data.dtos.request.PasswordRequest;
import africa.grandsafe.data.dtos.request.TokenRefreshRequest;
import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.JwtTokenResponse;
import africa.grandsafe.data.dtos.response.TokenResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import africa.grandsafe.exceptions.AuthException;
import africa.grandsafe.exceptions.TokenException;
import africa.grandsafe.exceptions.UserException;

import java.io.UnsupportedEncodingException;

public interface AuthenticationService {
    AppUser registerNewUserAccount(UserRequest userRequest) throws AuthException, UnsupportedEncodingException;
    void confirmVerificationToken(String verificationToken) throws TokenException;
    Token createVerificationToken(AppUser user, String token, String tokenType);
    JwtTokenResponse login(LoginRequest loginRequest) throws UserException;
    void saveResetPassword(PasswordRequest passwordRequest) throws AuthException, TokenException;
    TokenResponse resendVerificationToken(String token) throws TokenException;
    TokenResponse resendResetPasswordToken(String token) throws TokenException;
    TokenResponse createPasswordResetTokenForUser(String email) throws AuthException;
    JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException;
}
