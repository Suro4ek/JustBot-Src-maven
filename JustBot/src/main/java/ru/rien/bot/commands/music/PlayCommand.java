package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.music.VideoThread;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

@Component
public class PlayCommand implements Command {


    @Override
    public void execute(CommandEvent event) {
        Member member = event.getMember();
        GuildWrapper guild = event.getGuild();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        User sender = event.getSender();
        event.getEvent().deferReply(false).queue();
        InteractionHook interactionHook = event.getEvent().getHook();
        interactionHook.setEphemeral(false);
        String args = event.getOptionMappings().get(0).getAsString().split(" ")[0];
        if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel()) {
                if (guild.getGuild().getAudioManager().isAttemptingToConnect()) {
                    MessageUtils.sendErrorMessage(guild.getMessage("PLAY_CONNECTING"), replyAction);
                    return;
                }
                if (guild.getGuild().getSelfMember().getVoiceState().inVoiceChannel() &&
                        !(guild.getGuild().getSelfMember().getVoiceState().getChannel().getId()
                                .equals(member.getVoiceState().getChannel().getId()))) {
                    MessageUtils.sendErrorMessage(guild.getMessage("PLAY_ALREADY_CHANNEL"), replyAction);
                    return;
                }
                GuildUtils.joinChannel(guild.getGuild(), replyAction, member);
        }
        if (args.startsWith("http") || args.startsWith("www.")) {
            VideoThread.getThread(args, interactionHook, guild, sender).start();
        } else {
            args = event.getOptionMappings().get(0).getAsString();
            VideoThread.getSearchThread(args, interactionHook, guild, sender).start();
        }
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String getDescription(Language guild) {
        return guild.getMessage("PLAY_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return guild.getMessage("PLAY_USAGE");
//    }

    @Override
    public Permission getPermission() {
        return Permission.PLAY_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "args", "music name or url", true)};
    }
}