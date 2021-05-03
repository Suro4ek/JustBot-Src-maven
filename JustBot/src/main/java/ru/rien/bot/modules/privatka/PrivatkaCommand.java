package ru.rien.bot.modules.privatka;

import net.dv8tion.jda.api.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.services.PrivatkaService;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Component
public class PrivatkaCommand implements Command {

    public final PrivatkaService privatkaService;

    public PrivatkaCommand(PrivatkaService privatkaService) {
        this.privatkaService = privatkaService;
    }

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
            if(args[0].equalsIgnoreCase("init")) {
                if(event.getCommand().getPermissions(channel).hasPermission(event.getMember(),Permission.PRIVATKA_INIT)){
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
                }
            }else if(args[0].equalsIgnoreCase("lock")){
                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if(privatkaEntity != null){
                    VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                    if(voiceChannel != null){
                        voiceChannel.getManager().putPermissionOverride(guild.getGuild()
                                        .getPublicRole(), null,
                                EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT)).queue();
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("\uD83D\uDD12 Приватка заблокирована").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                }else{
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else if(args[0].equalsIgnoreCase("unlock")){
                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if(privatkaEntity != null){
                    VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                    if(voiceChannel != null){
                        voiceChannel.getManager().removePermissionOverride(guild.getGuild().getPublicRole()).queue();
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("\uD83D\uDD12 Приватка разблокирована").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                }else{
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else if(args[0].equalsIgnoreCase("text")){
                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if(privatkaEntity != null){
                    Category category = guild.getGuild().getCategoryById(guild.getGuildEntity().getCategory_id());
                    if(category != null) {
                        category.createTextChannel("Техтовый чат: " + sender.getName()).addPermissionOverride(event.getMember(), EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL), null)
                                .queue(textChannel -> {
                                    privatkaEntity.setTextid(textChannel.getIdLong());
                                    privatkaService.save(privatkaEntity);
                                });
                    }
                }else{
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("permit") || args[0].equalsIgnoreCase("allow")){
                String userString = args[1];
                User user = GuildUtils.getUser(userString, guild.getGuildId());
                if (user == null || guild.getGuild().getMember(user) == null) {
                    MessageUtils.sendErrorMessage("Пользователь не найден", channel);
                    return;
                }

                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if (privatkaEntity != null) {
                    VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                    if (voiceChannel != null) {
                        voiceChannel.getManager().putPermissionOverride(guild.getGuild().getMember(user), EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL,net.dv8tion.jda.api.Permission.VOICE_CONNECT),null)
                                .queue();
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Пользователию " + userString+ " разрешено заходить в приватку").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                } else {
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else if(args[0].equalsIgnoreCase("owner")){
                String userString = args[1];
                User user = GuildUtils.getUser(userString, guild.getGuildId());
                if (user == null || guild.getGuild().getMember(user) == null) {
                    MessageUtils.sendErrorMessage("Пользователь не найден", channel);
                    return;
                }

                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if(privatkaService.findbyOwnerId(user.getIdLong(),guild) != null){
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У пользователя уже есть приватка").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    return;
                }
                if (privatkaEntity != null) {
                    VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                    if (voiceChannel != null) {
                        voiceChannel.getManager().removePermissionOverride(event.getMember()).queue();
                        voiceChannel.getManager().putPermissionOverride(guild.getGuild().getMember(user),
                                EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL, net.dv8tion.jda.api.Permission.VOICE_CONNECT),null)
                                .queue();
                        privatkaService.newOwner(guild.getGuild().getMember(user).getIdLong(), privatkaEntity);
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Пользователию " + userString+ " теперь новый владелец").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                } else {
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else if(args[0].equalsIgnoreCase("reject") || args[0].equalsIgnoreCase("deny")){
                String userString = args[1];
                User user = GuildUtils.getUser(userString, guild.getGuildId());
                if (user == null || guild.getGuild().getMember(user) == null) {
                    MessageUtils.sendErrorMessage("Пользователь не найден", channel);
                    return;
                }
                PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                if (privatkaEntity != null) {
                    VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                    if (voiceChannel != null) {
                        voiceChannel.getManager().putPermissionOverride(guild.getGuild().getMember(user), null,EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL,net.dv8tion.jda.api.Permission.VOICE_CONNECT))
                                .queue();
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Пользователию " + userString+ " запрещено заходить в приватку").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                } else {
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else if(args[0].equalsIgnoreCase("limit")){
                try {
                    Integer limit = Integer.parseInt(args[1]);
                    if(limit < 0 || limit > 99){
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Вы ввели некоректное число").build(), TimeUnit.SECONDS.toMillis(5), channel);
                        return;
                    }
                    PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
                    if (privatkaEntity != null) {
                        VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                        if (voiceChannel != null) {
                            voiceChannel.getManager().setUserLimit(limit).queue();
                            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Новый лимит " + limit).build(), TimeUnit.SECONDS.toMillis(5), channel);
                        }
                    } else {
                        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);
                    }
                }catch (NumberFormatException e){
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Вы ввели не число").build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            }else {
                change_name(event, args, channel, sender, guild);
            }
        }else{
            change_name(event, args, channel, sender, guild);
        }
    }

    private void change_name(CommandEvent event, String[] args, TextChannel channel, User sender, GuildWrapper guild) {
        if(args[0].equalsIgnoreCase("name")){
            PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
            if (privatkaEntity != null) {
                VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
                if (voiceChannel != null) {
                    voiceChannel.getManager().setName(event.getArguments(1)).queue();
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("Теперь приватка называется: " + event.getArguments(1)).build(), TimeUnit.SECONDS.toMillis(5), channel);
                }
            } else {
                MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), TimeUnit.SECONDS.toMillis(5), channel);

            }
        }
    }


    @Override
    public String getCommand() {
        return "privatka";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Команды приваток";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}privatka - список команд\n" +
                "{%}privatka permit/allow [пользователь] - разрешить заходить в приватку\n" +
                "{%}privatka deny/reject [пользователь] - запретить заходить в приватку\n" +
                "{%}privatka name [название...] - поменять название каналу\n" +
                "{%}privatka lock - заблокировать вход в приватку\n" +
                "{%}privatka unlock - разблокировать вход в приватку\n" +
                "{%}privatka owner [пользователь] - новый владелец\n" +
                "{%}privatka limit [число] - лимит по пользователям";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.PRIVATKA_COMMAND;
    }
}
