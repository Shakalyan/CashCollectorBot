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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MessageHandler {
    private UsersRepo usersRepo;

    private HashMap<String, Handler> handlers;

    public MessageHandler() {}

    @Autowired
    public MessageHandler(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
        handlers = new HashMap<>();
        handlers.put(BotState.START.command, this::start);
        handlers.put(BotState.AT.command, this::addTransaction);
        handlers.put(BotState.AT_NAME.command, this::getName);
        handlers.put(BotState.AT_SUM.command, this::getSum);
        handlers.put(BotState.AT_DESCRIPTION.command, this::getDescription);
    }

    public BotApiMethod<?> handle(Update update) {
        Message receivedMessage = update.getMessage();
        Optional<User> user = usersRepo.findById(receivedMessage.getFrom().getId());

        String state = user.isPresent() ? user.get().getBotState() : BotState.NEW_USER.state;
        System.out.println(state);

        SendMessage sendMessage = null;

        if(handlers.containsKey(state))
            sendMessage = handlers.get(state).handle(update, state);
        else if(handlers.containsKey(receivedMessage.getText()))
            sendMessage = handlers.get(receivedMessage.getText()).handle(update, state);

        if(sendMessage == null)
            sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.BAD_COMMAND.description);

        return sendMessage;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private User buildUser(Update update, BotState botState) {
        User user = new User();
        user.setId(update.getMessage().getFrom().getId());
        user.setUsername(update.getMessage().getFrom().getUserName());
        user.setBotState(botState.state);
        return user;
    }

    private SendMessage start(Update update, String state) {
        usersRepo.save(buildUser(update, BotState.START));

        KeyboardRow addTransactionRow = new KeyboardRow();
        addTransactionRow.add(BotState.AT.command);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(addTransactionRow);

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.START.description);
        sendMessage.setReplyMarkup(buildReplyKeyboardMarkup(keyboard));

        return sendMessage;
    }

    private SendMessage addTransaction(Update update, String state) {
        usersRepo.save(buildUser(update, BotState.AT_NAME));
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_NAME.description);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    private SendMessage getName(Update update, String state) {
        SendMessage sendMessage = null;
        if(state.equals(BotState.AT_NAME.state)) {
            usersRepo.save(buildUser(update, BotState.AT_SUM));
            sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_SUM.description);
        }
        return sendMessage;
    }

    private SendMessage getSum(Update update, String state) {
        usersRepo.save(buildUser(update, BotState.AT_DESCRIPTION));
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_DESCRIPTION.description);
        return sendMessage;
    }

    private SendMessage getDescription(Update update, String state) {
        usersRepo.save(buildUser(update, BotState.START));
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.START.description);
        return sendMessage;
    }

}
