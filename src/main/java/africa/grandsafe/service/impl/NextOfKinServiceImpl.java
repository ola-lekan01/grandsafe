package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.NextOfKinRequest;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.NextOfKin;
import africa.grandsafe.data.repositories.NextOfKinRepository;
import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.AuthenticationService;
import africa.grandsafe.service.NextOfKinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class NextOfKinServiceImpl implements NextOfKinService {
    private final NextOfKinRepository nextOfKinRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    @Override
    public NextOfKin addNextOfKin(UserPrincipal principal, NextOfKinRequest nextOfKinRequest) throws UserException {
        AppUser appUser = authenticationService.internalFindUserByEmail(principal.getEmail());
        if (Objects.isNull(appUser)) throw new UserException(format("user not found with email %s", principal.getEmail()));
        return nextOfKinRepository.save(
                NextOfKin.builder()
                        .fullName(nextOfKinRequest.getFullName())
                        .email(nextOfKinRequest.getEmail())
                        .relationship(nextOfKinRequest.getRelationship())
                        .phoneNumber(nextOfKinRequest.getPhoneNumber())
                        .appUser(appUser)
                        .build()
        );
    }

    @Override
    public NextOfKin updateNextOfKin(UserPrincipal principal, NextOfKinRequest nextOfKinRequest) throws UserException {
        NextOfKin nextOfKin = findByAppUser(principal.getEmail());
        modelMapper.map(nextOfKinRequest, nextOfKin);
        return nextOfKinRepository.save(nextOfKin);
    }

    @Override
    public NextOfKin viewNextOfKin(UserPrincipal principal) throws GenericException, UserException {
        return findByAppUser(principal.getEmail());
    }

    private NextOfKin findByAppUser(String email) throws UserException {
        AppUser user = authenticationService.internalFindUserByEmail(email);
        if (Objects.isNull(user)) throw new UserException(format("user not found with email %s", email));
        return nextOfKinRepository.findByAppUser(user);
    }
}