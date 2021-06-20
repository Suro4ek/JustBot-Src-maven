package ru.rien.bot.commands.fun.random;

import com.google.common.base.Joiner;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
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

import java.util.List;

@Component
public class GifCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        try {
            String tags = "";
            List<OptionMapping> optionMappings = event.getOptionMappings();
            if (optionMappings.size() > 0) {
                String[] args = optionMappings.get(0).getAsString().split(" ");
                tags = "&tag=" + Joiner.on("+").join(args);
            }
            HttpResponse<JsonNode> response = Unirest.get("https://api.giphy.com/v1/gifs/random?api_key=mSJjgY9ZkbDtu5ON9WYGK6txrwAAFaX5" + tags).asJson();
            event.getEvent().reply(response.getBody().getObject().getJSONObject("data").getString("url")).queue();
        } catch (Exception ignored) {
            MessageUtils.sendErrorMessage("Не удалось отправить запрос", event.getEvent().deferReply(true));
        }
    }

    @Override
    public String getCommand() {
        return "gif";
    }

    @Override
    public String getDescription(Language guild) {
        return guild.getMessage("GIF_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return Language.getLanguage(guild.getLang()).getMessage("GIF_USAGE");
//    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "args", "args for search").setRequired(false)};
    }

    @Override
    public Permission getPermission() {
        return Permission.GIF_COMMAND;
    }
}
