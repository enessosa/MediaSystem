package de.mediasystem.backend.service;


import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.Admin;
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

    /**
     * this method gets the user with only the username.
     * @param username the username
     * @return user data
     */
    public MeResponse getCurrentUser(String username) {
        User user  = userRepository.findByUsername(username).orElseThrow();
        boolean isAdmin = user instanceof Admin;
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), isAdmin);
    }
}
