package ru.rien.bot.modules.reactinrole;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
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
            if (guildWrapper.getReactionLoader().getReactionEntities().get(message_id) != null) {
                String emojiAsText = "";
                if(event.getReactionEmote().isEmote()) {
                    emojiAsText = event.getReactionEmote().getEmote().getAsMention();
                }

                if(event.getReactionEmote().isEmoji()) {
                    emojiAsText = EmojiParser.parseToAliases(event.getReactionEmote().getEmoji());
                }
                HashMap<String, Long> roles = guildWrapper.getReactionLoader().getReactionEntity(message_id);
                Role role = guild.getRoleById(roles.get(emojiAsText));
                if (role != null) {
                    guild.addRoleToMember(event.getMember(), role).queue();
                }
            }
        }
    }

    @Override
    public void onGuildMessageDeleteEvent(@NotNull GuildMessageDeleteEvent event) {
        Guild guild = event.getGuild();
        Long message_id = event.getMessageIdLong();
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(guild.getId());
        if(guildWrapper.getReactionLoader().getReactionEntities().get(message_id) != null){
            guildWrapper.getReactionLoader().getReactionEntities().remove(message_id);
            reactionService.delete(message_id,guildWrapper);
        }
    }

    @Override
    public void onGuildMessageReactionRemoveEvent(@NotNull GuildMessageReactionRemoveEvent event) {
        if(!event.getMember().getUser().isBot()) {
            Guild guild = event.getGuild();
            Long message_id = event.getMessageIdLong();
            GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(guild.getId());
            if (guildWrapper.getReactionLoader().getReactionEntities().get(message_id) != null) {
                String emojiAsText = "";
                if(event.getReactionEmote().isEmote()) {
                    emojiAsText = event.getReactionEmote().getEmote().getAsMention();
                }

                if(event.getReactionEmote().isEmoji()) {
                    emojiAsText = EmojiParser.parseToAliases(event.getReactionEmote().getEmoji());
                }
                HashMap<String, Long> roles = guildWrapper.getReactionLoader().getReactionEntity(message_id);
                Role role = guild.getRoleById(roles.get(emojiAsText));
                if (role != null) {
                    guild.removeRoleFromMember(event.getMember(), role).queue();
                }
            }
        }
    }
}
