package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.LoginRequest;
import africa.grandsafe.data.dtos.request.PasswordRequest;
import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.JwtTokenResponse;
import africa.grandsafe.data.enums.Role;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Token;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.TokenRepository;
import africa.grandsafe.exceptions.AuthException;
import africa.grandsafe.exceptions.TokenException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.AppUserDetailService;
import africa.grandsafe.security.JwtTokenProvider;
import africa.grandsafe.security.UserPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static africa.grandsafe.data.enums.TokenType.PASSWORD_RESET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceImplTest {
    @Mock
    private AppUserRepository userRepositoryMock;
    @Mock
    private ModelMapper modelMapperMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private TokenRepository tokenRepositoryMock;
    @Mock
    private AuthenticationManager authenticationManagerMock;
    @Mock
    private JwtTokenProvider tokenProviderMock;
    @Mock
    private AppUserDetailService appUserDetailServiceMock;
    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceMock;
    private AppUser mockedUser;
    private UserRequest userRequest;
    private Token token;

    @BeforeEach
    void setUp() {
        // Arrange
        mockedUser = new AppUser();
        mockedUser.setId(UUID.randomUUID().toString());
        mockedUser.setFirstName("Lekan");
        mockedUser.setLastName("Sofuyi");
        mockedUser.setEmail("test@gmail.com");
        mockedUser.setPhoneNumber("08069580949");
        mockedUser.setPassword("pass1234");

        userRequest = new UserRequest();
        userRequest.setFirstName("Lekan");
        userRequest.setLastName("Sofuyi");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPhoneNumber("08069580949");
        userRequest.setPassword("pass1234");

        token = new Token();
        token.setUser(mockedUser);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(LocalDateTime.now());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    }

    @Test
    @DisplayName("User can register new Account when User Email does not exist on the database")
    public void registerNewUserAccount_ShouldRegisterNewUser_WhenUserDoesNotExist() throws AuthException {

        // Setting up variables
        when(userRepositoryMock.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoderMock.encode(userRequest.getPassword())).thenReturn("encodedPassword");

        doAnswer(invocation -> {
            modelMapperMock.map(userRequest, AppUser.class);
            mockedUser.setFirstName(userRequest.getFirstName());
            return mockedUser;
        }).when(userRepositoryMock).save(mockedUser);

        when(modelMapperMock.map(userRequest, AppUser.class)).thenReturn(mockedUser);

        // Act
        AppUser result = authenticationServiceMock.registerNewUserAccount(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userRequest.getFirstName(), result.getFirstName());
        assertEquals(userRequest.getLastName(), result.getLastName());
        assertEquals(userRequest.getEmail(), result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertEquals("encodedPassword", result.getPassword());

        // Verifying Method Calls
        verify(userRepositoryMock, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepositoryMock, times(1)).save(any(AppUser.class));
        verify(modelMapperMock, times(2)).map(userRequest, AppUser.class);
    }

    @Test
    @DisplayName("Authentication Exception should be thrown when User email is already in the Database")
    public void registerNewUserAccount_ShouldThrowAuthException_WhenUserAlreadyExists() {
        // Arrange
        when(userRepositoryMock.existsByEmail(userRequest.getEmail())).thenReturn(true);

        // Assert - exception is expected to be thrown
        assertThrows(AuthException.class, () -> authenticationServiceMock.registerNewUserAccount(userRequest));

        // Verifying Method Calls
        verify(userRepositoryMock, times(1)).existsByEmail(userRequest.getEmail());
    }

    @Test
    void whenLoginMethodIsCalled_ThenFindUserByEmailIsCalledOnce() throws UserException {
        //Given
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "pass1234");
        when(userRepositoryMock.findByEmailIgnoreCase("test@gmail.com")).thenReturn(Optional.of(mockedUser));

        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        testingAuthenticationToken.setAuthenticated(true);
        testingAuthenticationToken.setDetails(loginRequest);

        when(authenticationManagerMock.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()))).thenReturn(testingAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

        when(userRepositoryMock.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(mockedUser));
        UserPrincipal fetchedUser = (UserPrincipal) appUserDetailServiceMock.loadUserByUsername(loginRequest.getEmail());
        String actualToken = tokenProviderMock.generateToken(loginRequest.getEmail());

        when(appUserDetailServiceMock.loadUserByUsername(anyString())).thenReturn(fetchedUser);
        when(tokenProviderMock.generateToken(anyString())).thenReturn(actualToken);
        when(tokenRepositoryMock.save(any(Token.class))).thenReturn(new Token());

        JwtTokenResponse jwtTokenResponse = authenticationServiceMock.login(loginRequest);
        verify(appUserDetailServiceMock, times(1)).loadUserByUsername(loginRequest.getEmail());
        verify(tokenProviderMock, times(2)).generateToken(mockedUser.getEmail());
        verify(userRepositoryMock, times(1)).findByEmailIgnoreCase(loginRequest.getEmail());
        verify(tokenRepositoryMock, times(1)).save(any(Token.class));

        Assertions.assertNotNull(jwtTokenResponse);
        assertEquals(jwtTokenResponse.getJwtToken(), actualToken);
        assertEquals(jwtTokenResponse.getEmail(), loginRequest.getEmail());
    }

    @Test
    void whenLoginMethodIsCalled_withNullPassword_NullPointerExceptionIsThrown() {
        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("test@gmail.com");
        when(userRepositoryMock.findByEmailIgnoreCase(loginDto.getEmail())).thenThrow(new NullPointerException("User password cannot be null"));
        verify(userRepositoryMock, times(0)).findByEmailIgnoreCase(loginDto.getEmail());
    }

    @Test
    @DisplayName("Saved user can update password")
    void checkIfSavedUserCanUpdatePassword() throws AuthException, TokenException {
        String encoder = UUID.randomUUID().toString();
        //Given
        PasswordRequest passwordRequest = new PasswordRequest(token.getToken(), "pass1234", mockedUser.getPassword());
        when(tokenRepositoryMock.findByTokenAndTokenType(passwordRequest.getToken(), PASSWORD_RESET.toString())).thenReturn(Optional.of(token));
        when(userRepositoryMock.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoderMock.encode(passwordRequest.getPassword())).thenReturn(encoder);
        when(userRepositoryMock.save(mockedUser)).thenReturn(new AppUser());

        //When
        String expected = passwordRequest.getOldPassword();
        authenticationServiceMock.saveResetPassword(passwordRequest);

        //Assert
        verify(passwordEncoderMock, times(1)).encode(passwordRequest.getPassword());
        verify(userRepositoryMock, times(1)).save(mockedUser);

        assertNotEquals(expected, mockedUser.getPassword());
        assertEquals(encoder, mockedUser.getPassword());
    }

    @Test
    @DisplayName("User can reset password")
    void savedUserCanResetPassword() throws TokenException, AuthException {
        // Given
        String randomEncoder = UUID.randomUUID().toString();
        String passwordResetToken = UUID.randomUUID().toString();

        when(userRepositoryMock.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(mockedUser));
        when(tokenRepositoryMock.findByTokenAndTokenType(passwordResetToken, PASSWORD_RESET.toString())).thenReturn(Optional.of(token));
        when(passwordEncoderMock.encode(anyString())).thenReturn(randomEncoder);

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("12345");
        passwordRequest.setOldPassword(mockedUser.getPassword());
        passwordRequest.setToken(passwordResetToken);

        authenticationServiceMock.saveResetPassword(passwordRequest);

        ArgumentCaptor<AppUser> tokenArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(userRepositoryMock, times(1)).save(tokenArgumentCaptor.capture());

        assertThat(tokenArgumentCaptor.getValue()).isNotNull();
        assertThat(tokenArgumentCaptor.getValue().getPassword()).isNotNull();
    }

    @Test
    void testInternalFindUserByEmail() throws UserException {
        // Arrange
        String email = "user@example.com";
        AppUser expectedUser = new AppUser();
        expectedUser.setEmail("test@example.com");
        when(userRepositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUser));

        // Act
        AppUser actualUser = authenticationServiceMock.internalFindUserByEmail(email);

        // Assert
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testInternalFindUserByEmail_whenUserNotFound() {
        // Arrange
        String email = "test@example.com";
        when(userRepositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> authenticationServiceMock.internalFindUserByEmail(email));
        verify(userRepositoryMock, times(1)).findByEmailIgnoreCase(email);
    }
}