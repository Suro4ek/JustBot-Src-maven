//package ru.rien.bot.modules.reactinrole;
//
//import com.vdurmont.emoji.EmojiParser;
//import net.dv8tion.jda.api.entities.Emote;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import ru.rien.bot.modules.command.Command;
//import ru.rien.bot.modules.command.CommandEvent;
//import ru.rien.bot.modules.command.CommandType;
//import ru.rien.bot.objects.GuildWrapper;
//import ru.rien.bot.permission.Permission;
//import ru.rien.bot.services.ReactionService;
//import ru.rien.bot.utils.MessageUtils;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class ReactionRoleCommand implements Command {
//    public final ReactionService reactionService;
//
//    public ReactionRoleCommand(ReactionService reactionService) {
//        this.reactionService = reactionService;
//    }
//
//    @Override
//    public void execute(CommandEvent event) {
//        GuildWrapper guild = event.getGuild();
//        TextChannel channel = event.getChannel();
//        User user = event.getSender();
//        if(args.length == 0){
//            MessageUtils.sendUsage(this, guild, channel, user, args);
//        }else{
//            if(args[0].equalsIgnoreCase("create")){
//                Long message_id = Long.parseLong(args[1]);
//                channel.retrieveMessageById(message_id).queue(message -> {
//                    if(message!= null) {
//                        if(reactionService.checkmessage(message_id, guild.getGuildEntity())) {
//                            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Сообщение создано").build(), TimeUnit.SECONDS.toMillis(5), channel);
//                            reactionService.createMessage(message_id, guild);
//                        }else{
//                            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Уже есть сообщение с этим").build(), TimeUnit.SECONDS.toMillis(5), channel);
//                        }
//                    }
//                });
//            }else if(args[0].equalsIgnoreCase("add")){
//                Long message_id = Long.parseLong(args[1]);
//                Long role = Long.parseLong(args[2]);
//                String reaction = args[3];
//                try {
//                    Long custom_emoji = Long.parseLong(reaction);
//                    if(guild.getGuild().getEmoteById(custom_emoji) != null) {
//                        channel.addReactionById(message_id, guild.getGuild().getEmoteById(custom_emoji)).queue();
//                    }
//                }catch (NumberFormatException exception){
//                    channel.addReactionById(message_id, reaction).queue();
//                    reaction = EmojiParser.parseToAliases(reaction);
//                }
//                guild.getReactionLoader().addReaction(message_id, reaction, role);
//                reactionService.addReactionRole(message_id, guild);
//                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Роль добавлена").build(), TimeUnit.SECONDS.toMillis(5), channel);
//            }else if(args[0].equalsIgnoreCase("remove")){
//                Long message_id = Long.parseLong(args[1]);
//                Long role = Long.parseLong(args[2]);
//                String reaction = args[3];
//                try {
//                    Long custom_emoji = Long.parseLong(reaction);
//                    if(guild.getGuild().getEmoteById(custom_emoji) != null) {
//                        channel.removeReactionById(message_id, guild.getGuild().getEmoteById(custom_emoji)).queue();
//                    }
//                }catch (NumberFormatException exception){
//                    channel.removeReactionById(message_id, reaction).queue();
//                    reaction = EmojiParser.parseToAliases(reaction);
//                }
//                guild.getReactionLoader().removeReaction(message_id, reaction, role);
//                reactionService.removeReactionRole(message_id, guild);
//                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(user).setDescription("Роль удалена").build(), TimeUnit.SECONDS.toMillis(5), channel);
//
//            }
//        }
//    }
//
//    @Override
//    public String getCommand() {
//        return "reactionr";
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
//        return CommandType.ADMIN;
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
