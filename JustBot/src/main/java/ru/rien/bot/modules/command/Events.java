package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;
import ru.rien.bot.utils.buttons.ButtonUtil;
import ru.rien.bot.utils.objects.ButtonGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events extends ListenerAdapter {

    private Map<Long, Double> maxButtonClicksPerSec = new HashMap<>();
    private Map<Long, List<Double>> buttonClicksPerSec = new HashMap<>();

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_READ)) return;
        if (event.getUser().isBot()) return;
        if (ButtonUtil.isButtonMessage(event.getMessageId())) {
            for (ButtonGroup.Button button : ButtonUtil.getButtonGroup(event.getMessageId()).getButtons()) {
                if ((event.getReactionEmote() != null && event.getReactionEmote().isEmote()
                        && event.getReactionEmote().getIdLong() == button.getEmoteId())
                        || (button.getUnicode() != null && event.getReactionEmote().getName().equals(button.getUnicode()))) {
                    try {
                        if(event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                            Message message =  event.getTextChannel().getHistory().getMessageById(event.getMessageId());
                            if(message != null) {
                                for (MessageReaction reaction : message.getReactions()) {
                                    if (reaction.getReactionEmote().equals(event.getReactionEmote())) {
                                        reaction.removeReaction(event.getUser()).queue();
                                    }
                                }
                            }
                        }
                    } catch (InsufficientPermissionException e) {

                    }
                    button.onClick(ButtonUtil.getButtonGroup(event.getMessageId()).getOwner(), event.getUser());
//                    String emote = event.getReactionEmote() != null ? event.getReactionEmote().getName() + "(" + event.getReactionEmote().getId() + ")" : button.getUnicode();
                    if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        return;
                    }
                    return;
                }
            }
        }
//        if (!JustBotManager.instance().getGuild(event.getGuild().getId()).getBetaAccess()) return;
        if (!event.getReactionEmote().getName().equals("\uD83D\uDCCC")) return; // Check if it's a :pushpin:
        Message message =  event.getTextChannel().getHistory().getMessageById(event.getMessageId());
        if(message != null) {
            MessageReaction reaction =
                    message.getReactions().stream().filter(r -> r.getReactionEmote().getName()
                            .equals(event.getReactionEmote().getName())).findFirst().orElse(null);
            if (reaction != null) {
                if (reaction.getCount() == 5) {
                    message.pin().queue((aVoid) -> event.getChannel().getHistory().retrievePast(1).complete().get(0)
                            .delete().queue());
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        super.onGuildMemberRemove(event);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        GuildWrapper guildWrapper = JustBotManager.instance().getGuild(event.getGuild().getId());

        guildWrapper.getPermissions().getGroups().forEach(group -> {
            System.out.println(group.getName());
            System.out.println(group.isDef());
        });
        guildWrapper.getPermissions().getGroups().stream().filter(Group::isDef)
                .forEach(group -> {
                    System.out.println(group.getName());
                    Role role = event.getGuild().getRoleById(group.getRoleId());
                    if(role != null)
                        event.getGuild().addRoleToMember(event.getMember(),
                                role).queue();
                });
    }

    public Map<Long, Double> getMaxButtonClicksPerSec() {
        return maxButtonClicksPerSec;
    }

    public Map<Long, List<Double>> getButtonClicksPerSec() {
        return buttonClicksPerSec;
    }
}
