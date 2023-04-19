package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;

import java.io.IOException;
import java.util.List;

public interface CardService {
    CardResponse addCard(UserPrincipal userPrincipal, AddCardRequest addCardRequest) throws IOException, UserException;
    List<Card> getAllCards(UserPrincipal userPrincipal) throws UserException;
    Card getCardById(String id);
    Card updateCard(String cardId, AddCardRequest updateCardRequest);
    String deleteCardByUserId(String id, UserPrincipal currentUser, String password) throws UserException;
}