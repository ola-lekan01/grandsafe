package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AutoSaveRequest;
import africa.grandsafe.data.dtos.request.WithdrawRequest;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.Transaction;
import africa.grandsafe.data.repositories.TransactionRepository;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.AuthenticationService;
import africa.grandsafe.service.CardService;
import africa.grandsafe.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static africa.grandsafe.data.enums.TransactionType.SAVE;
import static africa.grandsafe.data.enums.TransactionType.WITHDRAW;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final AuthenticationService authenticationService;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;

    @Override
    public String deposit(AutoSaveRequest autoSaveRequest) throws UserException {
        AppUser user = authenticationService.internalFindUserByEmail(autoSaveRequest.getEmail());
        Transaction createdTransaction = Transaction.builder()
                .transactionAmount(autoSaveRequest.getAmount())
                .createdAt(LocalDateTime.now())
                .description("Automatic Debit For Saving Plan")
                .fromAccount(cardService.getCardById(autoSaveRequest.getCardId()).getCardNumber())
                .toAccount("Personal Wallet")
                .transactionType(SAVE)
                .appUser(user)
                .build();
        saveTransactions(createdTransaction);
        return String.format("You have Successfully saved %s", autoSaveRequest.getAmount());
    }

    @Override
    public Transaction saveTransactions(Transaction createdTransaction) {
        return transactionRepository.save(createdTransaction);
    }

    @Override
    public BigDecimal getWalletBalance(UserPrincipal userPrincipal) throws UserException {
        var foundUser = authenticationService.internalFindUserByEmail(userPrincipal.getEmail());

        BigDecimal balance = BigDecimal.ZERO;

        var foundTransactions = getListOfTransactionByUser(foundUser);

        for (Transaction foundTransaction : foundTransactions) {
            if (foundTransaction.getTransactionType() == SAVE)
                balance = balance.add(foundTransaction.getTransactionAmount());
            else
                balance = balance.subtract(foundTransaction.getTransactionAmount());
        }
        return balance;
    }

    private List<Transaction> getListOfTransactionByUser(AppUser foundUser) {
        return transactionRepository.findByAppUser(foundUser);
    }

    @Override
    public String withdraw(UserPrincipal userPrincipal, WithdrawRequest withdrawRequest) throws UserException {
        var foundUser = authenticationService.internalFindUserByEmail(userPrincipal.getEmail());

        var walletBalance = getWalletBalance(userPrincipal);
        var result = walletBalance.compareTo(withdrawRequest.getAmount());
        if(result < 0) throw new GenericException("You cannot Withdraw more than your balance!");

        Transaction createdTransaction = Transaction.builder()
                .transactionAmount(withdrawRequest.getAmount())
                .createdAt(LocalDateTime.now())
                .description("Automatic Debit For Saving Plan")
                .fromAccount("Personal Wallet")
                .toAccount("Personal Wallet")
                .transactionType(WITHDRAW)
                .appUser(foundUser)
                .build();
        saveTransactions(createdTransaction);
        return String.format("You have Successfully withdrawn %s", withdrawRequest.getAmount());
    }
}
