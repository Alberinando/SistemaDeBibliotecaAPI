package com.sistema.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.domain.entities.Notificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Serviço para enviar notificações via STOMP/WebSocket.
 * 
 * Usa SimpMessagingTemplate para enviar mensagens para:
 * - /user/{userId}/queue/notificacoes - para um usuário específico
 * - /topic/notificacoes - para todos os usuários conectados
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Envia uma notificação para um funcionário específico via WebSocket STOMP.
     * 
     * @param funcionarioId ID do funcionário de destino
     * @param notificacao   Objeto da notificação a ser enviada
     */
    public void enviarNotificacao(Long funcionarioId, Notificacao notificacao) {
        try {
            String destination = "/queue/notificacoes";
            String payload = objectMapper.writeValueAsString(notificacao);

            // Envia para o usuário específico usando o ID como identificador
            messagingTemplate.convertAndSendToUser(
                    funcionarioId.toString(),
                    destination,
                    payload);

            log.info("Notificação enviada via STOMP para funcionário ID: {}", funcionarioId);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação via STOMP para funcionário {}: {}",
                    funcionarioId, e.getMessage());
        }
    }

    /**
     * Envia uma notificação para um funcionário específico via WebSocket STOMP.
     * 
     * @param funcionarioId ID do funcionário de destino
     * @param mensagem      Mensagem JSON a ser enviada
     */
    public void enviarNotificacao(Long funcionarioId, String mensagem) {
        try {
            String destination = "/queue/notificacoes";

            // Envia para o usuário específico
            messagingTemplate.convertAndSendToUser(
                    funcionarioId.toString(),
                    destination,
                    mensagem);

            log.info("Notificação enviada via STOMP para funcionário ID: {}", funcionarioId);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação via STOMP para funcionário {}: {}",
                    funcionarioId, e.getMessage());
        }
    }

    /**
     * Envia uma notificação para todos os funcionários conectados.
     * 
     * @param mensagem Mensagem a ser enviada
     */
    public void enviarParaTodos(String mensagem) {
        try {
            messagingTemplate.convertAndSend("/topic/notificacoes", mensagem);
            log.info("Notificação broadcast enviada via STOMP");
        } catch (Exception e) {
            log.error("Erro ao enviar notificação broadcast via STOMP: {}", e.getMessage());
        }
    }
}
