package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PrivatkaInitCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("init", "setup privatka for your guild");
    }

    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String guildId = optionMappingList.get(0).getAsString();
        GuildWrapper guildWrapper = JustBotManager.instance().getGuildNoCache(guildId);
        if(guildWrapper != null){
            Guild guild = guildWrapper.getGuild();
            if(guild != null){
                Member member = guild.retrieveMember(user).complete();
                if(member != null) {
                    if (member.getPermissions().contains(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) {
                        if (guildWrapper.getGuildEntity().getCreate_channel_id() == 0 && guildWrapper.getGuildEntity().getCategory_id() == 0) {
                            guild.createCategory("Приватки").queue(category -> {
                                category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                                    guildWrapper.setCreeate_voice_channel(voiceChannel.getIdLong());
                                    guildWrapper.setCaterogy_id(category.getIdLong());
                                });
                            });
                        } else {
                            Category category1 = guild.getCategoryById(guildWrapper.getGuildEntity().getCategory_id());
                            if (category1 != null) {
                                category1.delete().queue();
                            }
                            guild.createCategory("Приватки").queue(category -> {
                                category.createVoiceChannel("Создание приваток").queue(voiceChannel -> {
                                    guildWrapper.setCreeate_voice_channel(voiceChannel.getIdLong());
                                    guildWrapper.setCaterogy_id(category.getIdLong());
                                });
                            });
                        }
                        event.getEvent().reply("Прикатки установлены").setEphemeral(true).queue();
                    } else {
                        MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                    }
                }else{
                    MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                }
            }else{
                MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
            }
        }else {
            MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "guild_id", "ваш id сервера", true)};
    }
}
