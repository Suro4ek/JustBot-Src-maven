package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.util.List;

@Component
public class RolesCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args=  event.getArgs();
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        if (args.length <= 1) {
            int page = 1;
            if (args.length == 1) {
                try {
                    page = Integer.valueOf(args[0]);
                } catch (NumberFormatException e) {
                    MessageUtils.sendErrorMessage("Неверный номер страницы: " + args[0] + ".", channel);
                    return;
                }
            }

            List<Role> roles = guild.getGuild().getRoles();

            if (roles.isEmpty()) {
                MessageUtils.sendInfoMessage("В этом сервере нет ролей!", channel, sender);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Role r : roles)
                sb.append(r.getName()).append(" (").append(r.getId()).append(")\n");

            PaginationUtil.sendEmbedPagedMessage(new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(sb.toString(),
                    PaginationUtil.SplitMethod.NEW_LINES, 20))
                    .setTitle("Роли")
                    .setCodeBlock("js")
                    .build(), page - 1, channel, sender, ButtonGroupConstants.ROLES);
        } else {
            MessageUtils.sendUsage(this,guild, channel, sender, args);
        }
    }

    @Override
    public String getCommand() {
        return "roles";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Получает роли сервера";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "`{%}roles [номер]` - Получить роли сервера.";
    }


    @Override
    public Permission getPermission() {
        return Permission.ROLES_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }
}
