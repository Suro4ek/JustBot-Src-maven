package ru.rien.bot.utils.objects;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import ru.rien.bot.utils.buttons.ButtonRunnable;

public class MyButton {
    private net.dv8tion.jda.api.interactions.components.Button button;
    private ButtonRunnable runnable;
    private User user;

    public MyButton(net.dv8tion.jda.api.interactions.components.Button button, ButtonRunnable runnable) {
        this.button = button;
        this.runnable = runnable;
    }

    public MyButton(net.dv8tion.jda.api.interactions.components.Button button, User user, ButtonRunnable runnable) {
        this.button = button;
        this.user = user;
        this.runnable = runnable;
    }

    public User getUser() {
        return user;
    }

    public Button getButton() {
        return button;
    }

    public void onClick(ButtonClickEvent event) {
            runnable.run(event);
    }
}
