//package ru.rien.bot.commands.admin.permissions;
//
//import net.dv8tion.jda.api.interactions.commands.OptionType;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.modules.command.SubCommand;
//
//public class PermissionsTest implements SubCommand {
//    @Override
//    public SubcommandData getSubCommands() {
//        return new SubcommandData("test", "ss");
//    }
//
//    @Override
//    public void execute(CommandEvent event) {
//        System.out.println(event.getOptionMappings().size());
//        event.getEvent().reply("sdsd").queue();
//    }
//
//    @Override
//    public CommandType getType() {
//        return CommandType.ADMIN;
//    }
//
//    @Override
//    public OptionData[] parameters() {
//        return new OptionData[]{new OptionData(OptionType.STRING, "user1", "for user123")};
//    }
//}
