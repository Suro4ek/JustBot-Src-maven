package ru.rien.bot.commands.fun.random;

import com.google.common.base.Joiner;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
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
public class GifCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        try {
            String[] args = event.getArgs();
            String tags = "";
            if (args.length > 0) {
                tags = "&tag=" + Joiner.on("+").join(args);
            }
            HttpResponse<JsonNode> response = Unirest.get("http://api.giphy.com/v1/gifs/random?api_key=mSJjgY9ZkbDtu5ON9WYGK6txrwAAFaX5" + tags).asJson();
            MessageEmbed embed = MessageUtils.getEmbed(event.getSender()).
                    setImage(response.getBody().getObject().getJSONObject("data").getString("url"))
                    .build();
            event.getChannel().sendMessage(embed).queue();
//            event.getChannel().sendMessage().queue();
        } catch (Exception ignored) {
            //this exception is about as useful as a nipple on a male
        }
    }

    @Override
    public String getCommand() {
        return "gif";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("GIF_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("GIF_USAGE");
    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public Permission getPermission() {
        return Permission.GIF_COMMAND;
    }
}
