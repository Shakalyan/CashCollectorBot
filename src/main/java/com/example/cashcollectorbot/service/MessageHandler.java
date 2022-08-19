package com.example.cashcollectorbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageHandler {

    public BotApiMethod<?> handle(Update update) {
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), update.getMessage().getText());
    }

}
