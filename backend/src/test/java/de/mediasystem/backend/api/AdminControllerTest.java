package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.UpdateCodewordRequest;
import de.mediasystem.backend.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void updateCodewordValidRequestReturnsNoContentAndCallsService() {
        UpdateCodewordRequest request = new UpdateCodewordRequest("neuesGeheimnis");

        ResponseEntity<Void> response = adminController.updateCodeword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(adminService).updateCodeword("neuesGeheimnis");
    }
}
