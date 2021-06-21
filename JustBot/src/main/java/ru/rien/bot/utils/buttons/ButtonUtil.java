package ru.rien.bot.utils.buttons;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import ru.rien.bot.utils.objects.MyButton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonUtil {

    private static Map<String, List<MyButton>> buttons = new ConcurrentHashMap<>();

    /**
     * Sends an embed button message with a set of buttons.
     *
     * @param channel The TextChannel to send it to.
     * @param embed   The embed to send.
     * @param buttons The buttons to display.
     */
//    public static void sendButtonedMessage(InteractionHook channel, MessageEmbed embed, List<MyButton> buttons) {
//        channel.sendMessage(embed).queue(message -> handleSuccessConsumer(channel, message.toString(), buttons));
//    }

    /**
     * Sends an embed button message with a set of buttons.
     *
     * @param channel The TextChannel to send it to.
     * @param embed   The embed to send.
     * @param buttons The buttons to display.
     */
    public static void sendButtonedMessage(InteractionHook channel, MessageEmbed embed, List<MyButton> buttons) {
        handleSuccessConsumer(channel, embed, buttons);
    }


    /**
     * Sends an embed button message with a set of buttons, and returns the message.
     *
     * @return The message we sent to discord.
     *
     * @param channel The {@link TextChannel} to send it to.
     * @param embed   The {@link MessageEmbed} to send.
     */
    public static Message sendReturnedButtonedMessage(TextChannel channel, MessageEmbed embed, List<MyButton> buttons2) {
        Button[] buttons1 = (Button[])buttons2.stream().map(MyButton::getButton).toArray();
        Message message = channel.sendMessageEmbeds(embed)
                .setActionRow(buttons1)
                .complete();
        buttons.put(message.getId(), buttons2);
        return message;
    }

    /**
     * Sends a string message with a set of buttons.
     *
     * @param channel The {@link TextChannel} to send it to.
     * @param text    The message to send.
     * @param buttons The buttons to display.
     */
    public static void sendButtonedMessage(Guild guild, InteractionHook channel, String text, List<MyButton> buttons) {
        handleSuccessConsumer(guild,channel, text, buttons);
    }


    private static void handleSuccessConsumer(InteractionHook channel, String  message, List<MyButton> buttonGroup) {
        Button[] buttons1 = (Button[])buttonGroup.stream().map(MyButton::getButton).toArray();
        channel.sendMessage(message)
                .addActionRow(buttons1)
                .queue(message1 -> {
                    buttons.put(message1.getId(), buttonGroup);
                });
    }

    private static void handleSuccessConsumer(InteractionHook channel, MessageEmbed  message, List<MyButton> buttonGroup) {

        Button[] buttons1 = (Button[])buttonGroup.stream().map(MyButton::getButton).toArray(Button[]::new);
        channel.sendMessageEmbeds(message)
                .addActionRow(buttons1)
                .queue(message1 ->{
                    buttons.put(message1.getId(), buttonGroup);
                });
    }

    private static void handleSuccessConsumer(Guild guild,InteractionHook channel, String text, List<MyButton> buttonGroup) {
//        if (!channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_ADD_REACTION)) {
//            MessageUtils.sendErrorMessage("We don't have permission to add reactions to messages so buttons have been " +
//                    "disabled", channel);
//            return;
//        }
//        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
//            MessageUtils.sendErrorMessage("We don't have permission to manage reactions so you won't be getting the best experience with buttons", channel);
//        }
        Button[] buttons1 = (Button[])buttonGroup.stream().map(MyButton::getButton).toArray();
        channel.sendMessage(text)
                .addActionRow(buttons1)
//                .addActionRow(buttonGroup.getButtons())
                .queue(message -> {
                    buttons.put(message.getId(), buttonGroup);
        });
    }

    /**
     * Gets all the button Messages along with their buttons.
     *
     * @return A map containing what message id corresponds to what buttons.
     */
    public static Map<String, List<MyButton>> getButtons() {
        return buttons;
    }

    /**
     * Gets weather or not the message has buttons.
     *
     * @param id The Message id.
     * @return If the message has buttons.
     */
    public static boolean isButton(String id) {
        return buttons.containsKey(id);
    }

}