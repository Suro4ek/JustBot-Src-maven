package ru.rien.bot.commands.fun.random;

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
public class RollCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        TextChannel channel = event.getChannel();
        Random random = new Random();
        GuildWrapper guild = event.getGuild();
        User user = event.getSender();
        if(args.length == 0){
            int rand = random.nextInt(100)+1;
            MessageUtils.sendInfoMessage("Случайное число " + rand, channel, user);
        }else if(args.length == 1){
            try {
                int rand = random.nextInt(Integer.parseInt(args[0]))+1;
                MessageUtils.sendInfoMessage("Случайное число " + rand, channel, user);
            }catch (NullPointerException e){
                MessageUtils.sendErrorMessage("Вы ввели не число", channel,user);
            }
        }else if(args.length == 2){
            try {
                int min = Integer.parseInt(args[0]);
                int max = Integer.parseInt(args[1]);
                int rand = random.nextInt(1+max-min)+min;
                MessageUtils.sendInfoMessage("Случайное число " + rand, channel, user);
            }catch (NumberFormatException e){
                MessageUtils.sendErrorMessage("Вы ввели не число", channel,user);
            }
        }else{
            MessageUtils.sendUsage(this, guild,  channel, user, args);
        }
    }

    @Override
    public String getCommand() {
        return "roll";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Случайное число";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}roll - число от 0 до 100\n" +
                "{%}roll [число] - от 0 до числа\n" +
                "{%}roll [от] [до]";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.ROLL_COMMAND;
    }
}
