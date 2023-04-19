package africa.grandsafe.controller;

import africa.grandsafe.annotations.CurrentUser;
import africa.grandsafe.data.dtos.request.NextOfKinRequest;
import africa.grandsafe.data.dtos.response.ApiResponse;
import africa.grandsafe.data.models.NextOfKin;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.NextOfKinService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/grandsafe/next-of-kin")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class NextOfKinController {
    private final NextOfKinService nextOfKinService;

    @PostMapping("/create")
    public ResponseEntity<?> saveNextOfKin (@Valid @RequestBody NextOfKinRequest kinRequest,
                                            @CurrentUser UserPrincipal principal,
                                            HttpServletRequest request){
        try {
            NextOfKin createdNextOfKin = nextOfKinService.addNextOfKin(principal, kinRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User next of kin added to user profile",
                    request.getRequestURL().toString(), createdNextOfKin), HttpStatus.CREATED);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateNextOfKin (@Valid @RequestBody NextOfKinRequest kinRequest,
                                              @CurrentUser UserPrincipal principal,
                                              HttpServletRequest request){
        try {
            NextOfKin createdNextOfKin = nextOfKinService.updateNextOfKin(principal, kinRequest);
            return new ResponseEntity<>(new ApiResponse(true, "Next of Kin updated Successful",
                    request.getRequestURL().toString(), createdNextOfKin), HttpStatus.OK);
        } catch (UserException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getNextOfKin (@CurrentUser UserPrincipal principal,
                                           HttpServletRequest request){
        try {
            NextOfKin createdNextOfKin = nextOfKinService.viewNextOfKin(principal);
            return new ResponseEntity<>(new ApiResponse(true, "Successful",
                    request.getRequestURL().toString(), createdNextOfKin), HttpStatus.OK);
        } catch (UserException | GenericException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
}