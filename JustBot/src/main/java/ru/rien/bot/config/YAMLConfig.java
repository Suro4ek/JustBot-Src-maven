package ru.rien.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
public class YAMLConfig {

    @Getter
    @Setter
    private String token;


    // standard getters and setters

}