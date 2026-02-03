package com.sistema.infrastructure.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NotificacaoWebSocketHandler extends TextWebSocketHandler {

    // Mapa de sessões por ID do funcionário
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extrai o ID do funcionário dos query params
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.contains("funcionarioId=")) {
            String[] parts = query.split("funcionarioId=");
            if (parts.length > 1) {
                String idStr = parts[1].split("&")[0];
                try {
                    Long funcionarioId = Long.parseLong(idStr);
                    sessions.put(funcionarioId, session);
                    log.info("WebSocket conectado para funcionário ID: {}", funcionarioId);
                } catch (NumberFormatException e) {
                    log.warn("ID de funcionário inválido na conexão WebSocket");
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove a sessão do mapa
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket desconectado");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Pode ser usado para receber mensagens do cliente
        log.debug("Mensagem recebida: {}", message.getPayload());
    }

    /**
     * Envia uma notificação para um funcionário específico via WebSocket
     */
    public void enviarNotificacao(Long funcionarioId, String mensagem) {
        WebSocketSession session = sessions.get(funcionarioId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(mensagem));
                log.info("Notificação enviada via WebSocket para funcionário ID: {}", funcionarioId);
            } catch (IOException e) {
                log.error("Erro ao enviar notificação via WebSocket", e);
            }
        }
    }

    /**
     * Envia uma notificação para todos os funcionários conectados
     */
    public void enviarParaTodos(String mensagem) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mensagem));
                } catch (IOException e) {
                    log.error("Erro ao enviar notificação broadcast via WebSocket", e);
                }
            }
        });
    }
}
