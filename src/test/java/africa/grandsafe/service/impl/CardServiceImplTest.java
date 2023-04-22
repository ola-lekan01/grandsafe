package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.CardRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private PayStackService payStackService;
    @Mock
    private ModelMapper modelMapper;
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
        when(cardRepository.findByCardNumberIgnoreCase(cardRequest.getCardNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(userRepositoryMock.findByEmailIgnoreCase(userPrincipalMock.getEmail())).thenReturn(Optional.of(mockedUser));
        when(payStackService.validateCardDetails(cardRequest)).thenReturn(cardDetails);
        when(modelMapper.map(cardRequest, Card.class)).thenReturn(card);
        when(modelMapper.map(savedCard, CardResponse.class)).thenReturn(new CardResponse());

        // Act
        CardResponse response = cardServiceMock.addCard(userPrincipalMock, cardRequest);

        // Assert
        assertEquals(CardResponse.class, response.getClass());

        // Verifying method calls
        verify(userRepositoryMock, times(1)).findByEmailIgnoreCase(userPrincipalMock.getEmail());
        verify(cardRepository, times(1)).findByCardNumberIgnoreCase(cardRequest.getCardNumber());
        verify(cardRepository, times(1)).save(card);
        verify(modelMapper, times(1)).map(cardRequest, Card.class);
        verify(modelMapper, times(1)).map(savedCard, CardResponse.class);
    }
}