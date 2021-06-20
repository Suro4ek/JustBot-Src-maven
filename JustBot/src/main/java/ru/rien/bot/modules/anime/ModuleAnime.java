package ru.rien.bot.modules.anime;

import org.springframework.stereotype.Component;
import ru.rien.bot.module.ModuleDiscord;

@Component
public class ModuleAnime extends ModuleDiscord {

    public ModuleAnime() {
        super("anime", false);
    }

    @Override
    protected void onEnable() {

    }
}
