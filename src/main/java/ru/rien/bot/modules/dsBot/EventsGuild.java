package ru.rien.bot.modules.dsBot;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;

@Component
public class EventsGuild extends ListenerAdapter {


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        GuildWrapper guildWrapper = JustBotManager.instance().getGuild(event.getGuild().getId());
        guildWrapper.getPermissions().getGroups().stream().filter(Group::isDef)
                .forEach(group -> {
                    Role role = event.getGuild().getRoleById(group.getRoleId());
                    if(role != null)
                        event.getGuild().addRoleToMember(event.getMember(),
                               role).queue();
                });
    }
}
