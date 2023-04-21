package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.SavingRequest;
import africa.grandsafe.data.dtos.response.AllSavingResponse;
import africa.grandsafe.data.dtos.response.SavingResponse;
import africa.grandsafe.data.enums.SavingPlan;
import africa.grandsafe.data.enums.Status;
import africa.grandsafe.data.models.Account;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;

public interface SavingService {
    SavingResponse createSave(UserPrincipal userPrincipal, SavingRequest savingRequest) throws UserException;

    List<AllSavingResponse> getAllSavings(UserPrincipal userPrincipal) throws UserException;

    List<Account> savingListByDate(SavingPlan savingPlan, LocalDateTime currentDateTime);

    Account save(Account dailyRunners);

    List<Account> savingListByStatus(Status scheduled);
}