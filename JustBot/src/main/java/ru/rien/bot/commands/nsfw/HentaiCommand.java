package ru.rien.bot.commands.nsfw;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class HentaiCommand implements Command {


    @Override
    public void execute(CommandEvent event) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://nekobot.xyz/api/image?type=hentai").asJson();
            MessageEmbed embed = MessageUtils.getEmbed(event.getSender()).
                    setImage(response.getBody().getObject().getString("message"))
                    .build();
            event.getChannel().sendMessage(embed).queue();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "hentai";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("HENTAI_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("HENTAI_USAGE");
    }

    @Override
    public CommandType getType() {
        return CommandType.NSWF;
    }

    @Override
    public Permission getPermission() {
        return Permission.HENTAI_COMMAND;
    }
}
