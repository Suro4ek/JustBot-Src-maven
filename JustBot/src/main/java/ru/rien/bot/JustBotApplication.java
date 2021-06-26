package ru.rien.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JustBotApplication {


	static final Logger log =
			LoggerFactory.getLogger(JustBotApplication.class);

	public static void main(String[] args) {
		new Load().onLoad();
		SpringApplication.run(JustBotApplication.class, args);
	}

	public static Logger getLog() {
		return log;
	}
}

