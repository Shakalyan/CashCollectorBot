package com.example.cashcollectorbot.service;

import lombok.Getter;
import lombok.Setter;


public enum BotState {

    NEW_USER("NU", "", ""),
    START("ST", "/start", "Выберите действие"),
    BAD_COMMAND("", "", "Я не знаю такой команды =("),

    AT("AT", "Добавить транзакцию", ""),
    AT_NAME("AT_N", "AT_N", "Введите имя заёмщика:"),
    AT_SUM("AT_S", "AT_S","Введите сумму:"),
    AT_DESCRIPTION("AT_D", "AT_D","Напишите описание к транзакции:");

    String state;
    String command;
    String description;

    BotState(String state, String command, String description) {
        this.state = state;
        this.command = command;
        this.description = description;
    }

}