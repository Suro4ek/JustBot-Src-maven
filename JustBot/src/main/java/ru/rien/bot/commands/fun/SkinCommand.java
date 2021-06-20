package ru.rien.bot.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class SkinCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        String name = event.getOptionMappings().get(0).getAsString().split(" ")[0];
        EmbedBuilder e = MessageUtils.getEmbed();
        e.setTitle("Скин игрока " + name, "https://minotar.net/download/" + name);
        e.setImage("https://minotar.net/armor/body/" + name+ "/300.png");
        e.setThumbnail("https://minotar.net/cube/" + name + "/500.png");
        event.getEvent().replyEmbeds(e.build()).setEphemeral(true).queue();
    }

    @Override
    public String getCommand() {
        return "skin";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "получить скин игрока из mojang";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}skin [ник игрока] - скин игрока из mojang";
//    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "name", "name is mojang site", true)};
    }

    @Override
    public Permission getPermission() {
        return Permission.SKIN_COMMAND;
    }
}
