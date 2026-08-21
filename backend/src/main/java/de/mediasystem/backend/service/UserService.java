package de.mediasystem.backend.service;


import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * gets userid  by usename.
     * @param username the username
     * @return the id
     */
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return user.getId();
    }
}
