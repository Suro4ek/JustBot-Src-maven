package ru.rien.bot.modules.punishment;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.BanEntity;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.schedule.JustBotTask;
import ru.rien.bot.services.BanService;

import java.util.List;

@Component
public class ModulePunishment extends ModuleDiscord {

    public final BanService banservice;

    public ModulePunishment(BanService banservice) {
        super("punishment", false);
        this.banservice = banservice;
    }

    @Override
    protected void onEnable() {
        new JustBotTask("UnbanTask"){
            @Override
            public void run() {
                List<BanEntity> banEntityList = banservice.gettimeend();
                banEntityList.forEach(banEntity -> {
                    Guild guild = getModuleDsBot().getJda().getGuildById(banEntity.getGuildEntity().getGuildid());
                    if(guild != null) {
                        guild.unban(User.fromId(banEntity.getBanned())).queue();
                    }
                    banservice.delete(banEntity);
                });
            }
        }.repeat(0, 1000*600);
    }

    public void ban(User banned, User banned_by, GuildWrapper guildWrapper,String cause, long delaymilliseconds){
        guildWrapper.getGuild().ban(banned,(int)(delaymilliseconds/1000)/60/60/24,cause).queue();
        banservice.ban(banned_by.getIdLong(), banned.getIdLong(),guildWrapper.getGuildEntity(),
                delaymilliseconds,cause);
    }

}
