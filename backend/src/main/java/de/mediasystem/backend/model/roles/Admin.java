package de.mediasystem.backend.model.roles;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;


@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class Admin extends User {
}
