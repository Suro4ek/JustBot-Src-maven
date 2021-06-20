//package ru.rien.bot.commands.fun;
//
//import net.dv8tion.jda.api.interactions.InteractionHook;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import net.dv8tion.jda.api.interactions.components.Button;
//import org.springframework.stereotype.Component;
//import ru.rien.bot.modules.command.Command;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.objects.GuildWrapper;
//import ru.rien.bot.permission.Permission;
//
//@Component
//public class TestButtonCommand implements Command {
//    @Override
//    public void execute(CommandEvent event) {
//        String userId = event.getSender().getId();
//        event.getEvent().reply("выбери кнопку")
//                .addActionRow(
//                        Button.secondary(userId+":prune:", "1"),
//                        Button.secondary(userId+":prune1:", "2")
//                )
//
//                .setEphemeral(true)
//                .queue();
//        InteractionHook interactionHook = event.getEvent().getHook();
//
//    }
//
//    @Override
//    public String getCommand() {
//        return "testbutton";
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
//        return CommandType.GENERAL;
//    }
//
//    @Override
//    public OptionData[] parameters() {
//        return new OptionData[0];
//    }
//
//    @Override
//    public Permission getPermission() {
//        return Permission.ALL_PERMISSIONS;
//    }
//}
