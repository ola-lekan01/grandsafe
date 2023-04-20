package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.data.dtos.request.InitiateTransferRequest;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.utils.account.AccountDetail;
import africa.grandsafe.utils.account.AccountDetails;
import africa.grandsafe.utils.banks.BankList;
import africa.grandsafe.utils.banks.BankListData;
import africa.grandsafe.utils.cardentity.CardEntity;
import africa.grandsafe.utils.cardentity.Data;
import africa.grandsafe.utils.receipt.InitiateTransfer;
import africa.grandsafe.utils.receipt.InitiateTransferData;
import africa.grandsafe.utils.receipt.TransferReceipt;
import africa.grandsafe.utils.receipt.TransferReceiptData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayStackServiceImpl implements PayStackService {
    private final String secretKey = System.getenv("PAY_STACK_SECRET_KEY");
    private static final String PAYSTACK_RESOLVE_URL = "https://api.paystack.co/bank/resolve";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Data validateCardDetails(AddCardRequest cardDetailsRequest) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.paystack.co/decision/bin/"
                        + cardDetailsRequest.getCardNumber().substring(0, 6))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .build();

        return client.get()
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .readValue(responseBody, CardEntity.class).getData();
                    } catch (JsonProcessingException exception) {
                        throw new GenericException("Error processing response body: " + exception.getMessage());
                    }
                })
                .block();
    }

    @Override
    public List<BankListData> getListOfBanks() throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.paystack.co")
                .addPathSegment("bank")
                .addQueryParameter("country", "nigeria")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + secretKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(response.body().string(), BankList.class).getData();
        } catch (JsonProcessingException exception) {
            throw new GenericException("Error processing response body: " + exception.getMessage());
        }
    }

    @Override
    public AccountDetail getAccountName(String accountNumber, String bankCode) throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(PAYSTACK_RESOLVE_URL)).newBuilder();
        urlBuilder.addQueryParameter("account_number", accountNumber);
        urlBuilder.addQueryParameter("bank_code", bankCode);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .header("Authorization", "Bearer " + secretKey)
                .build();

        try(Response response = client.newCall(request).execute()){
            assert response.body() != null;
            String responseBody = response.body().string();
            log.info(responseBody);
            return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(responseBody, AccountDetails.class).getData();
        } catch (JsonProcessingException exception) {
            throw new GenericException("Error processing response body: " + exception.getMessage());
        }
    }

    private TransferReceiptData generateTransferReceipt(InitiateTransferRequest transfer) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl PAYSTACK_ENDPOINT = HttpUrl.parse("https://api.paystack.co/transferrecipient");
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json");

        var foundAccount = getAccountName(transfer.getAccountNumber(), transfer.getBankCode());

        JSONObject json = new JSONObject();
        json.put("type", "nuban");
        json.put("bank_code", transfer.getBankCode());
        json.put("account_number", foundAccount.getAccount_number());
        json.put("name", foundAccount.getAccount_name());
        json.put("currency", "NGN");

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        assert PAYSTACK_ENDPOINT != null;
        HttpUrl url = PAYSTACK_ENDPOINT.newBuilder()
                .build();

        Request request = requestBuilder
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + secretKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            log.info("Transfer Receipt Data" + responseBody);
            return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(responseBody, TransferReceipt.class).getData();

        } catch (JsonProcessingException exception) {
            throw new GenericException("Error processing response body: " + exception.getMessage());
        }
    }

    @Override
    public InitiateTransferData initiateTransfer(InitiateTransferRequest transfer) throws IOException {
        var receipt_code = generateTransferReceipt(transfer);
        String reference = UUID.randomUUID().toString();
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        json.put("source", "balance");
        json.put("amount", transfer.getAmount());
        json.put("reference", reference);
        json.put("recipient", receipt_code.getRecipient_code());
        json.put("reason", transfer.getDescription());

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));


        Request request = new Request.Builder()
                .url("https://api.paystack.co/transfer")
                .post(body)
                .addHeader("Authorization", "Bearer " + secretKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            log.info("Initiate Transfer Receipt Data" + responseBody);
            return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(responseBody, InitiateTransfer.class).getData();
        } catch (JsonProcessingException exception) {
            throw new GenericException("Error processing response body: " + exception.getMessage());
        }
    }
}