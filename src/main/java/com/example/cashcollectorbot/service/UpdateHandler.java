package com.example.cashcollectorbot.service;

import com.example.cashcollectorbot.service.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
@Setter
public class UpdateHandler extends SpringWebhookBot {

    private String botUsername;
    private String botToken;
    private String botPath;
    private MessageHandler messageHandler;
    public UpdateHandler(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return messageHandler.handle(update);
    }
    

}
