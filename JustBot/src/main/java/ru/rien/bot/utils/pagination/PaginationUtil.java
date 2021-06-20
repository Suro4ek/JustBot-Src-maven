package ru.rien.bot.utils.pagination;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.commons.lang3.ArrayUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonUtil;
import ru.rien.bot.utils.objects.MyButton;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtil {

    /**
     * Splits a string into a {@link PaginationList}
     *
     * @param content     The string to split
     * @param splitMethod the method by witch to split
     * @param splitAmount The amount to split
     * @return {@link PaginationList}
     */
    public static PaginationList<String> splitStringToList(String content, SplitMethod splitMethod, int splitAmount) {
        List<String> pages = new ArrayList<>();
        if (splitMethod == SplitMethod.CHAR_COUNT) {
            int pagesCount = Math.max((int) Math.ceil((double) content.length() / splitAmount), 1);
            String workingString = content;
            for (int i = 0; i < pagesCount; i++) {
                String substring = workingString.substring(0, Math.min(splitAmount, workingString.length()));
                int splitIndex = substring.lastIndexOf("\n") == -1 ? substring.length() : substring.lastIndexOf("\n");
                pages.add(substring.substring(0, splitIndex));
                if (i != (pagesCount - 1)) {
                    workingString = workingString.substring(splitIndex + 1, workingString.length());
                }
            }

        } else if (splitMethod == SplitMethod.NEW_LINES) {
            String[] lines = content.split("\n");
            int pagesCount = Math.max((int) Math.ceil((double) lines.length / splitAmount), 1);
            for (int i = 0; i < pagesCount; i++) {
                String[] page = ArrayUtils.subarray(lines, splitAmount * i, (splitAmount * i) + splitAmount);
                StringBuilder sb = new StringBuilder();
                for (String line : page) {
                    sb.append(line).append("\n");
                }
                pages.add(sb.toString());
            }
        }
        return new PaginationList<>(pages);
    }

    /**
     * Sends a paged message
     *
     * @param textChannel The channel to send it to
     * @param list        The {@link PaginationList} to use
     * @param page        The starting page
     * @param sender      The member who requested the button
     */
    public static void sendPagedMessage(TextChannel textChannel, PaginationList list, int page, User sender, String group) {
        if (page < 0 || page > list.getPages() - 1) {
            MessageUtils.sendErrorMessage("Invalid page: " + (page + 1) + " Total Pages: " + list.getPages(), textChannel);
            return;
        }
        Integer[] pages = new Integer[]{page};
        if (list.getPages() > 1) {
            List<MyButton> buttonGroup = new ArrayList<>();
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"start", "запустить"), (event) -> {
                //Start
                pages[0] = 0;
                if(event.getMessage() != null)
                     event.getMessage().editMessage(list.getPage(pages[0])).queue();
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"prev", "предыдущая"), (event) -> {
                //Prev
                if (pages[0] != 0) {
                    pages[0] -= 1;
                    if(event.getMessage() != null)
                        event.getMessage().editMessage(list.getPage(pages[0])).queue();
                }
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"next", "следующая"), (event) -> {
                //Next
                if (pages[0] + 1 != list.getPages()) {
                    pages[0] += 1;
                    if(event.getMessage() != null)
                        event.getMessage().editMessage(list.getPage(pages[0])).queue();
                }
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"last", "последная"), (event) -> {
                //Last
                pages[0] = list.getPages() - 1;
                if(event.getMessage() != null)
                    event.getMessage().editMessage(list.getPage(pages[0])).queue();
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"delete", "остановить"),sender,  (event) -> {
                // Delete
                    if(event.getMessage() != null)
                        event.getMessage().delete().queue();
            }));
