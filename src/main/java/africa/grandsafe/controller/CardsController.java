package africa.grandsafe.controller;

import africa.grandsafe.annotations.CurrentUser;
import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.data.dtos.response.CardResponse;
import africa.grandsafe.data.models.Card;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/card")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class CardsController {
    private final CardService cardService;

    @PostMapping("/addCard")
    public ResponseEntity<?> addCard(@CurrentUser UserPrincipal principal,
                                     @Valid @RequestBody AddCardRequest addCardRequest,
                                     HttpServletRequest request){
        try{
            CardResponse addedCard = cardService.addCard(principal, addCardRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Card added Successfully",
                    request.getRequestURL().toString(), addedCard), HttpStatus.CREATED);
        } catch (IOException | UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("getCard/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable String cardId,
                                         HttpServletRequest request){
        try{
            Card addedCard = cardService.getCardById(cardId);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), addedCard), HttpStatus.OK);
        } catch (CardException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
    @GetMapping("get-all-card")
    public ResponseEntity<?> getAllCard(@CurrentUser UserPrincipal principal,
                                         HttpServletRequest request){
        try{
            List<Card> cards = cardService.getAllCards(principal);
            return new ResponseEntity<>(new ApiResponse(true, "Card retrieved Successfully",
                    request.getRequestURL().toString(), cards), HttpStatus.OK);
        } catch (CardException | UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @PutMapping("update/{cardId}")
    public ResponseEntity<?> updateCard(@Valid @RequestBody AddCardRequest cardRequest,
                                        @PathVariable String cardId,
                                        HttpServletRequest request){
        try{
            Card updateCard = cardService.updateCard(cardId,  cardRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Card updated Successfully",
                    request.getRequestURL().toString(), updateCard), HttpStatus.OK);
        } catch (CardException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @DeleteMapping("deleteCard/{cardId}")
    public ResponseEntity<?> deleteCardUser(@Valid @PathVariable String cardId,
                                            @RequestParam String password,
                                            @CurrentUser UserPrincipal currentUser,
                                            HttpServletRequest request) {
        try{
            String deleteCard = cardService.deleteCardByUserId(cardId, currentUser, password);
            return new ResponseEntity<>(new ApiResponse(true, "Card deleted Successfully",
                    request.getRequestURL().toString(), deleteCard), HttpStatus.OK);
        } catch (CardException | UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
}