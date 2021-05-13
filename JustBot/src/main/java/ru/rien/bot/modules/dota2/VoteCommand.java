package ru.rien.bot.modules.dota2;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.SteamEntity;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.services.SteamService;
import ru.rien.bot.utils.MessageUtils;

@Component
public class VoteCommand implements Command {

    @Autowired
    public SteamService steamService;

    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        String[] args = event.getArgs();
        if(steamService.checkValid(user.getIdLong(),guild.getGuildEntity())){
            steamService.createSteam(user.getIdLong(), guild);
        }
        if(args[0].equalsIgnoreCase("start")){
            SteamEntity steamEntity = steamService.findDiscordAndGuild(guild, user.getIdLong());
            if(steamEntity.getSteamid() == 0){
                MessageUtils.sendErrorMessage("У вас не привязан стим", channel);
                return;
            }
            Vote vote = new Vote(guild.getGuildIdLong(), channel.getIdLong());
            ModuleDota2.matchscan.put(vote,steamEntity.getSteamid());
            guild.setVote(vote);
            MessageUtils.sendInfoMessage("Запущено голосование на победу:\n" +
                    "!vote win - на победу\n" +
                    "!vote lose - на порожение\n",channel,user);
        }else if(args[0].equalsIgnoreCase("win")){
            if(guild.getVote() == null){
                MessageUtils.sendErrorMessage("Голосование не началось", channel);
                return;
            }
            Vote vote = guild.getVote();
            if(vote.getWins().contains(user.getIdLong()) || vote.getLoses().contains(user.getIdLong())){
                MessageUtils.sendErrorMessage("Вы уже проголосовали", channel);
                return;
            }
            vote.wins.add(user.getIdLong());
            MessageUtils.sendInfoMessage("Проголосовал за вин",channel,user);
        }else if(args[0].equalsIgnoreCase("lose")){
            if(guild.getVote() == null){
                MessageUtils.sendErrorMessage("Голосование не началось", channel);
                return;
            }
            Vote vote = guild.getVote();
            if(vote.getWins().contains(user.getIdLong()) || vote.getLoses().contains(user.getIdLong())){
                MessageUtils.sendErrorMessage("Вы уже проголосовали", channel);
                return;
            }
            vote.loses.add(user.getIdLong());
            MessageUtils.sendInfoMessage("Проголосовал за lose",channel,user);
        }
    }

    @Override
    public String getCommand() {
        return "vote";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.VOTEG_COMAMAND;
    }
}
