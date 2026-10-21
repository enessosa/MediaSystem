package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.CodewordResponse;
import de.mediasystem.backend.api.dto.UpdateCodewordRequest;
import de.mediasystem.backend.service.AdminService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void updateCodewordValidRequestReturnsNoContentAndCallsService() {
        UpdateCodewordRequest request = new UpdateCodewordRequest("neuesGeheimnis");

        ResponseEntity<@NonNull Void> response = adminController.updateCodeword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(adminService).updateCodeword("neuesGeheimnis");
    }

    @Test
    void getCodewordReturnsOkWithCurrentCodeword() {
        when(adminService.getCodeword()).thenReturn("geheim");

        ResponseEntity<@NonNull CodewordResponse> response = adminController.getCodeword();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new CodewordResponse("geheim"));
    }

    @Test
    void deleteCodewordReturnsNoContentAndCallsService() {
        ResponseEntity<@NonNull Void> response = adminController.deleteCodeword();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(adminService).deleteCodeword();
    }
}
