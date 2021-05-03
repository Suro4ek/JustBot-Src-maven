package ru.rien.bot.modules.reactinrole;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.services.ReactionService;
import ru.rien.bot.utils.MessageUtils;

import java.util.concurrent.TimeUnit;

@Component
public class ReactionRoleCommand implements Command {
    public final ReactionService reactionService;

    public ReactionRoleCommand(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        User user = event.getSender();
        if(args.length == 0){
            MessageUtils.sendUsage(this, guild, channel, user, args);
        }else{
            if(args[0].equalsIgnoreCase("create")){
                Long message_id = Long.parseLong(args[1]);
                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Сообщение создано").build(), TimeUnit.SECONDS.toMillis(5), channel);
                reactionService.createMessage(message_id, guild);
            }else if(args[0].equalsIgnoreCase("add")){
                Long message_id = Long.parseLong(args[1]);
                Long role = Long.parseLong(args[2]);
                Long reaction = Long.parseLong(args[3]);
                reactionService.addReactionRole(message_id, reaction, role, guild);
                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Роль добавлена").build(), TimeUnit.SECONDS.toMillis(5), channel);
            }else if(args[0].equalsIgnoreCase("remove")){
                Long message_id = Long.parseLong(args[1]);
                Long role = Long.parseLong(args[2]);
                Long reaction = Long.parseLong(args[3]);
                reactionService.addReactionRole(message_id, reaction, role, guild);
                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Роль удалена").build(), TimeUnit.SECONDS.toMillis(5), channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "reactionr";
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
        return CommandType.ADMIN;
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }
}
