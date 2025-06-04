package edu.amrita.medical_app.demo.repository;

import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
}