package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;

public class PrivatkaInitCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("init", "setup privatka for your guild");
    }

    @Override
    public void execute(CommandEvent event) {
        TextChannel textChannel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        GuildEntity guildEntity = guild.getGuildEntity();
        if(event.getCommand().getPermissions(textChannel).hasPermission(event.getMember(), Permission.PRIVATKA_INIT)){
            if (guildEntity.getCreate_channel_id() == 0 && guildEntity.getCategory_id() == 0) {
                guild.getGuild().createCategory("Приватки").queue(category -> {
                    category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                        guild.setCreeate_voice_channel(voiceChannel.getIdLong());
                        guild.setCaterogy_id(category.getIdLong());
                    });
                });
            } else {
                Category category1 = guild.getGuild().getCategoryById(guildEntity.getCategory_id());
                if (category1 != null) {
                    category1.delete().queue();
                }
                guild.getGuild().createCategory("Приватки").queue(category -> {
                    category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                        guild.setCreeate_voice_channel(voiceChannel.getIdLong());
                        guild.setCaterogy_id(category.getIdLong());
                    });
                });
            }
            event.getEvent().reply("Прикатки установлены").setEphemeral(true).queue();
        }
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }
}
