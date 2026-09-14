package de.mediasystem.backend.service;

import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.model.AppSetting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AppSettingRepository appSettingRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void updateCodewordCreatesNewSettingWhenNoneExists() {
        when(appSettingRepository.findById("signup_codeword")).thenReturn(Optional.empty());

        adminService.updateCodeword("neuesGeheimnis");

        ArgumentCaptor<AppSetting> captor = ArgumentCaptor.forClass(AppSetting.class);
        verify(appSettingRepository).save(captor.capture());
        assertThat(captor.getValue().getKey()).isEqualTo("signup_codeword");
        assertThat(captor.getValue().getValue()).isEqualTo("neuesGeheimnis");
    }

    @Test
    void updateCodewordOverwritesExistingSetting() {
        AppSetting existing = new AppSetting();
        existing.setKey("signup_codeword");
        existing.setValue("altesGeheimnis");
        when(appSettingRepository.findById("signup_codeword")).thenReturn(Optional.of(existing));

        adminService.updateCodeword("neuesGeheimnis");

        ArgumentCaptor<AppSetting> captor = ArgumentCaptor.forClass(AppSetting.class);
        verify(appSettingRepository).save(captor.capture());
        assertThat(captor.getValue().getValue()).isEqualTo("neuesGeheimnis");
    }
}
