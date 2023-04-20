package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.SavingRequest;
import africa.grandsafe.data.dtos.response.AllSavingResponse;
import africa.grandsafe.data.dtos.response.SavingResponse;
import africa.grandsafe.data.enums.SavingPlan;
import africa.grandsafe.data.enums.Status;
import africa.grandsafe.data.models.Account;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.repositories.AccountRepository;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.AuthenticationService;
import africa.grandsafe.service.CardService;
import africa.grandsafe.service.SavingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static africa.grandsafe.data.enums.SavingPlan.getPlan;
import static africa.grandsafe.data.enums.Status.ACTIVE;
import static africa.grandsafe.data.enums.Status.SCHEDULED;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {
    private final ModelMapper mapper;
    private final AccountRepository accountRepository;
    private final CardService cardService;
    private final AuthenticationService authenticationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    @Override
    public SavingResponse createSave(UserPrincipal userPrincipal, SavingRequest savingRequest) throws UserException {

        AppUser foundUser = authenticationService.internalFindUserByEmail(userPrincipal.getEmail());

        Account savedSavingAccount = Account.builder()
                .amountToSave(savingRequest.getAmountToSave())
                .cardName(cardService.getCardById(savingRequest.getCardId()))
                .startTime(LocalDateTime.parse(savingRequest.getStartTime(), formatter))
                .endTime(LocalDateTime.parse(savingRequest.getStartTime(), formatter))
                .savingPlan(getPlan(savingRequest.getSavingPlan()))
                .appUser(foundUser)
                .nextDebitDate(LocalDateTime.parse(savingRequest.getStartTime(), formatter))
                .status(SCHEDULED)
                .build();
        Account savedAccount = save(savedSavingAccount);
        foundUser.setAccount(savedAccount);
        authenticationService.saveAUser(foundUser);
        return mapper.map(savedAccount, SavingResponse.class);
    }

    @Override
    public List<AllSavingResponse> getAllSavings(UserPrincipal userPrincipal) throws UserException {
        AppUser foundUser = authenticationService.internalFindUserByEmail(userPrincipal.getEmail());

        var foundSavings = accountRepository.findByAppUser(foundUser);
        List<AllSavingResponse> savingResponses = new ArrayList<>();

        for (Account foundSaving : foundSavings) {
            savingResponses.add(
                    AllSavingResponse.builder()
                            .amountToSave(foundSaving.getAmountToSave())
                            .CardName(foundSaving.getCardName().toString())
                            .savingPlan(foundSaving.getSavingPlan())
                            .startTime(foundSaving.getStartTime())
                            .endTime(foundSaving.getEndTime())
                            .nextDebitDate(foundSaving.getNextDebitDate())
                            .build());
        }
        return savingResponses;
    }

    @Override
    public List<Account> savingListByDate(SavingPlan savingPlan, LocalDateTime currentDateTime) {
        return accountRepository.findByStatusAndSavingPlanAndNextDebitDateIsBefore(ACTIVE, savingPlan, currentDateTime);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public List<Account> savingListByStatus(Status status) {
        return accountRepository.findByStatus(status);
    }
}
