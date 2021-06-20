package ru.rien.bot.commands.fun.random.animals;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
public class KangarooCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://some-random-api.ml/animal/kangaroo").asJson();
            MessageEmbed embed = MessageUtils.getEmbed(event.getSender()).
                    setImage(response.getBody().getObject().getString("image"))
                    .build();
            event.getEvent().replyEmbeds(embed).queue();
        } catch (UnirestException e) {
            MessageUtils.sendErrorMessage("Не удалось отправить запрос", event.getEvent().deferReply(true));
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "kangaroo";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "Случайная кенгуру";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}kangaroo - Случайная кенгуру";
//    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }

    @Override
    public Permission getPermission() {
        return Permission.KANGAROO_COMMAND;
    }
}
