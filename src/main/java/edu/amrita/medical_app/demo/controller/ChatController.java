package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.dto.ChatResponse;
import edu.amrita.medical_app.demo.dto.MessageResponse;
import edu.amrita.medical_app.demo.dto.SendMessageRequest;
import edu.amrita.medical_app.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public ResponseEntity<?> getChatList() {
        try {
            List<ChatResponse> chats = chatService.getChatList();
            return ResponseEntity.ok(chats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching chats");
        }
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<MessageResponse> messages = chatService.getChatMessages(chatId, page, size);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching messages");
        }
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long chatId, @RequestBody SendMessageRequest request) {
        try {
            MessageResponse message = chatService.sendMessage(chatId, request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while sending message");
        }
    }

    @PutMapping("/{chatId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long chatId) {
        try {
            chatService.markMessagesAsRead(chatId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while marking messages as read");
        }
    }

    @PostMapping("/with-user/{userId}")
    public ResponseEntity<?> getOrCreateChatWithUser(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        try {
            ChatResponse chat = chatService.getOrCreateChatWithUser(userId);
            return ResponseEntity.ok(chat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating/getting chat");
        }
    }
}
