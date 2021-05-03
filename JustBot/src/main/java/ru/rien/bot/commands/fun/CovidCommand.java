package ru.rien.bot.commands.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class CovidCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        User user = event.getSender();
        if(args.length == 0){
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://disease.sh/v3/covid-19/all").asJson();
                try {
                    response.getBody().getObject().getString("message");
                    MessageUtils.sendErrorMessage("Страна не найдена", event.getChannel());
                    return;
                }catch (JSONException ignored){

                }
                JSONObject jsObject = response.getBody().getObject();
                event.getChannel().sendMessage(MessageUtils.getEmbed(user)
                        .setTitle("Ковид").
                                addField("Всего","**Зараженных** " + jsObject.getLong("cases") + "\n" +
                                        "**Выздоровели** " + jsObject.getLong("recovered") +"\n" +
                                        "**Умерло** " + jsObject.getLong("deaths") + "\n" +
                                        "**Население** " + jsObject.getLong("population"), true).
                                addField("Сегодня","**Зараженных** " + jsObject.getLong("todayCases") + "\n" +
                                        "**Выздоровели** " + jsObject.getLong("todayRecovered") +"\n" +
                                        "**Умерло** " + jsObject.getLong("todayDeaths"), true).
                                addField("**В критическом состоянии** ", jsObject.getLong("critical")+"", true).
                                addField("**Тестов** ", jsObject.getLong("tests")+"", true).
//                                setFooter("`Последнее обновление `").
        build())
                        .queue();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }else{
            String country = args[0];
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://disease.sh/v3/covid-19/countries/" + country).asJson();
                try {
                    response.getBody().getObject().getString("message");
                    MessageUtils.sendErrorMessage("Страна не найдена", event.getChannel());
                    return;
                }catch (JSONException ignored){

                }
                JSONObject jsObject = response.getBody().getObject();
                String countryimg = jsObject.getJSONObject("countryInfo").getString("flag");
                event.getChannel().sendMessage(MessageUtils.getEmbed(user)
                .setTitle("Ковид в " + country).
                                addField("Всего","**Зараженных** " + jsObject.getLong("cases") + "\n" +
                                        "**Выздоровели** " + jsObject.getLong("recovered") +"\n" +
                                        "**Умерло** " + jsObject.getLong("deaths") + "\n" +
                                        "**Население** " + jsObject.getLong("population"), true).
                                addField("Сегодня","**Зараженных** " + jsObject.getLong("todayCases") + "\n" +
                                        "**Выздоровели** " + jsObject.getLong("todayRecovered") +"\n" +
                                        "**Умерло** " + jsObject.getLong("todayDeaths"), true).
                                addField("**В критическом состоянии** ", jsObject.getLong("critical")+"", true).
                                addField("**Тестов** ", jsObject.getLong("tests")+"", true).
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
