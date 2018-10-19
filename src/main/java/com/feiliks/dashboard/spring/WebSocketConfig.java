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

    private WebSocketHandler _webSocketHandler = null;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/websocket");
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/sockjs").withSockJS();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        if (_webSocketHandler == null)
            _webSocketHandler = new WebSocketHandler();
        return _webSocketHandler;
    }

}
