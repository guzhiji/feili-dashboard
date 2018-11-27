package com.feiliks.dashboard.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
// @EnableWebMvc
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private WebSocketHandler _wsConsolidationHandler = null;
    private WebSocketHandler _wsShipmentHandler = null;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(wsConsolidationHandler(), "/sockjs/consolidation").withSockJS();
        webSocketHandlerRegistry.addHandler(wsShipmentHandler(), "/sockjs/shipment").withSockJS();
    }

    @Bean
    public WebSocketHandler wsConsolidationHandler() {
        if (_wsConsolidationHandler == null)
            _wsConsolidationHandler = new WebSocketHandler();
        return _wsConsolidationHandler;
    }

    @Bean
    public WebSocketHandler wsShipmentHandler() {
        if (_wsShipmentHandler == null)
            _wsShipmentHandler = new WebSocketHandler();
        return _wsShipmentHandler;
    }

}
