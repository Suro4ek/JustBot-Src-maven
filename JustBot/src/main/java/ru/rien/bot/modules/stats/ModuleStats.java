package ru.rien.bot.modules.stats;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.schedule.JustBotTask;
import ru.rien.bot.services.GuildService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;


@Component
public class ModuleStats extends ModuleDiscord {

    private final GuildService guildService;

    public ModuleStats(GuildService guildService) {
        super("stats", false);
        this.guildService = guildService;
    }

    @Override
    protected void onEnable() {
        registerListenerThis();
        new JustBotTask("UpdateDateTask") {
            @Override
            public void run() {
                guildService.findall().forEach(guildEntity -> {
                    GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuildNoCache("" + guildEntity.getGuildid());
                    if (guildWrapper.getGuildEntity().isStats()) {
                        Category category = guildWrapper.getGuild().getCategoryById(guildWrapper.getGuildEntity().getStatsid());
                        if (category != null) {
                            VoiceChannel v1 = category.getVoiceChannels().get(0);
                            if (v1 != null) {
                                LocalDate date = LocalDate.now();
                                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                v1.getManager().setName(date.format(outputFormat)).queue();
                            }
                            VoiceChannel v2 = category.getVoiceChannels().get(1);
                            if (v2 != null) {
                                v2.getManager().setName("Участников " + guildWrapper.getGuild().getMemberCount()).queue();
                            }
                        }
                    }
                });
            }
        }.repeat(0, 1000 * 60 * 60 * 12);
    }


    @Override
    public void onGuildMemberJoinEvent(@NotNull GuildMemberJoinEvent event) {
        GuildWrapper guild = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if (guild.getGuildEntity().isStats()) {
            Category category = guild.getGuild().getCategoryById(guild.getGuildEntity().getStatsid());
            if (category != null) {
                VoiceChannel v1 = category.getVoiceChannels().get(1);
                if (v1 != null) {
                    v1.getManager().setName("Участников " + event.getGuild().getMemberCount()).queue();
                }
            } else {
                LocalDate date = LocalDate.now();
                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                category = guild.getGuild().createCategory("Stats").complete();
                guild.initstats(category.getIdLong());
                category.createVoiceChannel(date.format(outputFormat)).addPermissionOverride(guild.getGuild()
                                .getPublicRole(), null,
                        EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT))
                        .queue(voiceChannel -> {
                                    voiceChannel.createCopy().setName("Участников " + event.getGuild().getMemberCount()).queue();
                                }
                        );
            }
        }
    }

    @Override
    public void onGuildMemberRemoveEvent(@NotNull GuildMemberRemoveEvent event) {
        GuildWrapper guild = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if (guild.getGuildEntity().isStats()) {
            Category category = guild.getGuild().getCategoryById(guild.getGuildEntity().getStatsid());
            if (category != null) {
                VoiceChannel v1 = category.getVoiceChannels().get(1);
                if (v1 != null) {
                    v1.getManager().setName("Участников " + event.getGuild().getMemberCount()).queue();
                }
            } else {
                LocalDate date = LocalDate.now();
                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                category = guild.getGuild().createCategory("Stats").complete();
                guild.initstats(category.getIdLong());
                category.createVoiceChannel(date.format(outputFormat)).addPermissionOverride(guild.getGuild()
                                .getPublicRole(), null,
                        EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT))
                        .queue(voiceChannel -> {
                                    voiceChannel.createCopy().setName("Участников " + event.getGuild().getMemberCount()).queue();
                                }
                        );
            }
        }
    }
}
