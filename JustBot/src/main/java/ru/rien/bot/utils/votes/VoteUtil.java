package ru.rien.bot.utils.votes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.schedule.JustBotTask;
import ru.rien.bot.schedule.Scheduler;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonUtil;
import ru.rien.bot.utils.objects.MyButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoteUtil {

    private static Map<String, VoteGroup> groupMap = new ConcurrentHashMap<>();
    private static Map<String, VoteGroup.VoteRunnable> runnableMap = new ConcurrentHashMap<>();

    public static void sendVoteMessage(UUID id, VoteGroup.VoteRunnable voteRunnable, VoteGroup group, long timeout, TextChannel channel, User user, String buttonGroupString, List<MyButton> myButtons) {
        EmbedBuilder votesEmbed = new EmbedBuilder()
                .setDescription("Голосов за " + group.getMessageDesc())
                .addField("Да", "0", true)
                .addField("Нет", "0", true);
        String messageDesc = group.getMessageDesc();
        votesEmbed.setColor(Color.CYAN);
        group.setVotesEmbed(votesEmbed);
        List<MyButton> buttonGroup = new ArrayList<>();

        groupMap.put(id + channel.getGuild().getId(), group);
        runnableMap.put(id + channel.getGuild().getId(), voteRunnable);

        buttonGroup.add(new MyButton(Button.primary(user.getId()+"yes_skip", "Да"), (event) -> {
            if (group.addVote(VoteGroup.Vote.YES, event.getUser())) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ты проголосовал за " + messageDesc).build()).setEphemeral(true).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ты не можешь голосовать " + messageDesc).build()).setEphemeral(true).queue();
            }
        }));

        buttonGroup.add(new MyButton(Button.primary(user.getId()+"no_skip", "Нет"), (event) -> {
            if (group.addVote(VoteGroup.Vote.NO, user)) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ты проголосовал за " + messageDesc).build()).setEphemeral(true).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ты не можешь голосовать " + messageDesc).build()).setEphemeral(true).queue();
            }
        }));

        myButtons.addAll(buttonGroup);

        Message voteMessage = ButtonUtil.sendReturnedButtonedMessage(channel, votesEmbed.build(), buttonGroup);
        group.setVoteMessage(voteMessage);

        new JustBotTask("Votes-" + voteMessage.getId()){

            @Override
            public void run() {
                voteRunnable.run(group.won());
                groupMap.remove(group.getMessageDesc() + channel.getGuild().getId());
                runnableMap.remove(group.getMessageDesc() + channel.getGuild().getId());
                channel.deleteMessageById(voteMessage.getId()).queue();
            }

        }.delay(timeout);
    }

    public static VoteGroup getVoteGroup(UUID uuid, Guild guild) {
        return groupMap.get(uuid + guild.getId());
    }

    public static boolean contains(UUID uuid, Guild guild) {
        return groupMap.containsKey(uuid + guild.getId());
    }

    public static void remove(UUID uuid, Guild guild) {
        VoteGroup group = groupMap.get(uuid + guild.getId());
        if (group == null) return;
        long message = group.getMessageId();
        groupMap.remove(uuid + guild.getId());
        Scheduler.cancelTask("Vote-" + message);
        ButtonUtil.getButtons().remove(String.valueOf(message));
        group.getVoteMessage().getChannel().deleteMessageById(group.getMessageId()).queue();
    }

    public static void finishNow(UUID uuid, Guild guild) {
        VoteGroup group = groupMap.get(uuid + guild.getId());
        String message = group.getVoteMessage().getId();
        groupMap.remove(uuid + guild.getId());
        runnableMap.get(uuid + guild.getId()).run(group.won());
        runnableMap.remove(uuid + guild.getId());
        ButtonUtil.getButtons().remove(String.valueOf(message));
        Scheduler.cancelTask("Votes-" + message);
        group.getVoteMessage().getChannel().deleteMessageById(group.getVoteMessage().getId()).queue();
    }
}