//            ButtonUtil.sendButtonedMessage(textChannel, list.getPage(page), buttonGroup);
        } else {
            textChannel.sendMessage(list.getPage(page)).queue();
        }
    }

    /**
     * Sends an embed paged message the to specified channel.
     * You can build with Embed with {@link PagedEmbedBuilder}.
     *
     * @param pagedEmbed The {@link PagedEmbedBuilder.PagedEmbed} to use.
     * @param page       The page to start on (0 Indexed).
     * @param channel    The channel to send the paged message to.
     * @param sender     The user who requested the embed
     */
    public static void sendEmbedPagedMessage(PagedEmbedBuilder.PagedEmbed pagedEmbed, int page, InteractionHook channel, User sender, String group) {
        if (page < 0 || page > pagedEmbed.getPageTotal() - 1) {
            MessageUtils.sendErrorMessage("Ошибка страницы: " + (page + 1) + " Всего страниц: " + pagedEmbed.getPageTotal(), channel);
            return;
        }
        if (!pagedEmbed.isSinglePage()) {
            List<MyButton> buttonGroup = new ArrayList<>();
            Integer[] pages = new Integer[]{page};
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"start", "начальная"), (event) -> {
                //Start
                pages[0] = 0;
                event.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"prev", "предыдущая"), (event) -> {
                //Prev
                if (pages[0] != 0) {
                    pages[0] -= 1;
                    event.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
                }
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"next", "следующая"), (event) -> {
                //Next
                if (pages[0] + 1 != pagedEmbed.getPageTotal()) {
                    pages[0] += 1;
                    event.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
                }
            }));
            buttonGroup.add(new MyButton(Button.primary(sender.getIdLong()+"last", "последная"), (event) -> {
                //Last
                pages[0] = pagedEmbed.getPageTotal() - 1;
                event.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
            }));

//            buttonGroup.add(new MyButton(Button.danger(sender.getIdLong()+"delete", "удалить"),sender,  (event) -> {
//                // Delete
//               channel.deleteOriginal().queue();
//            }));
            ButtonUtil.sendButtonedMessage(channel, pagedEmbed.getEmbed(page), buttonGroup);
        } else {
            channel.sendMessageEmbeds(pagedEmbed.getEmbed(page)).queue();
        }
    }
//
//    /**
//     * Sends an embed paged message the to specified channel.
//     * You can build with Embed with {@link PagedEmbedBuilder}.
//     *
//     * @param pagedEmbed The {@link PagedEmbedBuilder.PagedEmbed} to use.
//     * @param page       The page to start on (0 Indexed).
//     * @param channel    The channel to send the paged message to.
//     * @param sender     The user who requested the embed
//     */
//    public static void sendEmbedPagedMessage(PagedEmbedBuilder.PagedEmbed pagedEmbed, int page, InteractionHook channel, User sender, String group) {
//        if (page < 0 || page > pagedEmbed.getPageTotal() - 1) {
//            MessageUtils.sendErrorMessage("Ошибка страницы: " + (page + 1) + " Всего страниц: " + pagedEmbed.getPageTotal(), channel);
//            return;
//        }
//        if (!pagedEmbed.isSinglePage()) {
//            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), group);
//            Integer[] pages = new Integer[]{page};
////            buttonGroup.addButton(new ButtonGroup.Button("\u23EE", (ownerID, user, message) -> {
////                //Start
////                pages[0] = 0;
////                message.editMessage(pagedEmbed.getEmbed(pages[0])).queue();
////            }));
////            buttonGroup.addButton(new ButtonGroup.Button("\u23EA", (ownerID, user, message) -> {
////                //Prev
////                if (pages[0] != 0) {
////                    pages[0] -= 1;
////                    message.editMessage(pagedEmbed.getEmbed(pages[0])).queue();
////                }
////            }));
////            buttonGroup.addButton(new ButtonGroup.Button("\u23E9", (ownerID, user, message) -> {
////                //Next
////                if (pages[0] + 1 != pagedEmbed.getPageTotal()) {
////                    pages[0] += 1;
////                    message.editMessage(pagedEmbed.getEmbed(pages[0])).queue();
////                }
////            }));
////            buttonGroup.addButton(new ButtonGroup.Button("\u23ED", (ownerID, user, message) -> {
////                //Last
////                pages[0] = pagedEmbed.getPageTotal() - 1;
////                message.editMessage(pagedEmbed.getEmbed(pages[0])).queue();
////            }));
////            buttonGroup.addButton(new Button("\u274C", (ownerID, user, message) -> {
////                // Delete
////                //TODO Права не так берет или хз надо тоже пофиксить
////                if (user.getIdLong() == ownerID ||
////                        message.getGuild().getMember(user).hasPermission(Permission.MESSAGE_MANAGE)) {
////                    message.delete().queue(null, e -> {
////                    });
////                } else {
////                    MessageUtils.sendErrorMessage("Нет прав менять сообщение!", (TextChannel) message.getChannel());
////                }
////            }));
//            ButtonUtil.sendButtonedMessage(channel, pagedEmbed.getEmbed(page), buttonGroup);
//        } else {
//            channel.sendMessageEmbeds(pagedEmbed.getEmbed(page)).queue();
//        }
//    }

    /**
     * This is a sub-enum used to determine how the content will be split and displayed in pages.
     */
    public enum SplitMethod {
        CHAR_COUNT,
        NEW_LINES
    }
}