package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.music.VideoThread;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

@Component
public class PlayCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        if (args.length > 0) {
            if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel()) {
                if (channel.getGuild().getAudioManager().isAttemptingToConnect()) {
                    MessageUtils.sendErrorMessage(guild.getMessage("PLAY_CONNECTING"), channel);
                    return;
                }
                if (channel.getGuild().getSelfMember().getVoiceState().inVoiceChannel() &&
                        !(channel.getGuild().getSelfMember().getVoiceState().getChannel().getId()
                                .equals(member.getVoiceState().getChannel().getId()))) {
                    MessageUtils.sendErrorMessage(guild.getMessage("PLAY_ALREADY_CHANNEL"), channel);
                    return;
                }
                GuildUtils.joinChannel(channel, member);
            }
            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                VideoThread.getThread(args[0], channel, sender).start();
            } else {
                String term = MessageUtils.getMessage(args, 0);
                VideoThread.getSearchThread(term, channel, sender).start();
            }
        } else
            MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("PLAY_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("PLAY_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.PLAY_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}