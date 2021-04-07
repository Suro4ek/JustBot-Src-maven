package ru.rien.bot.api.music.hooks;


import ru.rien.bot.api.music.player.Player;

public interface PlayerCreateHook extends Hook {
    void execute(Player player);
}