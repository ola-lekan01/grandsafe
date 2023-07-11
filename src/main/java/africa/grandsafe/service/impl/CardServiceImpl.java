package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.request.UserRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.dtos.response.OnBoardingResponse;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.data.repositories.AppUserRepository;
import africa.grandsafe.data.repositories.CardRepository;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.CardService;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.utils.cardentity.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final ModelMapper mapper;
    private final CardRepository cardRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PayStackService payStackService;


    @Override
    public CardResponse addCard(UserPrincipal userPrincipal, AddCardRequest addCardRequest) throws UserException {
        if (cardRepository.findByCardNumberIgnoreCase(addCardRequest.getCardNumber()).isPresent()) throw new GenericException("Card already exists");
        AppUser user = internalFindUserByEmail(userPrincipal.getEmail());
        Data cardDetails = payStackService.validateCardDetails(addCardRequest);
        Card card = mapper.map(addCardRequest, Card.class);
        card.setBankName(cardDetails.getBank());
        card.setBrand(cardDetails.getBrand());
        card.setBin(cardDetails.getBin());
        card.setCard_type(cardDetails.getCard_type());
        card.setUser(user);
        Card savedCard = cardRepository.save(card);
        return mapper.map(savedCard, CardResponse.class);
    }

    private AppUser internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UserException("User Does not Exist"));

    }

    @Override
    public List<Card> getAllCards(UserPrincipal userPrincipal) throws UserException {
        AppUser user = internalFindUserByEmail(userPrincipal.getEmail());
        return cardRepository.findAllByUser(user);
    }

    @Override
    public Card getCardById(String id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardException("Card Does not exist"));
    }

    @Override
    public Card updateCard(String cardId, AddCardRequest updateCardRequest) {
        Card card = getCardById(cardId);
        mapper.map(updateCardRequest, card);
        return cardRepository.save(card);
    }

    @Override
    public String deleteCardByUserId(String cardId, UserPrincipal userPrincipal, String password) throws UserException, CardException {
        Card card = getCardById(cardId);
        if (card == null) throw new CardException("Card with ID " + cardId + " does not exist");
        AppUser user = internalFindUserByEmail(userPrincipal.getEmail());
        boolean isValid = passwordEncoder.matches(password, user.getPassword());
        if (isValid) {
            cardRepository.delete(card);
            return "Card Deleted Successfully";
        } else throw new CardException("Incorrect password entered");
    }

    public OnBoardingResponse onBoardUser(UserRequest request){
        return mapper.map(userRepository.save(mapper.map(request, AppUser.class)), OnBoardingResponse.class);
    }

    public OnBoardingResponse onBoard(UserRequest request){
        AppUser user = new AppUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setImageUrl(request.getImageURL());
        user.setPhoneNumber(request.getPhoneNumber());
        AppUser savedUser = userRepository.save(user);

        OnBoardingResponse response = new OnBoardingResponse();
        response.setFirstName(savedUser.getFirstName());
        response.setEmail(savedUser.getEmail());
        response.setId(savedUser.getId());
        return response;
    }
}