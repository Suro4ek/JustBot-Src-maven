package ru.rien.bot.commands.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.Random;

@Component
public class UrlShotCommand implements Command {

    private String generateString(){
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567980";
        StringBuilder random = new StringBuilder();
        Random rnd = new Random();
        while(random.length() < 15){
            int index = (int) (rnd.nextFloat() * CHARS.length());
            random.append(CHARS.charAt(index));
        }
        return random.toString();
    }

    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User user = event.getSender();
        String[] args = event.getArgs();
        if(args.length>=1){
            String shot_url = args[0];
            String name = generateString();
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://cutt.ly/api/api.php?key=7e71a55b3f6045ab6f29eff2bac7cc938c675&short="+shot_url+ "&name=" + name).asJson();
                int status = response.getBody().getObject().getJSONObject("url").getInt("status");
                if(status == 7)
                     MessageUtils.sendInfoMessage("Длинный url: "+shot_url+"\n" +
                         "Короткий: "+response.getBody().getObject().getJSONObject("url").getString("shortLink"), channel, user);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }else{
            MessageUtils.sendUsage(this, guild, channel, user, args);
        }
    }

    @Override
    public String getCommand() {
        return "urlshot";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Укорачивает ссылки";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}urlshot [ссылка] - укоротить ссылку";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.URLSHOT_COMMAND;
    }
}
