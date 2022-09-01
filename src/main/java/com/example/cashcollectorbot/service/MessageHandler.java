package com.example.cashcollectorbot.service;

import com.example.cashcollectorbot.model.User;
import com.example.cashcollectorbot.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageHandler {
    private UsersRepo usersRepo;

    public MessageHandler() {}

    @Autowired
    public MessageHandler(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    public BotApiMethod<?> handle(Update update) {
        Message receivedMessage = update.getMessage();

        if (receivedMessage.getText().equals("/start"))
            return start(update);

        SendMessage sendMessage = new SendMessage(String.valueOf(receivedMessage.getChatId()), receivedMessage.getText());
        return sendMessage;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private SendMessage start(Update update) {

        User user = new User();
        user.setId(update.getMessage().getFrom().getId());
        user.setUsername(update.getMessage().getFrom().getUserName());
        user.setBotState(BotState.ADD_TRANSACTION.value);

        if(!usersRepo.existsById(user.getId()))
            usersRepo.save(user);

        KeyboardRow addTransactionRow = new KeyboardRow();
        addTransactionRow.add("Add transaction");
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(addTransactionRow);

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), "Choose one:");
        sendMessage.setReplyMarkup(buildReplyKeyboardMarkup(keyboard));

        return sendMessage;
    }

}
