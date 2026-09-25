package com.agriconnect.controller;

import com.agriconnect.entity.Message;
import com.agriconnect.entity.User;
import com.agriconnect.repository.MessageRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    public MessageController(MessageRepository messageRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long u1, @RequestParam Long u2) {
        return messageRepo.findConversation(u1, u2);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> req) {
        try {
            Long senderId = Long.valueOf(req.get("senderId").toString());
            Long receiverId = Long.valueOf(req.get("receiverId").toString());
            String content = (String) req.get("content");

            User sender = userRepo.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
            User receiver = userRepo.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

            Message msg = new Message();
            msg.setSender(sender);
            msg.setReceiver(receiver);
            msg.setContent(content);

            return ResponseEntity.ok(messageRepo.save(msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
