package dev.adlin.mind.controller;

import dev.adlin.mind.ChatMessage;
import dev.adlin.mind.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/message/get")
    public ResponseEntity<List<ChatMessage>> getMessages(@RequestParam Integer limit) {
        try {
            return ResponseEntity.ok(this.chatService.getMessages(limit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/message/get/prev")
    public ResponseEntity<List<ChatMessage>> getPrevMessages(@RequestParam Long beforeId, @RequestParam Integer limit) {
        try {
            return ResponseEntity.ok(this.chatService.getPrevMessages(beforeId, limit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/message/send")
    public ResponseEntity<ChatMessage> receiveMessage(@RequestBody ChatMessage message) {
        try {
            return new ResponseEntity<>(this.chatService.receiveMessage(message), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/message/subscribe")
    public SseEmitter subscribeToUpdateMessages() {
        return chatService.registerEmitter();
    }

    @GetMapping("/")
    public ResponseEntity<Integer> getOnlineCount() {
        return ResponseEntity.ok(this.chatService.getOnlineCount());
    }
}
