package ru.rien.bot.commands.admin.permissions;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.*;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.objects.GuildWrapperLoader;
import ru.rien.bot.permission.Group;
import ru.rien.bot.permission.PerGuildPermissions;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GeneralUtils;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionsCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
//        String[] args = event.getArgs();
//        TextChannel channel = event.getChannel();
//        User sender = event.getSender();
//        GuildWrapper guild = event.getGuild();
//        if (args.length >= 2) {
//            if (args[0].equalsIgnoreCase("group")) {
//                String groupString = args[1];
//                Group group = getPermissions(channel).getGroup(groupString);
//                if (args.length >= 3) {
//                    if (group == null && !args[2].equalsIgnoreCase("create")) {
//                        MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `{%}permissions group " + groupString + " create`", channel);
//                        return;
//                    } else if (args[2].equalsIgnoreCase("add")) {
//                        if (args.length == 4) {
//                            if (!Permission.isValidPermission(args[3])) {
//                                MessageUtils.sendErrorMessage("Такие права не найдены! Права начинаются с `justbot.` затем название команды!\n" +
//                                        "**Пример:** `justbot.play`\n" +
//                                        "Просмотр всех прав `"+event.getGuild().getPrefix()+"permissions list`", channel);
//                                return;
//                            }
//                            if (group.addPermission(args[3])) {
//                                MessageUtils.sendSuccessMessage("+ права `" + args[3] + "` в группу `" + groupString + "`", channel, sender);
//                                return;
//                            } else {
//                                MessageUtils.sendErrorMessage("Не удалось добавить права (возможно они уже есть)", channel);
//                                return;
//                            }
//
//                        }
//                    } else if (args[2].equalsIgnoreCase("remove")) {
//                        if (args.length == 4) {
//                            if (group.removePermission(args[3])) {
//                                MessageUtils.sendSuccessMessage("- права `" + args[3] + "` в группу `" + groupString + "`", channel, sender);
//                                return;
//                            } else {
//                                MessageUtils.sendErrorMessage("Не удалось удалить права (возможно они уже удалены)", channel);
//                                return;
//                            }
//                        }
//                    } else if (args[2].equalsIgnoreCase("create")) {
//                        if (!GuildWrapperLoader.ALLOWED_CHARS_REGEX.matcher(groupString).matches()) {
//                            if (groupString.length() > 32)
//                                MessageUtils.sendErrorMessage("Имя группы не должно быть больше 32 символов!", channel);
//                            else if (groupString.length() < 3)
//                                MessageUtils.sendErrorMessage("Имя группы должно быть с 3 символов!", channel);
//                            else
//                                MessageUtils.sendErrorMessage("Имя содержит странное название. Допустимые символы: `" + new String(GuildWrapperLoader.ALLOWED_SPECIAL_CHARACTERS) + "`", channel);
//                            return;
//                        }
//                        if (getPermissions(channel).addGroup(groupString)) {
//                            MessageUtils.sendSuccessMessage("Группа: `" + groupString + "` создана", channel, sender);
//                            return;
//                        } else {
//                            MessageUtils.sendErrorMessage("Группа уже существует", channel);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("delete")) {
//                        getPermissions(channel).deleteGroup(groupString);
//                        MessageUtils.sendSuccessMessage("Группа `" + groupString + "` удалена", channel, sender);
//                        return;
//                    } else if (args[2].equalsIgnoreCase("link")) {
//                        if (args.length == 4) {
//                            Role role = GuildUtils.getRole(args[3], guild.getGuildId());
//                            if (role != null) {
//                                group.linkRole(role.getId());
//                                MessageUtils.sendSuccessMessage("Группа `" + groupString + "` связана с `" + role.getName() + "`", channel, sender);
//                                return;
//                            } else {
//                                MessageUtils.sendErrorMessage("Роль не найдена!", channel);
//                                return;
//                            }
//                        }
//                    } else if (args[2].equalsIgnoreCase("unlink")) {
//                        Role role;
//                        if (group.getRoleId() == null || (role =
//                                guild.getGuild().getRoleById(group.getRoleId())) == null) {
//                            MessageUtils.sendErrorMessage("Роль не связана!!", channel);
//                            return;
//                        } else {
//                            group.linkRole(null);
//                            MessageUtils.sendSuccessMessage("Развязано " + role.getName() + " с " + group.getName(), channel, sender);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("list")) {
//                        if (args.length <= 4) {
//                            int page = args.length == 4 ? Integer.valueOf(args[3]) : 1;
//                            Set<String> perms = group.getPermissions();
//                            List<String> permList = GeneralUtils.orderList(perms);
//
//                            String list = permList.stream().collect(Collectors.joining("\n"));
//
//                            PagedEmbedBuilder<String> pe =
//                                    new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list, PaginationUtil.SplitMethod.NEW_LINES, 25));
//                            pe.setTitle("Список прав группы: " + group.getName());
//                            pe.enableCodeBlock();
//
//                            PaginationUtil.sendEmbedPagedMessage(pe.build(), page - 1, channel, sender, ButtonGroupConstants.PERMISSIONS_GROUP);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("massadd")) {
//                        if (args.length == 4) {
//                            List<Member> roleMembers;
//                            String roleName = "";
//                            switch (args[3]) {
//                                case "@everyone":
//                                    roleMembers = guild.getGuild().getMembers();
//                                    roleName = "everyone";
//                                    break;
//                                case "@here":
//                                    roleMembers = channel.getMembers();
//                                    roleName = "here";
//                                    break;
//                                default:
//                                    Role role = GuildUtils.getRole(args[3], guild.getGuildId());
//                                    if (role != null) {
//                                        roleMembers = guild.getGuild().getMembersWithRoles(role);
//                                    } else {
//                                        MessageUtils.sendErrorMessage("Такой роли нет", channel);
//                                        return;
//                                    }
//                                    break;
//                            }
//                            for (Member user : roleMembers) {
//                                getPermissions(channel).getUser(user).addGroup(group);
//                            }
//                            MessageUtils.sendSuccessMessage("Успешно добавлена `" + groupString + "` группа для всех  @" + roleName, channel, sender);
//                            return;
//
//                        }
//                    } else if (args[2].equalsIgnoreCase("clear")) {
//                        group.getPermissions().clear();
//                        MessageUtils.sendSuccessMessage("Все права удалены группы : " + group.getName(), channel);
//                        return;
//                    } else if (args[2].equalsIgnoreCase("move") && args.length >= 4) {
//                        int pos = GeneralUtils.getInt(args[3], -1);
//                        if (pos < 1 || pos >= guild.getPermissions().getGroups().size()) {
//                            MessageUtils.sendWarningMessage("Неверная позиция : " + args[3], channel);
//                            return;
//                        } else {
//                            guild.getPermissions().moveGroup(group, pos - 1);
//                            MessageUtils.sendSuccessMessage("Группа `" + groupString + "` сдвинута на позицию " + pos, channel, sender);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("clone") && args.length >= 4) {
//                        if (guild.getPermissions().cloneGroup(group, args[3])) {
//                            MessageUtils.sendMessage("Клон группы был создан", channel);
//                            return;
//                        } else {
//                            MessageUtils.sendWarningMessage("Не удалось создать клона (возможно он уже есть)", channel);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("rename") && args.length >= 4) {
//                        if (guild.getPermissions().renameGroup(group, args[3])) {
//                            MessageUtils.sendMessage("Группа переименована", channel);
//                            return;
//                        } else {
//                            MessageUtils.sendWarningMessage("Не удалось переименовать (возможно уже такое имя есть)", channel);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("def")){
//                        if (args.length == 4) {
//                            String bool = args[3];
//                            group.setDef(Boolean.parseBoolean(bool));
//                            MessageUtils.sendInfoMessage("Изначальная роль установлена!",channel, sender);
//                            return;
//                        }
//                    }
//                }
//            } else if (args[0].equalsIgnoreCase("user")) {
//                String userString = args[1];
//                User user = GuildUtils.getUser(userString, guild.getGuildId());
//                if (user == null) {
//                    MessageUtils.sendErrorMessage("Пользователь не найден", channel);
//                    return;
//                }
//               ru.rien.bot.permission.User permUser =
//                        getPermissions(channel).getUser(guild.getGuild().getMember(user));
//                if (args.length >= 3) {
//                    if (args[2].equalsIgnoreCase("group")) {
//                        if (args.length >= 4) {
//                            if (args[3].equalsIgnoreCase("add")) {
//                                if (args.length == 5) {
//                                    String groupString = args[4];
//                                    Group group = getPermissions(channel).getGroup(groupString);
//                                    if (group == null) {
//                                        MessageUtils.sendErrorMessage("Группа не найдена. Создайте её через `"
//                                                + getPrefix(channel.getGuild()) + "permissions group " + groupString
//                                                + " create`", channel);
//                                        return;
//                                    }
//                                    permUser.addGroup(group);
//                                    MessageUtils.sendSuccessMessage("Группа `" + groupString
//                                            + "` добавлена пользователю " + user.getAsMention(), channel, sender);
//                                    return;
//                                }
//                            } else if (args[3].equalsIgnoreCase("remove")) {
//                                if (args.length == 5) {
//                                    String groupString = args[4];
//                                    Group group = getPermissions(channel).getGroup(groupString);
//                                    if (group == null) {
//                                        MessageUtils.sendErrorMessage("Группа не найдена", channel);
//                                        return;
//                                    }
//                                    if (permUser.removeGroup(group)) {
//                                        MessageUtils.sendSuccessMessage("Группа`" + groupString
//                                                + "` удалена с пользователя " + user.getAsMention(), channel, sender);
//                                        return;
//                                    } else {
//                                        MessageUtils.sendErrorMessage("Пользователь не состоит в этой группе", channel);
//                                        return;
//                                    }
//                                }
//                            } else if (args[3].equalsIgnoreCase("list")) {
//                                int page = (args.length == 5 ? GeneralUtils.getInt(args[4], 1) : 1) - 1;
//                                Set<String> groups = new HashSet<>(permUser.getGroups());
//                                groups.addAll(getPermissions(channel)
//                                        .getGroups()
//                                        .stream()
//                                        .filter(g -> guild.getGuild().getMember(user).getRoles()
//                                                .contains(guild.getGuild().getRoleById(g.getRoleId()))
//                                                || g.getRoleId().equals(guild.getGuildId()))
//                                        .map(Group::getName)
//                                        .collect(Collectors.toList()));
//                                List<String> groupList = GeneralUtils.orderList(groups);
//
//                                String list = groupList.stream().collect(Collectors.joining("\n"));
//
//                                PagedEmbedBuilder pe = new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list,
//                                        PaginationUtil.SplitMethod.NEW_LINES, 25))
//                                        .setTitle("Группы " + MessageUtils.getTag(user)).enableCodeBlock();
//
//                                PaginationUtil.sendEmbedPagedMessage(pe.build(), page, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_GROUPS);
//                                return;
//                            }
//                        }
//                    } else if (args[2].equalsIgnoreCase("permission")) {
//                        if (args.length >= 4) {
//                            if (args[3].equalsIgnoreCase("add")) {
//                                if (args.length == 5) {
//                                    if (!Permission.isValidPermission(args[4])) {
//                                        MessageUtils.sendErrorMessage("Такие права не найдены! Права начинаются с `justbot.` затем название команды!\n" +
//                                                "**Пример:** `justbot.play`\n" +
//                                                "Просмотр всех прав `"+event.getGuild().getPrefix()+"permissions list`", channel);
//                                        return;
//                                    }
//                                    if (permUser.addPermission(args[4])) {
//                                        MessageUtils.sendSuccessMessage("+ права `" + args[4] + "` " + user.getAsMention(), channel, sender);
//                                        return;
//                                    } else {
//                                        MessageUtils.sendErrorMessage("У пользователя уже есть такие права", channel);
//                                        return;
//                                    }
//                                }
//                            } else if (args[3].equalsIgnoreCase("remove")) {
//                                if (args.length == 5) {
//                                    if (permUser.removePermission(args[4])) {
//                                        MessageUtils.sendSuccessMessage("- права `" + args[4] + "` " + user.getAsMention(), channel, sender);
//                                        return;
//                                    } else {
//                                        MessageUtils.sendErrorMessage("У пользователя нету таких прав", channel);
//                                        return;
//                                    }
//                                }
//                            } else if (args[3].equalsIgnoreCase("list")) {
//                                int page = (args.length == 5 ? Integer.valueOf(args[4]) : 1) - 1;
//                                Set<String> perms = permUser.getPermissions();
//                                List<String> permList = GeneralUtils.orderList(perms);
//
//                                String list = permList.stream().collect(Collectors.joining("\n"));
//
//                                PagedEmbedBuilder<String> pe =
//                                        new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list, PaginationUtil.SplitMethod.NEW_LINES, 25));
//                                pe.setTitle("Права " + MessageUtils.getTag(user));
//                                pe.enableCodeBlock();
//                                PaginationUtil.sendEmbedPagedMessage(pe.build(), page, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_PERMISSIONS);
//                                return;
//                            }
//                        }
//                    } else if (args[2].equalsIgnoreCase("check")) {
//                        if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.ALL_PERMISSIONS)) {
//                            EmbedBuilder builder = new EmbedBuilder();
//                            builder.setTitle("Права " + user.getName());
//                            builder.setDescription("**Все права!**");
//                            channel.sendMessage(builder.build()).queue();
//                            return;
//                        } else {
//                            String perms = Arrays.stream(Permission.values())
//                                    .filter(p -> getPermissions(channel).hasPermission(guild.getGuild().getMember(user), p))
//                                    .map(m -> "`" + m + "`")
//                                    .collect(Collectors.joining("\n"));
//                            PagedEmbedBuilder<String> embedBuilder =
//                                    new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(perms, PaginationUtil.SplitMethod.NEW_LINES, 20));
//                            embedBuilder.setTitle("Права " + MessageUtils.getTag(user));
//                            PaginationUtil.sendEmbedPagedMessage(embedBuilder.build(), 0, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_CHECK);
//                            return;
//                        }
//                    } else if (args[2].equalsIgnoreCase("clear")) {
//                        permUser.getPermissions().clear();
//                        MessageUtils.sendSuccessMessage("Права удалены : " + MessageUtils.getTag(user), channel);
//                        return;
//                    }
//                }
//            }
//        } else if (args.length >= 1) {
//            if (args[0].equalsIgnoreCase("groups")) {
//                if (this.getPermissions(channel).getGroups().isEmpty()) {
//                    channel.sendMessage(MessageUtils.getEmbed(sender)
//                            .setColor(Color.RED)
//                            .setDescription("Для сервера нет групп!")
//                            .build()).queue();
//                    return;
//                } else {
//                    int page = args.length == 2 ? Integer.valueOf(args[1]) : 1;
//
//                    StringBuilder stringBuilder = new StringBuilder();
//                    int i = 1;
//                    for (Group group : guild.getPermissions().getGroups()) {
//                        stringBuilder.append(i).append(". ").append(group.getName()).append("\n");
//                        i++;
//                    }
//
//                    PagedEmbedBuilder<String> pe =
//                            new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(stringBuilder.toString(), PaginationUtil.SplitMethod.NEW_LINES, 20));
//                    pe.setTitle("Groups");
//                    pe.enableCodeBlock();
//                    PaginationUtil.sendEmbedPagedMessage(pe.build(), page - 1, channel, sender, ButtonGroupConstants.PERMISSIONS_GROUPS);
//                    return;
//                }
//            } else if (args[0].equalsIgnoreCase("list")) {
//                StringBuilder defaultPerms = new StringBuilder("**Обычные права**\n");
//                StringBuilder nonDefaultPerms = new StringBuilder("**Не обычные права**\n");
//                for (Permission p : Permission.values()) {
//                    if (p == Permission.ALL_PERMISSIONS) continue;
//                    if (p.isDefaultPerm())
//                        defaultPerms.append("`").append(p).append("`").append("\n");
//                    else
//                        nonDefaultPerms.append("`").append(p).append("`").append("\n");
//                }
//                PagedEmbedBuilder<String> embedBuilder =
//                        new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(
//                                defaultPerms.append("\n").append(nonDefaultPerms.toString()).toString(),
//                                PaginationUtil.SplitMethod.NEW_LINES,
//                                20
//                        ));
//                embedBuilder.setTitle("Права");
//                PaginationUtil.sendEmbedPagedMessage(embedBuilder.build(), 0, channel, sender, ButtonGroupConstants.PERMISSIONS_LIST);
//                return;
//            } else if (args[0].equalsIgnoreCase("reset")) {
//                guild.setPermissions(new PerGuildPermissions());
//                MessageUtils.sendSuccessMessage("Права перезагружены", channel, sender);
//                return;
//            }
//        }
//        MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
    }

    @Override
    public String getCommand() {
        return "permissions";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"perm", "perms"};
    }

    @Override
    public String getDescription(Language guild) {
        return "Управления правами.";
    }


