package ru.rien.bot.commands.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class Covid implements Command {
    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        User user = event.getSender();
        if(args.length == 0){

        }else{
            String country = args[1];
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://disease.sh/v3/covid-19/countries/" + country).asJson();
                if(response.getBody().getObject().getString("message") != null){
                    MessageUtils.sendErrorMessage("Страна не найдена", event.getChannel());
                    return;
                }
                JSONObject jsObject = response.getBody().getObject();
                String countryimg = jsObject.getJSONObject("countryInfo").getString("flag");
                event.getChannel().sendMessage(MessageUtils.getEmbed(user)
                .setTitle("Ковид в " + country).
                                addField("Всего","**Зараженных** " + jsObject.getString("cases") + "\n" +
                                        "**Выздоровели** " + jsObject.getString("recovered") +"\n" +
                                        "**Умерло** " + jsObject.getString("deaths") + "\n" +
                                        "**Население** " + jsObject.getString("population"), true).
                                addField("Сегодня","**Зараженных** " + jsObject.getString("todayCases") + "\n" +
                                        "**Выздоровели** " + jsObject.getString("todayRecovered") +"\n" +
                                        "**Умерло** " + jsObject.getString("todayDeaths"), true).
                                addField("**В критическом состоянии** ", jsObject.getString("critical"), true).
                                addField("**Тестов** ", jsObject.getString("tests"), true).
                                setImage(countryimg).
//                                setFooter("`Последнее обновление `").
                                build())
                        .queue();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getCommand() {
        return "covid";
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
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.COVID_COMMAND;
    }
}
