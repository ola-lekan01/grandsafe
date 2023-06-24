package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.request.InitiateTransferRequest;
import africa.grandsafe.service.impl.OrderRequest;
import africa.grandsafe.service.impl.OrderResponse;
import africa.grandsafe.utils.account.AccountDetail;
import africa.grandsafe.utils.banks.BankListData;
import africa.grandsafe.utils.cardentity.Data;
import africa.grandsafe.utils.receipt.InitiateTransferData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

public interface PayStackService {

    Data validateCardDetails(AddCardRequest cardDetailsRequest);
    List<BankListData> getListOfBanks() throws IOException;
    AccountDetail getAccountName(String accountNumber, String bankCode) throws IOException;
    InitiateTransferData initiateTransfer(InitiateTransferRequest transfer) throws IOException;

    OrderResponse makePayments(OrderRequest order) throws JsonProcessingException;
}
