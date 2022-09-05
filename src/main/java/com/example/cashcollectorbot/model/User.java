package com.example.cashcollectorbot.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="users")
@Data
public class User {

    @Id
    @Column(name="id")
    @NotNull
    private Long id;

    @Column(name="username")
    @NotNull
    private String username;

    @Column(name="bot_state")
    @NotNull
    private String botState;

}
