package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.ModuleCommand;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.music.extractors.YouTubeExtractor;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.FormatUtils;
import ru.rien.bot.utils.GeneralUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.buttons.ButtonUtil;
import ru.rien.bot.utils.objects.ButtonGroup;

@Component
public class SongCommand implements Command {

    @Autowired
    private static ModuleDsBot moduleDsBot = ModuleDsBot.getInstance();
    @Autowired
    private ModuleCommand moduleCommand;

    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        GuildWrapper guild = event.getGuild();
        PlayerManager manager = moduleDsBot.getMusicManager();
        if (manager.getPlayer(channel.getGuild().getId()).getPlayingTrack() != null) {
            Track track = manager.getPlayer(channel.getGuild().getId()).getPlayingTrack();
            EmbedBuilder eb = MessageUtils.getEmbed(sender)
                    .addField(guild.getMessage("SONG_NOW_PLAYING"), getLink(track), false)
                    .setThumbnail("https://img.youtube.com/vi/" + track.getTrack().getIdentifier() + "/hqdefault.jpg");
            if (track.getTrack().getInfo().isStream)
                eb.addField(guild.getMessage("SONG_DURATION"), guild.getMessage("SONG_LIVE"), false);
            else
                eb.addField(guild.getMessage("SONG_DURATION"), GeneralUtils.getProgressBar(track), true)
                        .addField(guild.getMessage("SONG_TIME"), String.format("%s / %s", FormatUtils.formatDuration(track.getTrack().getPosition()),
                                FormatUtils.formatDuration(track.getTrack().getDuration())), false);
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), ButtonGroupConstants.SONG);
            buttonGroup.addButton(new ButtonGroup.Button("\u23EF", (owner, user, message1) -> {
                if (manager.hasPlayer(guild.getGuildId())) {
                    if (manager.getPlayer(guild.getGuild().getId()).getPaused()) {
                        if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.RESUME_COMMAND)) {
                            manager.getPlayer(guild.getGuild().getId()).play();
                        }
                    } else {
                        if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.PAUSE_COMMAND)) {
                            manager.getPlayer(guild.getGuild().getId()).setPaused(true);
                        }
                    }
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23F9", (owner, user, message1) -> {
                if (manager.hasPlayer(guild.getGuildId()) &&
                        getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.STOP_COMMAND)) {
                    manager.getPlayer(guild.getGuildId()).stop();
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23ED", (owner, user, message1) -> {
                if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.SKIP_COMMAND)) {
                    Command cmd = moduleCommand.getCommand("skip", user);
                    CommandEvent commandEvent = new CommandEvent(cmd,user, guild, channel, message1, new String[0], guild.getGuild().getMember(user));
                    if (cmd != null)
                        cmd.execute(commandEvent);
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\uD83D\uDD01", (ownerID, user, message1) -> {
                updateSongMessage(user, guild, message1, message1.getTextChannel());
            }));
            ButtonUtil.sendButtonedMessage(channel, eb.build(), buttonGroup);
        } else {
            channel.sendMessage(MessageUtils.getEmbed(sender)
                    .addField(guild.getMessage("SONG_NOW_PLAYING"), guild.getMessage("SONG_NOTHING"), false)
                    .build()).queue();
        }
    }

    public static String getLink(Track track) {
        String name = String.valueOf(track.getTrack().getInfo().title).replace("`", "'");
        String link = YouTubeExtractor.WATCH_URL + track.getTrack().getIdentifier();
        return String.format("[`%s`](%s)", name, link);
    }

    @Override
    public String getCommand() {
        return "song";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("SONG_DESCRIPTION");
    }

    @Override
    public String[] getAliases() {
        return new String[]{"playing"};
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("SONG_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.SONG_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    public static void updateSongMessage(User sender, GuildWrapper guild, Message message, TextChannel channel) {
        Track track = moduleDsBot.getMusicManager().getPlayer(channel.getGuild().getId()).getPlayingTrack();
        if (track == null)
            return;
        EmbedBuilder eb = MessageUtils.getEmbed(sender)
                .addField(guild.getMessage("SONG_NOW_PLAYING"), getLink(track), false)
                .setThumbnail("https://img.youtube.com/vi/" + track.getTrack().getIdentifier() + "/hqdefault.jpg");
        if (track.getTrack().getInfo().isStream)
            eb.addField(guild.getMessage("SONG_DURATION"), guild.getMessage("SONG_LIVE"), false);
        else
            eb.addField(guild.getMessage("SONG_DURATION"), GeneralUtils.getProgressBar(track), true)
                    .addField(guild.getMessage("SONG_TIME"), String.format("%s / %s", FormatUtils.formatDuration(track.getTrack().getPosition()),
                            FormatUtils.formatDuration(track.getTrack().getDuration())), false);
        message.editMessage(eb.build()).queue();
    }

}