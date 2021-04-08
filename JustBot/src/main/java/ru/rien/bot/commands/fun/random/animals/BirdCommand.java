package ru.rien.bot.commands.fun.random.animals;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class BirdCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://some-random-api.ml/img/birb").asJson();
            MessageEmbed embed = MessageUtils.getEmbed().
                    setImage(response.getBody().getObject().getString("link"))
                    .build();
            event.getChannel().sendMessage(embed).queue();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "bird";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("BIRD_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("BIRD_USAGE");
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.BIRD_COMMAND;
    }
}
