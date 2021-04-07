package ru.rien.bot.api.music.hooks;


import ru.rien.bot.api.music.player.Item;
import ru.rien.bot.api.music.player.Player;

/**
 * Called when calling {@link Player#queue}.
 */
@FunctionalInterface
public interface QueueHook extends Hook {
    void execute(Player player, Item item);
}