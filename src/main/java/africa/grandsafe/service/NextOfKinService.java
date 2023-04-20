package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.NextOfKinRequest;
import africa.grandsafe.data.models.NextOfKin;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;

public interface NextOfKinService {
    NextOfKin addNextOfKin(UserPrincipal principal, NextOfKinRequest nextOfKinRequest) throws UserException;
    NextOfKin updateNextOfKin(UserPrincipal principal, NextOfKinRequest nextOfKinRequest) throws UserException;
    NextOfKin viewNextOfKin(UserPrincipal principal) throws GenericException, UserException;
}
