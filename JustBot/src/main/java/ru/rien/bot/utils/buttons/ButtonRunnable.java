package ru.rien.bot.utils.buttons;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonRunnable {

    void run(ButtonClickEvent event);
}