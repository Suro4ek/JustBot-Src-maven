package ru.rien.bot.modules.reactinrole;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.ReactionService;

import java.util.HashMap;

@Component
public class ModuleReactionRole extends ModuleDiscord {

    public final ReactionService reactionService;

    public ModuleReactionRole(ReactionService reactionService) {
        super("reaction-role", false);
        this.reactionService = reactionService;
    }

    @Override
    protected void onEnable() {
        registerListenerThis();
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(!event.getMember().getUser().isBot()) {
            Guild guild = event.getGuild();
            Long message_id = event.getMessageIdLong();
            GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(guild.getId());
            if (reactionService.checkMessageRole(message_id, guildWrapper)) {
                HashMap<Long, Long> roles = reactionService.getReactionRoles(message_id, guildWrapper);
                Role role = guild.getRoleById(roles.get(event.getReactionEmote().getIdLong()));
                if (role != null) {
                    guild.addRoleToMember(event.getMember(), role).queue();
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemoveEvent(@NotNull GuildMessageReactionRemoveEvent event) {
        if(!event.getMember().getUser().isBot()) {
            Guild guild = event.getGuild();
            Long message_id = event.getMessageIdLong();
            GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(guild.getId());
            if (reactionService.checkMessageRole(message_id, guildWrapper)) {
                HashMap<Long, Long> roles = reactionService.getReactionRoles(message_id, guildWrapper);
                Role role = guild.getRoleById(roles.get(event.getReactionEmote().getIdLong()));
                if (role != null) {
                    guild.removeRoleFromMember(event.getMember(), role).queue();
                }
            }
        }
    }
}
