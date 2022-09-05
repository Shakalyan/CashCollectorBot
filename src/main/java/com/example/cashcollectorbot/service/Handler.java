package com.example.cashcollectorbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface Handler {

    public SendMessage handle(Update update, String state);

}
