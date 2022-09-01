package com.example.cashcollectorbot.service;

public enum BotState {
    START(0), ADD_TRANSACTION(1);

    int value;

    private BotState(int value) {
        this.value = value;
    }



}
