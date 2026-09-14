package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.UpdateCodewordRequest;
import de.mediasystem.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Setzt/ändert das Signup-Codewort. Nur für ROLE_ADMIN erreichbar (SecurityConfig + zusätzlich
     * @PreAuthorize direkt auf AdminService.updateCodeword).
     * @param request enthält das neue Codewort
     * @return 204, kein Body nötig
     */
    @PutMapping("/admin/codeword")
    public ResponseEntity<Void> updateCodeword(@RequestBody @Valid UpdateCodewordRequest request) {
        adminService.updateCodeword(request.codeword());
        return ResponseEntity.noContent().build();
    }
}
