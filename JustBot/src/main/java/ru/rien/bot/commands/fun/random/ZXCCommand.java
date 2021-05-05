//package ru.rien.bot.commands.fun.random;
//
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.JsonNode;
//import com.mashape.unirest.http.Unirest;
//import com.mashape.unirest.http.exceptions.UnirestException;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.stereotype.Component;
//import ru.rien.bot.modules.command.Command;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.modules.messsage.Message;
//import ru.rien.bot.objects.GuildWrapper;
//import ru.rien.bot.permission.Permission;
//import ru.rien.bot.utils.MessageUtils;
//
//import java.io.IOException;
//
//@Component
//public class ZXCCommand implements Command {
//    @Override
//    public void execute(CommandEvent event) {
//        try {
//            HttpResponse<JsonNode> response = Unirest.get("https://tiktok28.p.rapidapi.com/hashtag/zxc")
//                    .header("x-rapidapi-key", "f08096119cmsh6009f2bab8a020bp1d10dfjsna361d7962a0f")
//                    .header("x-rapidapi-host", "tiktok28.p.rapidapi.com")
//                    .asJson();
//            System.out.println(response.getBody().getArray().getJSONObject(0).getString("videoUrl"));
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getCommand() {
//        return "zxc";
//    }
//
//    @Override
//    public String getDescription(GuildWrapper guildWrapper) {
//        return "";
//    }
//
//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "";
//    }
//
//    @Override
//    public CommandType getType() {
//        return CommandType.RANDOM;
//    }
//
//    @Override
//    public Permission getPermission() {
//        return Permission.ZXC_COMMAND;
//    }
//}
