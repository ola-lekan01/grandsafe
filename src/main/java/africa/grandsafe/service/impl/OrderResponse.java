package africa.grandsafe.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderResponse {
    private boolean status;
    private String message;
    private PageData data;
    private String paymentUrl;

    @Data
    public static class PageData implements Serializable {
        private String name;
        private String description;
        private int amount;
        private String splitCode;
        private int integration;
        private String domain;
        private String slug;
        private String currency;
        private String type;
        @JsonProperty("collect_phone")
        private boolean collectPhone;
        private boolean active;
        private boolean published;
        private boolean migrate;
        private int id;
        private String createdAt;
        private String updatedAt;
    }
}
