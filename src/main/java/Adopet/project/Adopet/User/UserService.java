package Adopet.project.Adopet.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Adopet.project.Adopet.User.Exceptions.UserNotFoundException;
import Adopet.project.Adopet.User.Exceptions.DuplicateEmailException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;


@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all active (non-deleted) users
    public List<User> getAllUsers() {
        log.info("Fetching all active users");
        return userRepository.findAllByDeletedAtIsNull();
    }

    // Get a single user by ID
    public User getUserById(Long id) {
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

    private boolean isPasswordPlain(String password) {
        // BCrypt hashes always start with $2a or $2b and are 60 chars long
        return password == null || !password.startsWith("$2");
    }
    // Create or update a user
    public User saveUser(User user) {
        log.info("Saving user with email: {}", user.getEmail());

        // Check for duplicate email before saving
        if (user.getId() == null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + user.getEmail());
        }
        // Hash password if it's new or changed
        if (user.getId() == null || isPasswordPlain(user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    // Soft delete a user
    public void deleteUser(Long id, Long deletedBy) {
        log.info("Attempting to delete user with ID: {} by {}", id, deletedBy);

        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setDeletedBy(deletedBy);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);

        log.info("User with ID: {} has been soft deleted", id);
    }
    
    //update user
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setPhone(updatedUser.getPhone());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
