package ru.rien.bot.commands.fun;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class PngCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        event.checkSizeArguments(1);
        String[] args = event.getArgs();
        StringBuilder done = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            done.append("+").append(args[i]);
        }

        MessageEmbed embed =
                MessageUtils.getEmbed().setImage("https://dummyimage.com/1000x395/417426.png/FFA718/&text=" + done)
                .build();
        event.getChannel().sendMessage(embed).queue();

    }

    @Override
    public String getCommand() {
        return "png";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "получить png картинку с текстом";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}png [текст...] - получить png картинку с текстом";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.PNG_COMAMND;
    }
}
