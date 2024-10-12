package com.minh.fastmail.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.minh.fastmail.model.ChatMessage;
import com.minh.fastmail.utils.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    
    @SuppressWarnings("null")
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor == null || headerAccessor.getSessionAttributes() == null) {
            log.error("Invalid STOMP message header");
            return;
        }

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (!ObjectUtils.isEmpty(username)) {
            var chatMessage = ChatMessage.builder()
                .type(MessageType.LEAVE)
                .sender(username)
                .build();
            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}