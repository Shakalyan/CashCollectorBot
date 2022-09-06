package com.example.cashcollectorbot.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="transactions")
@Data
public class Transaction {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    @NotNull
    private Long userId;

    @Column(name="borrower_name")
    private String borrowerName;

    @Column(name="sum")
    private Integer sum;

    @Column(name="description")
    private String description;

    @Override
    public String toString() {
        return String.format("#%d %s %d₽\n%s", id, borrowerName, sum, description != null ? description : "");
    }

}
