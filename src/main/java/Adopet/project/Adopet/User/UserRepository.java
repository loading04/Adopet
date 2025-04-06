package Adopet.project.Adopet.User;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.lang.Long;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find a user by their email (useful for authentication)
    Optional<User> findByEmail(String email);
    void deleteById(Long id);
    List<User> findAllByDeletedAtIsNull();

    
    
}
