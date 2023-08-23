package africa.grandsafe.service.impl;

import africa.grandsafe.data.dtos.request.NextOfKinRequest;
import africa.grandsafe.data.models.AppUser;
import africa.grandsafe.data.models.NextOfKin;
import africa.grandsafe.data.repositories.NextOfKinRepository;
import africa.grandsafe.exceptions.UserException;
import africa.grandsafe.security.UserPrincipal;
import africa.grandsafe.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NextOfKinServiceImplTest {
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private NextOfKinRepository nextOfKinRepository;
    @Mock
    private ModelMapper modelMapperMock;
    @InjectMocks
    private NextOfKinServiceImpl nextOfKinService;
    private UserPrincipal userPrincipalMock;
    private AppUser mockedUser;
    private NextOfKinRequest mockedRequest;
    private NextOfKin mockedNextOfKin;

    @BeforeEach
    void setUp() {
        List<GrantedAuthority> authorities = Collections.singletonList(mock(GrantedAuthority.class));
        userPrincipalMock = new UserPrincipal(UUID.randomUUID().toString(),
                "Lekan",
                "Sofuyi",
                "test@gmail.com",
                "pass1234",
                true,
                authorities);


        mockedUser = new AppUser();
        mockedUser.setId(UUID.randomUUID().toString());
        mockedUser.setFirstName("Lekan");
        mockedUser.setLastName("Sofuyi");
        mockedUser.setEmail("test@gmail.com");
        mockedUser.setPhoneNumber("08069580949");
        mockedUser.setPassword("pass1234");

        mockedRequest = new NextOfKinRequest();
        mockedRequest.setPhoneNumber("08131598002");
        mockedRequest.setFullName("Sofuyi Oyinlola");
        mockedRequest.setEmail("test@gmail.com");
        mockedRequest.setRelationship("Wife");

        mockedNextOfKin = new NextOfKin();
        mockedNextOfKin.setRelationship("Wife");
        mockedNextOfKin.setFullName("Sofuyi Oyinlola");
        mockedNextOfKin.setPhoneNumber("08131598002");
        mockedNextOfKin.setEmail("test@gmail.com");
        mockedNextOfKin.setAppUser(mockedUser);
    }

    @Test
    void addNextOfKin_validInputs_shouldReturnNextOfKin() throws UserException {
        // Arrange
        when(authenticationService.internalFindUserByEmail(userPrincipalMock.getEmail())).thenReturn(mockedUser);
        when(nextOfKinRepository.save(any(NextOfKin.class))).thenReturn(mockedNextOfKin);

        // Act
        NextOfKin result = nextOfKinService.addNextOfKin(userPrincipalMock, mockedRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockedRequest.getFullName(), result.getFullName());

        // Verifying method calls
        verify(authenticationService, times(1)).internalFindUserByEmail(userPrincipalMock.getEmail());
        verify(nextOfKinRepository, times(1)).save(any(NextOfKin.class));

    }

    @Test
    void addNextOfKin_userNotFound_shouldThrowUserException() throws UserException {
        // Arrange
        when(authenticationService.internalFindUserByEmail(userPrincipalMock.getEmail())).thenReturn(null);
        // Act + Assert
        assertThrows(UserException.class, () -> nextOfKinService.addNextOfKin(userPrincipalMock, mockedRequest));
        // Verifying method calls
        verify(authenticationService, times(1)).internalFindUserByEmail(userPrincipalMock.getEmail());
    }

    @Test
    void testUpdateNextOfKin() throws UserException {
        mockedRequest.setFullName("Arinakore Keren");
        mockedRequest.setPhoneNumber("08023325065");

        when(nextOfKinRepository.findByAppUser(mockedUser)).thenReturn(mockedNextOfKin);
        when(nextOfKinRepository.save(any(NextOfKin.class))).thenReturn(mockedNextOfKin);
        when(authenticationService.internalFindUserByEmail(userPrincipalMock.getEmail())).thenReturn(mockedUser);


        doAnswer(invocation -> {
            modelMapperMock.map(mockedRequest, mockedNextOfKin);
            mockedNextOfKin.setFullName(mockedRequest.getFullName());
            mockedNextOfKin.setPhoneNumber(mockedRequest.getPhoneNumber());
            return mockedNextOfKin;
        }).when(nextOfKinRepository).save(mockedNextOfKin);

        NextOfKin result = nextOfKinService.updateNextOfKin(userPrincipalMock, mockedRequest);

        assertNotNull(result);
        assertEquals("Arinakore Keren", result.getFullName());
        assertEquals("08023325065", result.getPhoneNumber());

        verify(modelMapperMock, times(2)).map(mockedRequest, mockedNextOfKin);
        verify(nextOfKinRepository, times(1)).save(any(NextOfKin.class));
        verify(authenticationService, times(1)).internalFindUserByEmail(userPrincipalMock.getEmail());

    }
}