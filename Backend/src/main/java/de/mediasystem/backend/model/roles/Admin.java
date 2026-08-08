package de.mediasystem.backend.model.roles;


public class Admin extends User {

    private String codeword;

    public Admin(long id, String username, String email, String passwordHash, String createdAt) {
        super(id, username, email, passwordHash, createdAt);
    }

    public void setCodeword(String codeword) {
        this.codeword = codeword;
    }
}
