package africa.grandsafe.service;

import africa.grandsafe.data.dtos.request.AddCardRequest;
import africa.grandsafe.utils.cardentity.Data;

public interface PayStackService {

    Data validateCardDetails(AddCardRequest cardDetailsRequest);
}
