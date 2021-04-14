package ru.rien.bot.modules.general;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.GuildService;

@Component
public class ModuleGeneral extends ModuleDiscord {

    public final GuildService guildService;

    public ModuleGeneral(GuildService guildService) {
        super("general", false);
        this.guildService = guildService;
    }

    @Override
    protected void onEnable() {
        registerListenerThis();
    }

    @Override
    public void onGuildLeaveEvent(@NotNull GuildLeaveEvent event) {
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        guildService.deleteGuild(guildWrapper);
    }

    @Override
    public void onGuildJoinEvent(@NotNull GuildJoinEvent event) {
        GuildEntity guildEntity = new GuildEntity().setGuildid(event.getGuild().getIdLong())
                .setPrefix('!');
        guildService.save(guildEntity);
    }
}
