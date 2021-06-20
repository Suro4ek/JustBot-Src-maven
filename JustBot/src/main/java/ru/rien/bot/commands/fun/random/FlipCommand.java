package ru.rien.bot.commands.fun.random;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.Random;

@Component
public class FlipCommand implements Command {
    @Override
    public void execute(CommandEvent event){
        Random random = new Random();
        float rand = random.nextFloat();
        if(rand > 0.5){
            MessageUtils.sendInfoMessage("Орел", event.getEvent().deferReply(), event.getSender());
        }else{
            MessageUtils.sendInfoMessage("Решка", event.getEvent().deferReply(), event.getSender());
        }
    }

    @Override
    public String getCommand() {
        return "flip";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "Орел или решка";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}flip - игра орел или решка";
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
        return Permission.FLIP_COMMAND;
    }
}
