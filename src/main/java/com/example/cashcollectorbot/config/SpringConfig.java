package com.example.cashcollectorbot.config;

import com.example.cashcollectorbot.service.MessageHandler;
import com.example.cashcollectorbot.service.UpdateHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {

    private BotConfig botConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebhookPath()).build();
    }

    @Bean
    public UpdateHandler botUpdateHandler(SetWebhook setWebhook, MessageHandler messageHandler) {
        UpdateHandler updateHandler = new UpdateHandler(setWebhook, messageHandler);
        updateHandler.setBotUsername(botConfig.getName());
        updateHandler.setBotToken(botConfig.getToken());
        updateHandler.setBotPath(botConfig.getWebhookPath());
        return updateHandler;
    }

}
