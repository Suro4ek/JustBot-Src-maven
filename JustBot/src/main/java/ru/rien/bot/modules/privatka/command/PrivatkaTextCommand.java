package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.PrivatkaService;
import ru.rien.bot.utils.MessageUtils;

import java.util.EnumSet;

public class PrivatkaTextCommand implements SubCommand {

    private final PrivatkaService privatkaService;

    public PrivatkaTextCommand(PrivatkaService privatkaService){
        this.privatkaService = privatkaService;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("text", "create text channel for mine");
    }

    @Override
    public void execute(CommandEvent event) {
        User sender = event.getSender();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        GuildWrapper guild = event.getGuild();
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
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), replyAction);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }


    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }
}
