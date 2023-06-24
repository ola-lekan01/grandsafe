package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.CardRepository;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.utils.cardentity.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private PayStackService payStackServiceMock;
    @Mock
    private ModelMapper modelMapperMock;
    @Mock
    private AppUserRepository userRepositoryMock;
    @InjectMocks
    private CardServiceImpl cardServiceMock;
    private UserPrincipal userPrincipalMock;
    private AppUser mockedUser;
    private Data cardDetails;
    private Card card;
    private Card savedCard;
    private AddCardRequest cardRequest;

    @BeforeEach
    void setUp() {
        List<GrantedAuthority> authorities = Collections.singletonList(mock(GrantedAuthority.class));
        userPrincipalMock = new UserPrincipal(UUID.randomUUID().toString(),
                "Lekan",
                "Sofuyi",
                "test@gmail.com",
                "pass1234",
                true,
                authorities);

        this.card = new Card();
        this.savedCard = new Card();

        cardRequest = new AddCardRequest("1234567890123456", "123", "Lekan Sofuyi", "10/25");

        mockedUser = new AppUser();
        mockedUser.setId(UUID.randomUUID().toString());
        mockedUser.setFirstName("Lekan");
        mockedUser.setLastName("Sofuyi");
        mockedUser.setEmail("test@gmail.com");
        mockedUser.setPhoneNumber("08069580949");
        mockedUser.setPassword("pass1234");

        cardDetails = new Data();
        cardDetails.setBin("bin");
        cardDetails.setBank("bank name");
        cardDetails.setCard_type("Master Card");
        cardDetails.setBrand("Master");

    }

    @Test
    public void testAddCard_success() throws UserException {
        // Arrange
        when(cardRepositoryMock.findByCardNumberIgnoreCase(cardRequest.getCardNumber())).thenReturn(Optional.empty());
        when(cardRepositoryMock.save(any(Card.class))).thenReturn(savedCard);
        when(userRepositoryMock.findByEmailIgnoreCase(userPrincipalMock.getEmail())).thenReturn(Optional.of(mockedUser));
        when(payStackServiceMock.validateCardDetails(cardRequest)).thenReturn(cardDetails);
        when(modelMapperMock.map(cardRequest, Card.class)).thenReturn(card);
        when(modelMapperMock.map(savedCard, CardResponse.class)).thenReturn(new CardResponse());

        // Act
        CardResponse response = cardServiceMock.addCard(userPrincipalMock, cardRequest);

        // Assert
        assertEquals(CardResponse.class, response.getClass());

        // Verifying method calls
        verify(userRepositoryMock, times(1)).findByEmailIgnoreCase(userPrincipalMock.getEmail());
        verify(cardRepositoryMock, times(1)).findByCardNumberIgnoreCase(cardRequest.getCardNumber());
        verify(cardRepositoryMock, times(1)).save(card);
        verify(modelMapperMock, times(1)).map(cardRequest, Card.class);
        verify(modelMapperMock, times(1)).map(savedCard, CardResponse.class);
    }

    @Test
    void should_DeleteCard_When_CardExistsAndCorrectCredentialsProvided() throws UserException, CardException {
        // Arrange
        String cardId = "test123";
        String password = "pass1234";
        Card card = new Card();
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepositoryMock.findByEmailIgnoreCase(userPrincipalMock.getEmail())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoderMock.matches(password, mockedUser.getPassword())).thenReturn(true);

        // Act
        String result = cardServiceMock.deleteCardByUserId(cardId, userPrincipalMock, password);

        // Assert
        assertEquals("Card Deleted Successfully", result);
        verify(passwordEncoderMock, times(1)).matches(anyString(), anyString());
        verify(cardRepositoryMock, times(1)).delete(card);
    }

    @Test
    void should_ThrowCardException_When_CardDoesNotExist() throws CardException {
        // Arrange
        String cardId = "test123";
        String password = "pass1234";
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.empty());
        when(passwordEncoderMock.matches(password, mockedUser.getPassword())).thenReturn(true);

        // Act & Assert
        assertThrows(CardException.class, () -> cardServiceMock.deleteCardByUserId(cardId, userPrincipalMock, password));
    }

    @Test
    void should_ThrowCardException_When_IncorrectPasswordEntered() throws CardException {
        // Arrange
        String cardId = "test123";
        String password = "incorrect-password";
        Card card = new Card();
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepositoryMock.findByEmailIgnoreCase(userPrincipalMock.getEmail())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoderMock.matches(password, mockedUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(CardException.class, () -> cardServiceMock.deleteCardByUserId(cardId, userPrincipalMock, password));
    }
}