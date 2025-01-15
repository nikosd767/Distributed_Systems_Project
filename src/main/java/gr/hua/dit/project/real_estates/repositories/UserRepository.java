package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.User;
import gr.hua.dit.project.real_estates.entities.OtherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find users by their status (PENDING, APPROVED, etc)
    List<User> findByStatus(OtherStatus status);

    // Delete user by email address
    Optional<User> deleteUserByEmail(String email);

    // Find user by username
    Optional<User> findByUsername(String username);

    // Find user by email address
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    Boolean existsByUsername(String username);

    // Check if email exists
    Boolean existsByEmail(String email);
}