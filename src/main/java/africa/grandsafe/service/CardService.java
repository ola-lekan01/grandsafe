package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;

import java.io.IOException;
import java.util.List;

public interface CardService {
    CardResponse addCard(AddCardRequest addCardRequest, UserPrincipal userPrincipal) throws IOException, UserException;
    List<Card> getAllCards();
    Card getCardById(String id);
    String updateCard(String cardId, String userId, AddCardRequest updateCardRequest);
    Object validateCardDetails(AddCardRequest cardDetailsRequest) throws IOException;
    String deleteCardByUserId(String id, String password);
}