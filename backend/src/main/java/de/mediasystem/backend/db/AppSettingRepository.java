package de.mediasystem.backend.db;

import de.mediasystem.backend.model.AppSetting;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<@NonNull AppSetting, @NonNull String> {
}
