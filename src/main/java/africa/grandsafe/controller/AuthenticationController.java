package africa.grandsafe.controller;

import africa.grandsafe.data.dtos.request.LoginRequest;
import africa.grandsafe.data.dtos.request.PasswordRequest;
import africa.grandsafe.data.dtos.request.TokenRefreshRequest;
import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.data.dtos.response.JwtTokenResponse;
import africa.grandsafe.data.dtos.response.TokenResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import africa.grandsafe.exceptions.AuthException;
import africa.grandsafe.exceptions.TokenException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.service.AuthenticationService;
import africa.grandsafe.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static africa.grandsafe.data.enums.TokenType.VERIFICATION;
import static africa.grandsafe.utils.EmailTemplate.buildEmail;
import static africa.grandsafe.utils.EmailTemplate.resetPassword;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/grandsafe/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        try {
            AppUser user = authenticationService.registerNewUserAccount(userRequest);
            String token = UUID.randomUUID().toString();
            Token vToken = authenticationService.createVerificationToken(user, token, VERIFICATION.toString());

            ResponseEntity<ApiResponse> methodLinkBuilder = methodOn(AuthenticationController.class)
                    .verifyUser(vToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("user-verification");
            emailService.sendEmail(userRequest.getEmail(),
                    buildEmail(user.getFirstName(), verificationLink.getHref()));

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AuthException | UnsupportedEncodingException exception) {
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam("token") String token) {
        try {
            authenticationService.confirmVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User is successfully verified"), HttpStatus.OK);
        } catch (TokenException exception) {
            return new ResponseEntity<>(new ApiResponse
                    (false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtTokenResponse authenticationDetail = authenticationService.login(loginRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User is successfully logged in",
                    authenticationDetail ), HttpStatus.OK);
        }catch (UserException exception){
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset-token")
    public ResponseEntity<?> createResetPasswordToken(@RequestParam("email") String email) {
        try {
            TokenResponse passwordResetToken = authenticationService.createPasswordResetTokenForUser(email);
            AppUser user = authenticationService.internalFindUserByEmail(email);

            ResponseEntity<ApiResponse> methodLinkBuilder = methodOn(AuthenticationController.class)
                    .verifyToken(passwordResetToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("password-reset");
            emailService.sendEmail(email,
                    resetPassword(user.getFirstName(), verificationLink.getHref()));

            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED);
        } catch (AuthException | UserException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify-reset-password-token")
    public ResponseEntity<ApiResponse> verifyToken(@RequestParam("token") String token) {
        try {
            authenticationService.confirmResetPasswordToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "Token Verified Successfully"), HttpStatus.OK);
        } catch (TokenException exception) {
            return new ResponseEntity<>(new ApiResponse
                    (false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse> updatePassword(@RequestParam("token") String token,
                                                      @Valid @RequestBody PasswordRequest passwordRequest) {
        try {
            passwordRequest.setToken(token);
            authenticationService.saveResetPassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User password is successfully updated"), HttpStatus.OK);
        } catch (AuthException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            JwtTokenResponse jwtTokenResponse = authenticationService.refreshToken(request);
            return new ResponseEntity<>(jwtTokenResponse, HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}