//    //TODO: Pagination
//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return "`{%}permissions group <группа> add|remove <права>` - Добавить права группе.\n" +
//                "`{%}permissions group <группа> create|delete` - Создание/Удаление группы.\n" +
//                "`{%}permissions group <группа> link <роль>` - Привязать к роли дискорда.\n" +
//                "`{%}permissions group <группа> unlink` - Отвязать от ролей.\n" +
//                "`{%}permissions group <группа> list [страница]` - Список прав группы.\n" +
//                "`{%}permissions group <группа> massadd <@everyone/@here/role>` - Помещает всех с этой ролью в группу.\n" +
//                "`{%}permissions group <группа> clear` - Очистить права группы!\n" +
//                "`{%}permissions group <группа> move <место>` - Переместить группы по иерархии.\n" +
//                "`{%}permissions group <группа> clone <новая_группа>` - Клонировать группу.\n" +
//                "`{%}permissions group <группа> rename <новое_имя>` - Переименовать группу.\n\n" +
//                "`{%}permissions user <Участник> group add|remove <Группа>` - Добавить/Удалить группу участнику.\n" +
//                "`{%}permissions user <Участник> group list [страница]` - Просмотр групп участника.\n" +
//                "`{%}permissions user <Участник> permission add|remove <права>` - Дать права участнику.\n" +
//                "`{%}permissions user <Участник> permission list [права]` - Просмотр прав участника (Права групп не показываются).\n" +
//                "`{%}permissions user <Участник> check` - Показывает к чему имеет доступ участник\n" +
//                "`{%}permissions user <Участник> clear` - Удаляет все права!\n\n" +
//                "`{%}permissions groups` - Просмотр групп.\n" +
//                "`{%}permissions list` - Просмотр всех прав бота!\n" +
//                "`{%}permissions reset` - Очистить все.";
//    }

    @Override
    public List<SubCommandGroups> getSubCommandGruops() {
        return Lists.newArrayList(new PermissionsGroupCommand());
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }

}