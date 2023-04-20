package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.service.PayStackService;
import africa.grandsafe.utils.banks.BankList;
import africa.grandsafe.utils.cardentity.CardEntity;
import africa.grandsafe.utils.cardentity.Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayStackServiceImpl implements PayStackService {
    private final String secretKey = System.getenv("PAY_STACK_SECRET_KEY");
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

    public List<africa.grandsafe.utils.banks.Data> getListOfBanks() throws IOException {

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
            ObjectMapper mapper = new ObjectMapper();
            assert response.body() != null;
            JsonNode jsonNode = mapper.readTree(response.body().string());
            return mapper.convertValue(jsonNode.get("data"), BankList.class).getData();
        }
    }
}
