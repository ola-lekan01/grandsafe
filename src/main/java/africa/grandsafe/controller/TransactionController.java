package africa.grandsafe.controller;

import africa.grandsafe.data.dtos.request.InitiateTransferRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.exceptions.CardException;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.utils.account.AccountDetail;
import africa.grandsafe.utils.banks.BankListData;
import africa.grandsafe.utils.receipt.InitiateTransferData;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TransactionController {
    private final PayStackService payStackService;

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
}