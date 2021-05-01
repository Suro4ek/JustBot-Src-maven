package ru.rien.bot.modules.stats;

import com.google.common.base.Enums;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

@Component
public class AStatsCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        GuildWrapper guild = event.getGuild();
        if (guild.getGuildEntity().isStats()) {
            Category category = guild.getGuild().getCategoryById(guild.getGuildEntity().getStatsid());
            if (category != null) {
                category.delete().queue();
            }
            guild.removestats(guild.getGuildEntity().getStatsid());
            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(event.getSender())
                    .setDescription("Статистика выключена")
                    .build(), 2000L, event.getChannel());
        } else {
            LocalDate date = LocalDate.now();
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            Category category = guild.getGuild().createCategory("Stats").complete();
            guild.initstats(category.getIdLong());
            category.createVoiceChannel(date.format(outputFormat)).addPermissionOverride(guild.getGuild()
                            .getPublicRole(), null,
                    EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT))
                    .queue(voiceChannel -> {
                                voiceChannel.createCopy().setName("Участников: " + guild.getGuild().getMembers().size()).queue();
                            }
                    );
            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(event.getSender())
                    .setDescription("Статистика включена")
                    .build(), 2000L, event.getChannel());
        }
    }

    @Override
    public String getCommand() {
        return "astats";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "статистика в категориях";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}astats - включить/выключить статистику";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }
}
