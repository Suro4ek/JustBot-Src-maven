package ru.rien.bot.modules.privatka;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.PrivatkaService;

import java.util.List;

@Component
public class ModulePrivatka extends ModuleDiscord {

    private final PrivatkaService privatkaService;
    public ModulePrivatka(PrivatkaService privatkaService) {
        super("privatka", false);
        this.privatkaService = privatkaService;
    }

    @Override
    protected void onEnable() {
        clear();
        registerListenerThis();
        registerCommand(new PrivatkaCommand());
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if(guildWrapper.getGuildEntity().getCategory_id() != 0 && guildWrapper.getGuildEntity().getCreate_channel_id() != 0){
            GuildChannel guildChannel = guildWrapper.getGuild().getGuildChannelById(guildWrapper.getGuildEntity().getCreate_channel_id());
            if(guildChannel != null){
                if(guildChannel.getIdLong() == event.getChannelJoined().getIdLong()) {
                    Category category = event.getGuild().getCategoryById(guildWrapper.getGuildEntity().getCategory_id());
                    if (category != null) {
                        Member member = event.getMember();
                        createprivatka(category, member, event, guildWrapper);
                    }
                }
            }
        }
    }

    public void createprivatka(Category category, Member member,@NotNull GenericGuildEvent event, GuildWrapper guildWrapper){
        category.createVoiceChannel("Приватка: " + member.getEffectiveName()).queue(voiceChannel -> {

            privatkaService.createPrivatka(voiceChannel.getIdLong(), guildWrapper);
            try {
                event.getGuild().moveVoiceMember(member, voiceChannel).queue();
            }catch (IllegalStateException e){
                PrivatkaEntity privatkaEntity = privatkaService.findbyChannelId(voiceChannel.getIdLong(), guildWrapper);
                privatkaService.delete(privatkaEntity);
            }
        });
    }

    public void deleteprivatka(@NotNull  GenericGuildVoiceUpdateEvent event, GuildWrapper guildWrapper){
        if(event.getChannelLeft() != null && event.getChannelLeft().getMembers().size() == 0){
            PrivatkaEntity privatkaEntity = privatkaService.findbyChannelId(event.getChannelLeft().getIdLong(), guildWrapper);
            if(privatkaEntity != null){
                VoiceChannel voiceChannel = event.getChannelLeft();
                voiceChannel.delete().queue();
                privatkaService.delete(privatkaEntity);
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if(guildWrapper.getGuildEntity().getCategory_id() != 0 && guildWrapper.getGuildEntity().getCreate_channel_id() != 0){
            Category category = event.getGuild().getCategoryById(guildWrapper.getGuildEntity().getCategory_id());
            if (category != null) {
                deleteprivatka(event, guildWrapper);
            }
        }
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if(guildWrapper.getGuildEntity().getCategory_id() != 0 && guildWrapper.getGuildEntity().getCreate_channel_id() != 0){
            Category category = event.getGuild().getCategoryById(guildWrapper.getGuildEntity().getCategory_id());
            if (category != null) {
                deleteprivatka(event, guildWrapper);
                GuildChannel guildChannel = guildWrapper.getGuild().getGuildChannelById(guildWrapper.getGuildEntity().getCreate_channel_id());
                if(guildChannel != null) {
                    if (guildChannel.getIdLong() == event.getChannelJoined().getIdLong()) {
                        Member member = event.getMember();
                        createprivatka(category,member,event,guildWrapper);
                    }
                }
            }
        }
    }

    public void clear(){
        List<PrivatkaEntity> privatkaEntities = privatkaService.findAll();
        privatkaEntities.forEach(privatkaEntity -> {
            Guild guild = getModuleDsBot().getJda().getGuildById(privatkaEntity.getGuildEntity().getGuildid());
            if(guild != null){
                GuildChannel guildChannel = guild.getGuildChannelById(privatkaEntity.getVchannelid());
                if(guildChannel != null && guildChannel.getMembers().size() == 0){
                    guildChannel.delete().queue();
                }
            }
            privatkaService.delete(privatkaEntity);
        });
    }
}
