package edu.amrita.medical_app.demo.repository;

import edu.amrita.medical_app.demo.entity.Chat;
import edu.amrita.medical_app.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE (c.participant1 = :user OR c.participant2 = :user) ORDER BY c.updatedAt DESC")
    List<Chat> findByParticipant(@Param("user") User user);
    
    @Query("SELECT c FROM Chat c WHERE (c.participant1 = :user1 AND c.participant2 = :user2) OR (c.participant1 = :user2 AND c.participant2 = :user1)")
    Optional<Chat> findByParticipants(@Param("user1") User user1, @Param("user2") User user2);
}
