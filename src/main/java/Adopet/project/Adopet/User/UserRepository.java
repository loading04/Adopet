package Adopet.project.Adopet.User;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Find a user by their email (useful for authentication)
    Optional<User> findByEmail(String email);
    void deleteById(UUID id);
    List<User> findAllByDeletedAtIsNull();

    
    
}
