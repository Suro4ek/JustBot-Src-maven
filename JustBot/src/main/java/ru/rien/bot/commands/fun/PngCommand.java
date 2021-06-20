package ru.rien.bot.commands.fun;

import net.dv8tion.jda.api.entities.MessageEmbed;
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

@Component
public class PngCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getOptionMappings().get(0).getAsString().split(" ");
        StringBuilder done = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            done.append("+").append(args[i]);
        }

        MessageEmbed embed =
                MessageUtils.getEmbed().setImage("https://dummyimage.com/1000x395/417426.png/FFA718/&text=" + done)
                .build();
        event.getEvent().replyEmbeds(embed).setEphemeral(true).queue();

    }

    @Override
    public String getCommand() {
        return "png";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "получить png картинку с текстом";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}png [текст...] - получить png картинку с текстом";
//    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "text", "text for picture", true)};
    }

    @Override
    public Permission getPermission() {
        return Permission.PNG_COMAMND;
    }
}
