package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.*;
import edu.amrita.medical_app.demo.entity.Chat;
import edu.amrita.medical_app.demo.entity.Message;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.repository.ChatRepository;
import edu.amrita.medical_app.demo.repository.MessageRepository;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ChatResponse> getChatList() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Chat> chats = chatRepository.findByParticipant(currentUser);
        
        return chats.stream().map(chat -> {
            ChatResponse response = new ChatResponse();
            response.setId(chat.getId());
            
            // Set participants
            List<ParticipantResponse> participants = new ArrayList<>();
            User otherParticipant = chat.getOtherParticipant(currentUser);
            
            ParticipantResponse participantResponse = new ParticipantResponse();
            participantResponse.setId(otherParticipant.getId());
            participantResponse.setFullName(otherParticipant.getFullName());
            participantResponse.setAvatar(otherParticipant.getAvatar());
            participantResponse.setRole(otherParticipant.getRole().toString());
            participantResponse.setOnline(false); // TODO: Implement online status
            participantResponse.setLastSeen(null); // TODO: Implement last seen
            
            participants.add(participantResponse);
            response.setParticipants(participants);
            
            // Set last message
            Optional<Message> lastMessage = messageRepository.findTopByChatOrderByCreatedAtDesc(chat);
            if (lastMessage.isPresent()) {
                Message msg = lastMessage.get();
                MessageResponse msgResponse = new MessageResponse();
                msgResponse.setId(msg.getId());
                msgResponse.setChatId(msg.getChat().getId());
                msgResponse.setSenderId(msg.getSender().getId());
                msgResponse.setText(msg.getText());
                msgResponse.setRead(msg.isRead());
                msgResponse.setCreatedAt(msg.getCreatedAt());
                response.setLastMessage(msgResponse);
            }
            
            // Set unread count
            int unreadCount = messageRepository.countUnreadMessages(chat, currentUser);
            response.setUnreadCount(unreadCount);
            
            return response;
        }).collect(Collectors.toList());
    }

    public List<MessageResponse> getChatMessages(Long chatId, int page, int size) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Check if user is participant
        if (!chat.isParticipant(currentUser)) {
            throw new IllegalArgumentException("Access denied to this chat");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByChatOrderByCreatedAtDesc(chat, pageable);

        return messages.getContent().stream().map(message -> {
            MessageResponse response = new MessageResponse();
            response.setId(message.getId());
            response.setChatId(message.getChat().getId());
            response.setSenderId(message.getSender().getId());
            response.setText(message.getText());
            response.setRead(message.isRead());
            response.setCreatedAt(message.getCreatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse sendMessage(Long chatId, SendMessageRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Check if user is participant
        if (!chat.isParticipant(currentUser)) {
            throw new IllegalArgumentException("Access denied to this chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(currentUser);
        message.setText(request.getText());
        message.setRead(false);

        message = messageRepository.save(message);

        // Update chat's updatedAt timestamp
        chat.setUpdatedAt(message.getCreatedAt());
        chatRepository.save(chat);

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setChatId(message.getChat().getId());
        response.setSenderId(message.getSender().getId());
        response.setText(message.getText());
        response.setRead(message.isRead());
        response.setCreatedAt(message.getCreatedAt());

        return response;
    }

    @Transactional
    public void markMessagesAsRead(Long chatId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Check if user is participant
        if (!chat.isParticipant(currentUser)) {
            throw new IllegalArgumentException("Access denied to this chat");
        }

        messageRepository.markMessagesAsRead(chat, currentUser);
    }

    @Transactional
    public ChatResponse getOrCreateChatWithUser(Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User otherUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Don't allow chat with self
        if (currentUser.getId().equals(otherUser.getId())) {
            throw new IllegalArgumentException("Cannot create chat with yourself");
        }

        // Check if chat already exists
        Optional<Chat> existingChat = chatRepository.findByParticipants(currentUser, otherUser);
        
        Chat chat;
        if (existingChat.isPresent()) {
            chat = existingChat.get();
        } else {
            // Create new chat
            chat = new Chat();
            chat.setParticipant1(currentUser);
            chat.setParticipant2(otherUser);
            chat = chatRepository.save(chat);
        }

        // Convert to response
        ChatResponse response = new ChatResponse();
        response.setId(chat.getId());
        
        // Set participants
        List<ParticipantResponse> participants = new ArrayList<>();
        
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(otherUser.getId());
        participantResponse.setFullName(otherUser.getFullName());
        participantResponse.setAvatar(otherUser.getAvatar());
        participantResponse.setRole(otherUser.getRole().toString());
        participantResponse.setOnline(false); // TODO: Implement online status
        participantResponse.setLastSeen(null); // TODO: Implement last seen
        
        participants.add(participantResponse);
        response.setParticipants(participants);
        
        // Set last message
        Optional<Message> lastMessage = messageRepository.findTopByChatOrderByCreatedAtDesc(chat);
        if (lastMessage.isPresent()) {
            Message msg = lastMessage.get();
            MessageResponse msgResponse = new MessageResponse();
            msgResponse.setId(msg.getId());
            msgResponse.setChatId(msg.getChat().getId());
            msgResponse.setSenderId(msg.getSender().getId());
            msgResponse.setText(msg.getText());
            msgResponse.setRead(msg.isRead());
            msgResponse.setCreatedAt(msg.getCreatedAt());
            response.setLastMessage(msgResponse);
        }
        
        // Set unread count
        int unreadCount = messageRepository.countUnreadMessages(chat, currentUser);
        response.setUnreadCount(unreadCount);
        
        return response;
    }
}
