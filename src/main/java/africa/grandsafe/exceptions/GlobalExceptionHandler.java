package africa.grandsafe.exceptions;

import africa.grandsafe.data.dtos.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handleGenericException(GenericException genericException,
                                                    HttpServletRequest httpServletRequest){
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .isSuccessful(false)
                .data(genericException.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException genericException,
                                                    HttpServletRequest httpServletRequest){
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .isSuccessful(false)
                .data(genericException.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
