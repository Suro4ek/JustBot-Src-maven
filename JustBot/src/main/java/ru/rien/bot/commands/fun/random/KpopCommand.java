package ru.rien.bot.commands.fun.random;

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
public class KpopCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://apis.duncte123.me/kpop").asJson();
            MessageEmbed embed = MessageUtils.getEmbed(event.getSender()).
                    setImage(response.getBody().getObject().getJSONObject("data").getString("img"))
                    .build();
            event.getChannel().sendMessage(embed).queue();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "kpop";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public Permission getPermission() {
        return Permission.KPOP_COMMAND;
    }
}
