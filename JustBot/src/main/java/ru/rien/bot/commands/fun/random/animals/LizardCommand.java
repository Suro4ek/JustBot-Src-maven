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
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class LizardCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://nekos.life/api/v2/img/lizard").asJson();
            MessageEmbed embed = MessageUtils.getEmbed(event.getSender()).
                    setImage(response.getBody().getObject().getString("url"))
                    .build();
            event.getChannel().sendMessage(embed).queue();
        } catch (UnirestException e) {
            MessageUtils.sendErrorMessage("Не удалось отправить запрос", event.getChannel());
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "lizard";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Случайная ящерица";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}lizard - Случайная ящерица";
    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public Permission getPermission() {
        return Permission.LIZARD_COMMAND;
    }
}
