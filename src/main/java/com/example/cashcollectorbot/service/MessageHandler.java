package com.example.cashcollectorbot.service;

import com.example.cashcollectorbot.model.Transaction;
import com.example.cashcollectorbot.model.User;
import com.example.cashcollectorbot.repo.TransactionsRepo;
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
    private TransactionsRepo transactionsRepo;

    private HashMap<String, Handler> handlers;

    public MessageHandler() {}

    @Autowired
    public MessageHandler(UsersRepo usersRepo, TransactionsRepo transactionsRepo) {
        this.usersRepo = usersRepo;
        this.transactionsRepo = transactionsRepo;

        handlers = new HashMap<>();
        handlers.put(BotState.START.command, this::start);
        handlers.put(BotState.AT.command, this::addTransaction);
        handlers.put(BotState.AT_NAME.command, this::getName);
        handlers.put(BotState.AT_SUM.command, this::getSum);
        handlers.put(BotState.AT_DESCRIPTION.command, this::getDescription);
        handlers.put(BotState.ST_ALL.command, this::showAllTransactions);
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

    private List<KeyboardRow> buildStartKeyboard() {
        KeyboardRow addTransactionRow = new KeyboardRow();
        addTransactionRow.add(BotState.AT.command);
        addTransactionRow.add(BotState.ST_ALL.command);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(addTransactionRow);
        return keyboard;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private User initializeUser(Update update, BotState botState) {
        User user = new User();
        user.setId(update.getMessage().getFrom().getId());
        user.setUsername(update.getMessage().getFrom().getUserName());
        user.setBotState(botState.state);
        return user;
    }

    private User findUser(Update update) {
        Long id = update.getMessage().getFrom().getId();
        Optional<User> user = usersRepo.findById(id);
        if(!user.isPresent())
            System.out.println(String.format("CANNOT FIND USER WITH ID = %d", id));
        return user.get();
    }

    private Transaction findTransaction(Long id) {
        Optional<Transaction> transaction = transactionsRepo.findById(id);
        if(!transaction.isPresent())
            System.out.println(String.format("CANNOT FIND TRANSACTION WITH ID = %d", id));
        return transaction.get();
    }

    private SendMessage start(Update update, String state) {
        usersRepo.save(initializeUser(update, BotState.START));

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.START.description);
        sendMessage.setReplyMarkup(buildReplyKeyboardMarkup(buildStartKeyboard()));

        return sendMessage;
    }

    private SendMessage addTransaction(Update update, String state) {
        User user = findUser(update);
        user.setBotState(BotState.AT_NAME.state);

        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transactionsRepo.save(transaction);

        user.setTransactionId(transaction.getId());
        usersRepo.save(user);

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_NAME.description);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    private SendMessage getName(Update update, String state) {
        Message receivedMessage = update.getMessage();
        SendMessage sendMessage = null;
        if(state.equals(BotState.AT_NAME.state)) {
            User user = findUser(update);
            user.setBotState(BotState.AT_SUM.state);
            usersRepo.save(user);

            Transaction transaction = findTransaction(user.getTransactionId());
            transaction.setBorrowerName(receivedMessage.getText());
            transactionsRepo.save(transaction);

            sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_SUM.description);
        }
        return sendMessage;
    }

    private SendMessage getSum(Update update, String state) {

        try {
            int sum = Integer.parseInt(update.getMessage().getText());
            if(sum < 0)
                throw new NumberFormatException();

            User user = findUser(update);
            user.setBotState(BotState.AT_DESCRIPTION.state);
            usersRepo.save(user);

            Transaction transaction = findTransaction(user.getTransactionId());
            transaction.setSum(sum);
            transactionsRepo.save(transaction);

            SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.AT_DESCRIPTION.description);
            return sendMessage;

        } catch(NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.BAD_SUM.description);
            return sendMessage;
        }

    }

    private SendMessage getDescription(Update update, String state) {
        User user = findUser(update);
        user.setBotState(BotState.START.state);
        usersRepo.save(user);

        Transaction transaction = findTransaction(user.getTransactionId());
        transaction.setDescription(update.getMessage().getText());
        transactionsRepo.save(transaction);

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), BotState.START.description);
        sendMessage.setReplyMarkup(buildReplyKeyboardMarkup(buildStartKeyboard()));
        return sendMessage;
    }

    private SendMessage showAllTransactions(Update update, String state) {
        ArrayList<Transaction> transactions = transactionsRepo.findAllByUserId(update.getMessage().getFrom().getId());
        StringBuilder text = new StringBuilder();

        for(int i = 0; i < transactions.size(); ++i) {
            Transaction t = transactions.get(i);
            text.append(String.format("%d. Сумма: %d₽\nДолжники: %s\nОписание: \"%s\"", i+1,
                                                                                        t.getSum(),
                                                                                        t.getBorrowerName(),
                                                                                        t.getDescription() != null ? t.getDescription() : ""));
            text.append("\n\n");
        }

        return new SendMessage(String.valueOf(update.getMessage().getChatId()), text.toString());
    }

}
