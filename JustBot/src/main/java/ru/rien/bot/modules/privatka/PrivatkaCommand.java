package ru.rien.bot.modules.privatka;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class PrivatkaCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        GuildWrapper guild = event.getGuild();
        GuildEntity guildEntity = guild.getGuildEntity();
        if(args.length == 0){
            MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
        }else if(args.length == 1){
            if(args[0].equalsIgnoreCase("init")){
                if(guildEntity.getCreate_channel_id() == 0 && guildEntity.getCategory_id() == 0){
                    guild.getGuild().createCategory("Приватки").queue(category -> {
                        category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                            guild.setCreeate_voice_channel(voiceChannel.getIdLong());
                            guild.setCaterogy_id(category.getIdLong());
                        });
                    });
                }else{
                    Category category1 = guild.getGuild().getCategoryById(guildEntity.getCategory_id());
                    if(category1 != null){
                        category1.delete().queue();
                    }
                    guild.getGuild().createCategory("Приватки").queue(category -> {
                        category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                            guild.setCreeate_voice_channel(voiceChannel.getIdLong());
                            guild.setCaterogy_id(category.getIdLong());
                        });
                    });
                }
            }
        }
    }

    @Override
    public String getCommand() {
        return "privatka";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}privatka - список команд";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }
}
