package ru.rien.bot.commands.fun.random;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;
import java.util.Random;

@Component
public class RollCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        Random random = new Random();
        GuildWrapper guild = event.getGuild();
        User user = event.getSender();
        List<OptionMapping> optionMappings = event.getOptionMappings();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        if(optionMappings.size() == 0){
            int rand = random.nextInt(100)+1;
            MessageUtils.sendInfoMessage("Случайное число " + rand, replyAction, user);
        }else if(optionMappings.size() == 1){
            try {
                int one = (int)optionMappings.get(0).getAsLong();
                int rand = random.nextInt(one)+1;
                MessageUtils.sendInfoMessage("Случайное число " + rand, replyAction, user);
            }catch (NullPointerException e){
                MessageUtils.sendErrorMessage("Вы ввели не число", replyAction,user);
            }
        }else if(optionMappings.size() == 2){
            try {
                int one = (int)optionMappings.get(0).getAsLong();
                int two = (int)optionMappings.get(1).getAsLong();
                int rand = random.nextInt(1+two-one)+one;
                MessageUtils.sendInfoMessage("Случайное число " + rand, replyAction, user);
            }catch (NumberFormatException e){
                MessageUtils.sendErrorMessage("Вы ввели не число", replyAction,user);
            }
        }
    }

    @Override
    public String getCommand() {
        return "roll";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "Случайное число";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}roll - число от 0 до 100\n" +
//                "{%}roll [число] - от 0 до числа\n" +
//                "{%}roll [от] [до]";
//    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "one", "from or to"), new OptionData(OptionType.INTEGER, "two", "to")};
    }

    @Override
    public Permission getPermission() {
        return Permission.ROLL_COMMAND;
    }
}
