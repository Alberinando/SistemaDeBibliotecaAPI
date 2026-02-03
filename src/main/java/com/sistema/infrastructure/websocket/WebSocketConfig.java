package com.sistema.infrastructure.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração do WebSocket com STOMP e SockJS para notificações em tempo real.
 * 
 * Esta configuração permite:
 * - Conexões via SockJS (fallback para browsers sem suporte nativo a WebSocket)
 * - CORS configurado para aceitar conexões do frontend
 * - Message broker para envio de mensagens para tópicos específicos
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.allowed-origins:https://sistemabiblioteca.alberinando.com}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita um simple broker para destinos que começam com /user e /topic
        config.enableSimpleBroker("/user", "/topic");
        // Define o prefixo para mensagens enviadas pelo cliente para o servidor
        config.setApplicationDestinationPrefixes("/app");
        // Define o prefixo para mensagens destinadas a usuários específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Divide as origens permitidas
        String[] origins = allowedOrigins.split(",");

        // Endpoint para conexão via SockJS
        // Usar setAllowedOriginPatterns em vez de setAllowedOrigins para
        // compatibilidade
        // com allowCredentials=true (necessário para cookies/autenticação)
        registry.addEndpoint("/ws-notificacoes")
                .setAllowedOriginPatterns(origins)
                .withSockJS();

        // Endpoint para WebSocket nativo (opcional, para clientes que suportam)
        registry.addEndpoint("/ws-notificacoes")
                .setAllowedOriginPatterns(origins);
    }
}
