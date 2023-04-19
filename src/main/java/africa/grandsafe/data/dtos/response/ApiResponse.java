package africa.grandsafe.data.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean isSuccessful;
    private String message;
    private String path;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private Object data;

    public ApiResponse(boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        timeStamp = LocalDateTime.now();
    }

    public ApiResponse(boolean isSuccessful, String message, String path) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        this.path = path;
        timeStamp = LocalDateTime.now();
    }


    public ApiResponse(boolean isSuccessful, String message, String path, Object data) {
        this(isSuccessful, message, path);
        this.data = data;
    }
}
