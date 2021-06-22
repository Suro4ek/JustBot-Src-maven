package ru.rien.bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl;
import org.apache.commons.lang3.StringUtils;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.GuildWrapper;

import java.awt.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.EmbedBuilder.ZERO_WIDTH_SPACE;

public class MessageUtils {

    private static JDA cachedJDA;

    public static void sendErrorMessage(String message, TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        embedBuilder.setDescription(message);

        channel.sendMessage(embedBuilder.build()).queue();
    }

    public static void sendErrorMessage(String message, ReplyAction channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        embedBuilder.setDescription(message);
        ReplyActionImpl action = (ReplyActionImpl)channel;
        action.addEmbeds(embedBuilder.build()).queue();
    }

    public static void sendErrorMessage(String message, InteractionHook channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        embedBuilder.setDescription(message);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void sendErrorMessage(MessageEmbed message, ReplyAction channel) {
        ReplyActionImpl action = (ReplyActionImpl)channel;
        action.addEmbeds(message).queue();
    }

    public static void sendErrorMessage(MessageEmbed message, InteractionHook channel) {
        channel.sendMessageEmbeds(message).queue();
    }

    public static void sendInfoMessage(String message, TextChannel channel, User sender) {
        sendMessage(MessageType.INFO, message, channel, sender);
    }

    public static void sendInfoMessage(String message, ReplyAction replyAction, User sender) {
        sendMessage(MessageType.INFO, message, replyAction, sender);
    }

    public static void sendErrorMessage(EmbedBuilder builder, TextChannel channel) {
        sendMessage(MessageType.ERROR, builder, channel);
    }

    public static void sendInfoMessage(EmbedBuilder builder, ReplyAction replyAction) {
        sendMessage(MessageType.INFO, builder, replyAction);
    }

    // Root of sendMessage(Type, Builder, channel)
    public static void sendMessage(MessageType type, EmbedBuilder builder, TextChannel channel) {
        sendMessage(type, builder, channel, 0);
    }


    public static void sendErrorMessage(String message, TextChannel channel, User sender) {
        sendMessage(MessageType.ERROR, message, channel, sender);
    }

    public static void sendErrorMessage(String message, ReplyAction channel, User sender) {
        sendMessage(MessageType.ERROR, message, channel, sender);
    }

    public static String getMessage(String[] args, int min) {
        return Arrays.stream(args).skip(min).collect(Collectors.joining(" ")).trim();
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static EmbedBuilder getEmbed() {
        if (cachedJDA == null || cachedJDA.getStatus() != JDA.Status.CONNECTED)
            cachedJDA = ModuleDsBot.getInstance().getJda();

        EmbedBuilder defaultEmbed = new EmbedBuilder().setColor(Color.CYAN);

        if (cachedJDA != null) {
            defaultEmbed.setAuthor("JustBot", null, cachedJDA.getSelfUser().getEffectiveAvatarUrl());
        }

        return defaultEmbed.setColor(Color.CYAN);
    }

    public static String getTag(User user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static EmbedBuilder getEmbed(User user) {
        return getEmbed().setFooter("От @" + getTag(user), user.getEffectiveAvatarUrl());
    }

    public static void sendUsage(Command command, GuildWrapper guildWrapper, TextChannel channel, User user, String[] args) {
        String title = capitalize("Как использовать "+command.getCommand()+"?");
        List<String> usages = UsageParser.matchUsage(command,guildWrapper, args);

        String usage = FormatUtils.formatCommandPrefix(channel.getGuild(), usages.stream().collect(Collectors.joining("\n")));
        EmbedBuilder b = getEmbed(user).setTitle(title, null).setDescription(usage).setColor(Color.pink);
        if (command.getExtraInfo() != null) {
            b.addField("Доп. инфо", command.getExtraInfo(), false);
        }
        channel.sendMessage(b.build()).queue();
    }

    public static String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(StringUtils.repeat("-", size + padding * 2));
            } else {
                ret.append(middle).append(StringUtils.repeat("-", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }

    public static String getFooter(String footer, int padding, int... sizes) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        int total = 0;
        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            total += size + (i == sizes.length - 1 ? 0 : 1) + padding * 2;
        }
        sb.append(footer);
        sb.append(StringUtils.repeat(" ", total - footer.length()));
        sb.append("|\n");
        return sb.toString();
    }

    public static void sendAutoDeletedMessage(Message message, long delay, MessageChannel channel) {
        channel.sendMessage(message).queue(msg -> autoDeleteMessage(msg, delay));
    }
    public static void sendAutoDeletedMessage(MessageEmbed messageEmbed, long delay, MessageChannel channel) {
        channel.sendMessage(messageEmbed).queue(msg -> autoDeleteMessage(msg, delay));
    }

    public static void autoDeleteMessage(Message message, long delay) {
        message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
    }

    public static void editMessage(String s, EmbedBuilder embed, Message message) {
        if (message != null)
            message.editMessage(new MessageBuilder().setContent((s == null ? ZERO_WIDTH_SPACE : s)).setEmbed(embed.build()).build()).queue();
    }

    public static void editMessage(Message message, String content) {
        message.editMessage(content).queue();
    }

    public static void sendMessage(MessageType type, String message, TextChannel channel) {
        sendMessage(type, message, channel, null);
    }

    public static void sendMessage(MessageType type, String message, ReplyAction channel) {
        sendMessage(type, message, channel, null);
    }
    public static void sendWarningMessage(String message, TextChannel channel) {
        sendMessage(MessageType.WARNING, message, channel);
    }

    public static void sendWarningMessage(String message, ReplyAction channel) {
        sendMessage(MessageType.WARNING, message, channel);
    }

    public static void sendWarningMessage(String message, TextChannel channel, User sender) {
        sendMessage(MessageType.WARNING, message, channel, sender);
    }

    public static void sendWarningMessage(String message, ReplyAction channel, User sender) {
        sendMessage(MessageType.WARNING, message, channel, sender);
    }

    public static void sendMessage(MessageType type, String message, TextChannel channel, User sender) {
        sendMessage(type, message, channel, sender, 0);
    }

    public static void sendMessage(MessageType type, String message, ReplyAction replyAction, User sender) {
        sendMessage(type, (sender != null ? getEmbed(sender) : getEmbed()).setColor(type.getColor())
                        .setTimestamp(OffsetDateTime.now(Clock.systemUTC()))
                        .setDescription(message)
                , replyAction);
    }


    public static void sendMessage(MessageType type, String message, TextChannel channel, User sender, long autoDeleteDelay) {
        sendMessage(type, (sender != null ? getEmbed(sender) : getEmbed()).setColor(type.getColor())
                        .setTimestamp(OffsetDateTime.now(Clock.systemUTC()))
                        .setDescription(FormatUtils.formatCommandPrefix((channel != null ? channel.getGuild() : null), message))
                , channel, autoDeleteDelay);
    }

    public static void sendMessage(MessageType type, EmbedBuilder builder, ReplyAction replyAction) {
        if (builder.build().getColor() == null)
            builder.setColor(type.getColor());

//        if (type != MessageType.WARNING && type != MessageType.ERROR && builder.getFields().isEmpty()) {
//            Optional<String> globalMsg = getGlobalMessage();
//            if ((!lastGlobalMsg.containsKey(channel.getIdLong())
//                    || System.currentTimeMillis() - lastGlobalMsg.get(channel.getIdLong()) >= GLOBAL_MSG_DELAY)
//                    && globalMsg.isPresent()) {
//                lastGlobalMsg.put(channel.getIdLong(), System.currentTimeMillis());
//
//                builder.setDescription(builder.build().getDescription() + "\n\n" + globalMsg.get());
//            }
//        }
        ReplyActionImpl action = (ReplyActionImpl)replyAction;
        action.addEmbeds(builder.build()).queue();
    }

    public static void sendMessage(MessageType type, EmbedBuilder builder, TextChannel channel, long autoDeleteDelay) {
        if (builder.build().getColor() == null)
            builder.setColor(type.getColor());

//        if (type != MessageType.WARNING && type != MessageType.ERROR && builder.getFields().isEmpty()) {
//            Optional<String> globalMsg = getGlobalMessage();
//            if ((!lastGlobalMsg.containsKey(channel.getIdLong())
//                    || System.currentTimeMillis() - lastGlobalMsg.get(channel.getIdLong()) >= GLOBAL_MSG_DELAY)
//                    && globalMsg.isPresent()) {
//                lastGlobalMsg.put(channel.getIdLong(), System.currentTimeMillis());
//
//                builder.setDescription(builder.build().getDescription() + "\n\n" + globalMsg.get());
//            }
//        }
        if (autoDeleteDelay > 0)
            sendAutoDeletedMessage(builder.build(), autoDeleteDelay, channel);
        else
            sendMessage(builder.build(), channel);
    }

    private static void sendMessage(MessageEmbed embed, TextChannel channel) {
        if (channel == null) return;
        channel.sendMessage(embed).queue();
    }

    public static void sendMessage(String message, TextChannel channel) {
        sendMessage(MessageType.NEUTRAL, message, channel);
    }

    public static void sendMessage(String message, ReplyAction channel) {
        sendMessage(MessageType.NEUTRAL, message, channel);
    }

    public static void sendSuccessMessage(String message, TextChannel channel, User sender) {
        sendMessage(MessageType.SUCCESS, message, channel, sender);
    }

    public static void sendSuccessMessage(String message, ReplyAction channel, User sender) {
        sendMessage(MessageType.SUCCESS, message, channel, sender);
    }


    public static void sendSuccessMessage(String message, TextChannel channel) {
        sendMessage(MessageType.SUCCESS, message, channel);
    }

}

