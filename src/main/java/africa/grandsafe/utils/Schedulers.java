package africa.grandsafe.utils;

import africa.grandsafe.data.dtos.request.AutoSaveRequest;
import africa.grandsafe.data.models.Account;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.service.SavingService;
import africa.grandsafe.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

import static africa.grandsafe.data.enums.SavingPlan.*;
import static africa.grandsafe.data.enums.Status.*;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Schedulers {

    private final SavingService savingService;
    private final TransactionService transactionService;

    @Scheduled(cron = "0 55 23 * * *")
    public void scheduledToActive() {
        List<Account> scheduledStatuses = savingService.savingListByStatus(SCHEDULED);
        for (Account dailyRunner : scheduledStatuses) {
            if (dailyRunner.getStartTime().isBefore(LocalDateTime.now())) dailyRunner.setStatus(ACTIVE);
            savingService.save(dailyRunner);
        }
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void dailyAutomaticDebitForDailySavings() throws UserException {
        List<Account> dailyRunners = savingService.savingListByDate(DAILY_SAVINGS, LocalDateTime.now());

        for (Account dailyRunner : dailyRunners) {
            AutoSaveRequest autoSaveRequest = AutoSaveRequest.builder()
                    .amount(dailyRunner.getAmountToSave())
                    .cardId(dailyRunner.getCardName().getId())
                    .email(dailyRunner.getAppUser().getEmail())
                    .build();
            transactionService.deposit(autoSaveRequest);
            dailyRunner.setNextDebitDate(LocalDateTime.now().plusHours(23));
            if (dailyRunner.getNextDebitDate().isAfter(dailyRunner.getEndTime())) dailyRunner.setStatus(IN_ACTIVE);
            savingService.save(dailyRunner);
        }
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void dailyAutomaticDebitForWeeklySavings() throws UserException {
        List<Account> dailyRunners = savingService.savingListByDate(WEEKLY_SAVING, LocalDateTime.now());
        for (Account dailyRunner : dailyRunners) {
            AutoSaveRequest autoSaveRequest = AutoSaveRequest.builder()
                    .amount(dailyRunner.getAmountToSave())
                    .cardId(dailyRunner.getCardName().getId())
                    .email(dailyRunner.getAppUser().getEmail())
                    .build();
            transactionService.deposit(autoSaveRequest);
            dailyRunner.setNextDebitDate(LocalDateTime.now().plusDays(7));
            if (dailyRunner.getNextDebitDate().isAfter(dailyRunner.getEndTime())) dailyRunner.setStatus(IN_ACTIVE);
            savingService.save(dailyRunner);
        }
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void dailyAutomaticDebitForMonthlySavings() throws UserException {
        List<Account> dailyRunners = savingService.savingListByDate(MONTHLY_SAVINGS, LocalDateTime.now());
        for (Account dailyRunner : dailyRunners) {
            AutoSaveRequest autoSaveRequest = AutoSaveRequest.builder()
                    .amount(dailyRunner.getAmountToSave())
                    .cardId(dailyRunner.getCardName().getId())
                    .email(dailyRunner.getAppUser().getEmail())
                    .build();
            transactionService.deposit(autoSaveRequest);
            dailyRunner.setNextDebitDate(LocalDateTime.now().plusDays(29));
            if (dailyRunner.getNextDebitDate().isAfter(dailyRunner.getEndTime())) dailyRunner.setStatus(IN_ACTIVE);
            savingService.save(dailyRunner);
        }
    }
}