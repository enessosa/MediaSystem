package de.mediasystem.backend.model.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {

    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String createdAt;

    public void register() { }

    public void login() { }
}
