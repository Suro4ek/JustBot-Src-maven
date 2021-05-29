package ru.rien.bot.commands.anime;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class RandomAnime implements Command {
    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        TextChannel channel = event.getChannel();
        try {
            Random random  = new Random();
            int randomInt = random.nextInt(999) + 1;
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.get("https://api.jikan.moe/v3/anime/"+randomInt+"/")
                    .header("accept", "application/json")
                    .asJson();
            JsonNode jsonNode = jsonResponse.getBody();
            if(jsonNode.getObject().isNull("status") || !(jsonNode.getObject().get("status") instanceof Number)) {
                String title_jp = jsonNode.getObject().getString("title_japanese");
                String rating = jsonNode.getObject().getString("rating");
                String premiered = jsonNode.getObject().getString("premiered");
                String title_en = jsonNode.getObject().getString("title");
                String type = jsonNode.getObject().getString("type");
                double score = jsonNode.getObject().getDouble("score");
                String image_url = jsonNode.getObject().getString("image_url");
                JSONArray genres_array = jsonNode.getObject().getJSONArray("genres");
                List<String> genres = new ArrayList<>();
                for (int i=0; i< genres_array.length(); i++){
                    genres.add(genres_array.getJSONObject(i).getString("name"));
                }
                event.getChannel().sendMessage(MessageUtils.getEmbed(user)
                        .setTitle("Случайное аниме").
                                addField("Название",  title_en+"("+title_jp+")", false).
                                addField("Возраст",  rating, false).
                                addField("Премьера",  premiered, false).
                                addField("Тип",  type, false).
                                addField("Рейтинг",  score+"/10", false).
                                addField("Жанры",  genres.stream().collect(Collectors.joining(",")), false).
                                setImage(image_url).
        build())
                        .queue();
            }else{
                MessageUtils.sendErrorMessage("Не смог найти", channel, user);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "randomanime";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Случайное аниме";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}randomanime - Случайное аниме";
    }

    @Override
    public CommandType getType() {
        return CommandType.ANIME;
    }

    @Override
    public Permission getPermission() {
        return Permission.RANDOMANIME_COMMAND;
    }
}
