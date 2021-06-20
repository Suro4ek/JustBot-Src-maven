//package ru.rien.bot.commands.mod;
//
//import net.dv8tion.jda.api.entities.User;
//import org.springframework.stereotype.Component;
//import ru.rien.bot.modules.command.Command;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.modules.punishment.ModulePunishment;
//import ru.rien.bot.objects.GuildWrapper;
//import ru.rien.bot.permission.Permission;
//import ru.rien.bot.utils.GuildUtils;
//import ru.rien.bot.utils.MessageUtils;
//
//@Component
//public class KickCommand implements Command {
//
//    private final ModulePunishment modulePunishment;
//
//    public KickCommand(ModulePunishment modulePunishment) {
//        this.modulePunishment = modulePunishment;
//    }
//
//    @Override
//    public void execute(CommandEvent event) {
//        String[] args = event.getArgs();
//        GuildWrapper guild = event.getGuild();
//        if(args.length == 0){
//            MessageUtils.sendUsage(this,guild,event.getChannel(),event.getSender(),args);
//            return;
//        }
//        event.checkSizeArguments(2);
//        User kicked_by = event.getSender();
//        String userString = args[0];
//        User kicked = GuildUtils.getUser(userString, guild.getGuildId());
//        if (kicked == null) {
//            MessageUtils.sendErrorMessage("Пользователь не найден", event.getChannel());
//            return;
//        }
//        String cause = args[1];
//        modulePunishment.kick(kicked, kicked_by, guild, cause);
//        MessageUtils.sendInfoMessage("Вы кикнули " + kicked.getName(),event.getChannel(),kicked_by);
//    }
//
//    @Override
//    public String getCommand() {
//        return "kick";
//    }
//
//    @Override
//    public String getDescription(GuildWrapper guildWrapper) {
//        return "Кикнуть пользователя";
//    }
//
//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}kick [пользователь] [причина] - Кикнуть пользователя";
//    }
//
//    @Override
//    public CommandType getType() {
//        return CommandType.MODERATION;
//    }
//
//    @Override
//    public Permission getPermission() {
//        return Permission.KICK_COMMAND;
//    }
//}
