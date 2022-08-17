package com.example.cashcollectorbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix="bot")
@Getter
@Setter
public class BotConfig {

    private String name;

    private String apiUrl;

    private String webhookPath;

    private String token = System.getenv("BOT_TOKEN");

}
