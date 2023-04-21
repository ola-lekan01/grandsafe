package africa.grandsafe.controller;

import africa.grandsafe.annotations.CurrentUser;
import africa.grandsafe.data.dtos.request.AutoSaveRequest;
import africa.grandsafe.data.dtos.request.DepositRequest;
import africa.grandsafe.data.dtos.request.InitiateTransferRequest;
import africa.grandsafe.data.dtos.request.WithdrawRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.service.TransactionService;
import africa.grandsafe.utils.account.AccountDetail;
import africa.grandsafe.utils.banks.BankListData;
import africa.grandsafe.utils.receipt.InitiateTransferData;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TransactionController {
    private final PayStackService payStackService;
    private final TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<?> getListOfBanks(HttpServletRequest request){
        try{
            List<BankListData> banks = payStackService.getListOfBanks();
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), banks), HttpStatus.OK);
        } catch (CardException | IOException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/resolve_accountname")
    public ResponseEntity<?> getAccountName(@RequestParam("accountNumber") String accountNumber, @RequestParam("bankCode") String bankCode, HttpServletRequest request){
        try{
            AccountDetail accountDetail = payStackService.getAccountName(accountNumber, bankCode);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), accountDetail.getAccount_name()), HttpStatus.OK);
        } catch (CardException | IOException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/initiate_transfer")
    public ResponseEntity<?> initiateTransfer(@RequestBody InitiateTransferRequest transferRequest, HttpServletRequest request){
        try{
            InitiateTransferData transferData = payStackService.initiateTransfer(transferRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), transferData), HttpStatus.OK);
        } catch (CardException | IOException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@CurrentUser UserPrincipal userPrincipal,
                                        HttpServletRequest request) {

        try{
            BigDecimal walletBalance = transactionService.getWalletBalance(userPrincipal);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), walletBalance), HttpStatus.OK);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/withdraw/balance")
    public ResponseEntity<?> withdrawFromBalance(@CurrentUser UserPrincipal userPrincipal,
                                                 @Valid @RequestBody WithdrawRequest withdrawRequest,
                                                 HttpServletRequest request) {
        try{
            var withdrawalResponse = transactionService.withdraw(userPrincipal, withdrawRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), withdrawalResponse), HttpStatus.OK);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
    @PostMapping("/deposit/wallet")
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest depositRequest,
                                     @CurrentUser UserPrincipal userPrincipal,
                                     HttpServletRequest request) {
        AutoSaveRequest autoSaveRequest = AutoSaveRequest.builder()
                .amount(depositRequest.getAmount())
                .cardId(depositRequest.getCardId())
                .currentDate(LocalDateTime.now())
                .email(userPrincipal.getEmail())
                .build();
        try{
            var depositResponse = transactionService.deposit(autoSaveRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Success",
                    request.getRequestURL().toString(), depositResponse), HttpStatus.OK);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
}