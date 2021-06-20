package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;
import ru.rien.bot.utils.buttons.ButtonUtil;
import ru.rien.bot.utils.objects.MyButton;

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
        });
        guildWrapper.getPermissions().getGroups().stream().filter(Group::isDef)
                .forEach(group -> {
                    Role role = event.getGuild().getRoleById(group.getRoleId());
                    if(role != null)
                        event.getGuild().addRoleToMember(event.getMember(),
                                role).queue();
                });
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if(ButtonUtil.isButton(event.getMessageId())) {
           List<MyButton> myButton = ButtonUtil.getButtons().get(event.getMessageId());
           for(MyButton button : myButton){
               if(button.getButton().getId().equals(event.getComponentId())) {
                   if (button.getUser() != null) {
                       if (!button.getUser().getId().equals(event.getUser().getId())) {
                           return;
                       }
                   }
                   button.onClick(event);
               }
            }
        }
    }

    public Map<Long, Double> getMaxButtonClicksPerSec() {
        return maxButtonClicksPerSec;
    }

    public Map<Long, List<Double>> getButtonClicksPerSec() {
        return buttonClicksPerSec;
    }
}
