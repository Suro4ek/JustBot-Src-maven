//package ru.rien.bot.commands.admin;
//
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import net.dv8tion.jda.api.interactions.commands.OptionType;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import org.springframework.stereotype.Component;
//import ru.rien.bot.modules.command.Command;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.modules.messsage.Language;
//import ru.rien.bot.objects.GuildWrapper;
//import ru.rien.bot.permission.Permission;
//import ru.rien.bot.utils.MessageUtils;
//
//import java.util.List;
//
//@Component
//public class SetPrefixCommand implements Command {
//
//    @Override
//    public void execute(CommandEvent event) {
//        List<OptionMapping> parameters = event.getOptionMappings();
//        GuildWrapper guild = event.getGuild();
//        TextChannel channel = event.getChannel();
//        User sender = event.getSender();
//        if (parameters.size() == 1) {
//            String args1 = parameters.get(0).getAsString();
//            if (args1.equalsIgnoreCase("reset")) {
//                guild.setPrefix('!');
//
//                event.getEvent().reply(Language.getLanguage(0).getMessage("PREFIX_RESET")).setEphemeral(true).queue();
//            } else if (args1.length() == 1) {
//                guild.setPrefix(args1.charAt(0));
//                event.getEvent().reply(Language.getLanguage(0).getMessage("PREFIX_SET")).setEphemeral(true).queue();
//            } else {
//                event.getEvent().reply(Language.getLanguage(0).getMessage("PREFIX_MORE_SYMBOLS")).setEphemeral(true).queue();
//            }
//        } else {
//            event.getEvent().reply(Language.getLanguage(0).getMessage("CURRENT_PREFIX").replace("%s",guild.getPrefix()+"")).setEphemeral(true).queue();
//        }
//    }
//
//    @Override
//    public String getCommand() {
//        return "prefix";
//    }
//
//    @Override
//    public String getDescription(GuildWrapper guildWrapper) {
//        return Language.getLanguage(0).getMessage("PREFIX_DESCRIPTION");
//    }
//
//
//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return Language.getLanguage(0).getMessage("PREFIX_USAGE");
//    }
//
//    @Override
//    public Permission getPermission() {
//        return Permission.ALL_PERMISSIONS;
//    }
//
//    @Override
//    public CommandType getType() {
//        return CommandType.ADMIN;
//    }
//
//    @Override
//    public OptionData[] parameters() {
//        return new OptionData[]{new OptionData(OptionType.STRING, "prefix", "установить префикс", false)};
//    }
//
//    @Override
//    public String[] getAliases() {
//        return new String[]{"setprefix"};
//    }
//}