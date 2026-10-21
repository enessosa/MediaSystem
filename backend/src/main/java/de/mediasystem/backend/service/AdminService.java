package de.mediasystem.backend.service;

import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.model.AppSetting;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private static final String CODEWORD_KEY = "signup_codeword";

    private final AppSettingRepository appSettingRepository;

    public AdminService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    /**
     * Setzt/ändert das Signup-Codewort (ab dem 11. Nutzer Pflicht, siehe AuthService.register).
     * Zusätzlich zur URL-Regel in SecurityConfig ("/admin/**" -> ROLE_ADMIN) hier nochmal per
     * {@code @PreAuthorize} geprüft - zweite, unabhängige Schutzschicht direkt an der sensiblen Methode
     * (siehe learn-progress/Defense-in-Depth-Autorisierung.md).
     * @param codeword das neue Codewort
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCodeword(String codeword) {
        AppSetting setting = appSettingRepository.findById(CODEWORD_KEY).orElseGet(AppSetting::new);
        setting.setKey(CODEWORD_KEY);
        setting.setValue(codeword);
        appSettingRepository.save(setting);
    }

    /**
     * Liefert das aktuelle Signup-Codewort, damit der Admin es einsehen und weitergeben kann. Es ist ein
     * gemeinsamer Einladungscode, kein persönliches Passwort - deshalb im Klartext gespeichert.
     * @return das Codewort oder null, wenn keins gesetzt ist (Registrierung dann offen)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public String getCodeword() {
        return appSettingRepository.findById(CODEWORD_KEY).map(AppSetting::getValue).orElse(null);
    }

    /**
     * Entfernt das Codewort - ab dann ist die Registrierung auch ab dem 11. Nutzer offen.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCodeword() {
        appSettingRepository.deleteById(CODEWORD_KEY);
    }
}
