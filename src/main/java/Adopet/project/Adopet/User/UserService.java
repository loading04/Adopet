package Adopet.project.Adopet.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Adopet.project.Adopet.User.Exceptions.UserNotFoundException;
import Adopet.project.Adopet.User.Exceptions.DuplicateEmailException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all active (non-deleted) users
    public List<User> getAllUsers() {
        log.info("Fetching all active users");
        return userRepository.findAllByDeletedAtIsNull();
    }

    // Get a single user by ID
    public User getUserById(UUID id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

    }

    // Get a user by email
    public User getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    // Create or update a user
    public User saveUser(User user) {
        log.info("Saving user with email: {}", user.getEmail());

        // Check for duplicate email before saving
        if (user.getId() == null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    // Soft delete a user
    public void deleteUser(UUID id, UUID deletedBy) {
        log.info("Attempting to delete user with ID: {} by {}", id, deletedBy);

        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setDeletedBy(deletedBy);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);

        log.info("User with ID: {} has been soft deleted", id);
    }
}
