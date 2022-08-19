package com.example.cashcollectorbot.controller;

import com.example.cashcollectorbot.service.UpdateHandler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@AllArgsConstructor
public class BotController {

    private UpdateHandler updateHandler;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        System.out.println(update.getMessage());
        System.out.println(update.getMessage().getText());
        return updateHandler.onWebhookUpdateReceived(update);
    }

}
