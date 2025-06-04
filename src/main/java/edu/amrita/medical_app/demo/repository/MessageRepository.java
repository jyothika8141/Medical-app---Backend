package edu.amrita.medical_app.demo.repository;

import edu.amrita.medical_app.demo.entity.Chat;
import edu.amrita.medical_app.demo.entity.Message;
import edu.amrita.medical_app.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByChatOrderByCreatedAtDesc(Chat chat, Pageable pageable);
    
    Optional<Message> findTopByChatOrderByCreatedAtDesc(Chat chat);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat = :chat AND m.sender != :user AND m.isRead = false")
    int countUnreadMessages(@Param("chat") Chat chat, @Param("user") User user);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat = :chat AND m.sender != :user AND m.isRead = false")
    void markMessagesAsRead(@Param("chat") Chat chat, @Param("user") User user);
}
