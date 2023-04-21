package africa.grandsafe.controller;

import africa.grandsafe.annotations.CurrentUser;
import africa.grandsafe.data.dtos.request.SavingRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.SavingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/saving")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class SavingController {
    private final SavingService savingService;

    @PostMapping("")
    public ResponseEntity<?> createSavingPlan(@CurrentUser UserPrincipal userPrincipal,
                                              @Valid @RequestBody SavingRequest savingRequest,
                                              HttpServletRequest request) {

        try {
            var savedService = savingService.createSave(userPrincipal, savingRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Card added Successfully",
                    request.getRequestURL().toString(), savedService), HttpStatus.CREATED);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllSavingPlan(@CurrentUser UserPrincipal userPrincipal,
                                              HttpServletRequest request){
        try {
            var allUserSavings = savingService.getAllSavings(userPrincipal);
            return new ResponseEntity<>(new ApiResponse(true, "Card added Successfully",
                    request.getRequestURL().toString(), allUserSavings), HttpStatus.CREATED);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
}