package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.AutoSaveRequest;
import africa.grandsafe.data.dtos.request.WithdrawRequest;
import africa.grandsafe.data.models.Transaction;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;

import java.math.BigDecimal;

public interface TransactionService {
    String deposit(AutoSaveRequest autoSaveRequest) throws UserException;
    Transaction saveTransactions(Transaction createdTransaction);
    BigDecimal getWalletBalance(UserPrincipal userPrincipal) throws UserException;
    String withdraw(UserPrincipal userPrincipal, WithdrawRequest withdrawRequest) throws UserException;
}
