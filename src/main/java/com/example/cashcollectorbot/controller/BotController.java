package com.example.cashcollectorbot.controller;

import com.example.cashcollectorbot.model.User;
import com.example.cashcollectorbot.repo.UsersRepo;
import com.example.cashcollectorbot.service.UpdateHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class BotController {

    private UpdateHandler updateHandler;

    @Autowired
    private UsersRepo usersRepo;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return updateHandler.onWebhookUpdateReceived(update);
    }

}
