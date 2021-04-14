package ru.rien.bot.module;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.ModuleCommand;
import ru.rien.bot.modules.dsBot.ModuleDsBot;

import javax.annotation.Nonnull;

public abstract class ModuleDiscord extends CommonModule implements EventListener{

    @Autowired
    private ModuleDsBot moduleDsBot;


    public ModuleDiscord(String name,boolean config) {
        super(name, config);
    }

    public ModuleDsBot getModuleDsBot() {
        return moduleDsBot;
    }

    public void registerListenerThis(){
        moduleDsBot.getJda().addEventListener(this);
    }

    public void registerListener(EventListener eventListener){
        moduleDsBot.getJda().addEventListener(eventListener);
    }

    public void registerCommand(Command command){
        ModuleCommand.getCommands().add(command);
    }

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    }

    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    }
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
    }
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
    }
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
    }

    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
    }

    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
    }

    public
    void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
    }

    public void onVoiceChannelDelete(@Nonnull VoiceChannelDeleteEvent event) {
    }

    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
    }

    public void onRoleUpdateColor(@Nonnull RoleUpdateColorEvent event) {
    }

    public void onRoleUpdateHoisted(@Nonnull RoleUpdateHoistedEvent event) {
    }

    public void onRoleUpdateMentionable(@Nonnull RoleUpdateMentionableEvent event) {
    }

    public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {
    }

    public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {
    }

    public void onRoleUpdatePosition(@Nonnull RoleUpdatePositionEvent event) {
    }

    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
    }

    public void onGuildLeaveEvent(@NotNull GuildLeaveEvent event){

    }

    public void onGuildJoinEvent(@NotNull GuildJoinEvent event){

    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent) {
            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
        }else if(event instanceof MessageReceivedEvent){
            this.onMessageReceived((MessageReceivedEvent) event);
        }else if(event instanceof MessageDeleteEvent){
            this.onMessageDelete((MessageDeleteEvent) event);
        }else if(event instanceof GuildVoiceJoinEvent){
            this.onGuildVoiceJoin((GuildVoiceJoinEvent)event);
        }else if(event instanceof GuildVoiceMoveEvent){
            this.onGuildVoiceMove((GuildVoiceMoveEvent)event);
        }else if(event instanceof GuildVoiceLeaveEvent){
            this.onGuildVoiceLeave((GuildVoiceLeaveEvent)event);
        }else if(event instanceof GuildVoiceUpdateEvent){
            this.onGuildVoiceUpdate((GuildVoiceUpdateEvent)event);
        }else if(event instanceof VoiceChannelDeleteEvent){
            this.onVoiceChannelDelete((VoiceChannelDeleteEvent)event);
        }else if(event instanceof RoleDeleteEvent){
            this.onRoleDelete((RoleDeleteEvent) event);
        }else if(event instanceof RoleUpdateColorEvent){
            this.onRoleUpdateColor((RoleUpdateColorEvent) event);
        }else if(event instanceof RoleUpdateHoistedEvent){
            this.onRoleUpdateHoisted((RoleUpdateHoistedEvent)event);
        }else if(event instanceof RoleUpdateMentionableEvent){
            this.onRoleUpdateMentionable((RoleUpdateMentionableEvent) event);
        }else if(event instanceof RoleUpdatePermissionsEvent){
            this.onRoleUpdatePermissions((RoleUpdatePermissionsEvent) event);
        }else if(event instanceof RoleUpdateNameEvent){
            this.onRoleUpdateName((RoleUpdateNameEvent) event);
        }else if(event instanceof RoleUpdatePositionEvent){
            this.onRoleUpdatePosition((RoleUpdatePositionEvent) event);
        }else if(event instanceof GuildMessageReactionAddEvent){
            this.onGuildMessageReactionAdd((GuildMessageReactionAddEvent)event);
        }else if(event instanceof UserUpdateOnlineStatusEvent){
            this.onUserUpdateOnlineStatus((UserUpdateOnlineStatusEvent)event);
        }else if(event instanceof GuildLeaveEvent){
            this.onGuildLeaveEvent((GuildLeaveEvent) event);
        }else if(event instanceof GuildJoinEvent){
            this.onGuildJoinEvent((GuildJoinEvent) event);
        }
    }
}