package africa.grandsafe.data.controller;

import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import africa.grandsafe.exceptions.AuthException;
import africa.grandsafe.exceptions.TokenException;
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
}
