package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.CardRepository;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.CardService;
import africa.grandsafe.utils.cardentity.CardEntity;
import africa.grandsafe.utils.cardentity.Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final ModelMapper modelMapper;
    private final CardRepository cardRepository;
    private final AppUserRepository userRepository;
    private final String secretKey = System.getenv("PAY_STACK_SECRET_KEY");
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public CardResponse addCard(AddCardRequest addCardRequest, UserPrincipal userPrincipal) throws UserException {
        if (cardRepository.findByCardNumberIgnoreCase(addCardRequest.getCardNumber())
                .isPresent()) throw new GenericException("Card already exists");

        AppUser user = internalFindUserByEmail(userPrincipal.getEmail());

        try {
            validateCardDetails(addCardRequest);
            Data cardDetails;
            cardDetails = (Data) validateCardDetails(addCardRequest);
            Card card = Card.builder()
                    .cardNumber(addCardRequest.getCardNumber())
                    .cvv(addCardRequest.getCvv())
                    .nameOnCard(addCardRequest.getCardName())
                    .expiryDate(addCardRequest.getExpiryDate())
                    .bankName(cardDetails.getBank())
                    .brand(cardDetails.getBrand())
                    .bin(cardDetails.getBin())
                    .user(user)
                    .card_type(cardDetails.getCard_type())
                    .build();
            Card savedCard = cardRepository.save(card);
            return modelMapper.map(savedCard, CardResponse.class);
        } catch (GenericException exception) {
            throw new GenericException("Invalid Details");
        }
    }

    private AppUser internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UserException("User Does not Exist"));

    }

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public Card getCardById(String id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardException("Card Does not exist"));
    }

    @Override
    public String updateCard(String cardId, String userId, AddCardRequest updateCardRequest) {
        return null;
    }

    @Override
    public Object validateCardDetails(AddCardRequest cardDetailsRequest) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.paystack.co/decision/bin/"
                        + cardDetailsRequest.getCardNumber().substring(0, 6))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .build();

        return client.get()
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .readValue(responseBody, CardEntity.class).getData();
                    } catch (JsonProcessingException exception) {
                        throw new GenericException("Error processing response body: " + exception.getMessage());
                    }
                })
                .block();
    }


    @Override
    public String deleteCardByUserId(String id, String password) {
        return null;
    }
}
