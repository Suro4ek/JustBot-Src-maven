package ru.rien.bot.modules.command;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.modules.privatka.PrivatkaCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.GuildService;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@EnableAsync
public class ModuleCommand extends ModuleDiscord {

    private static Set<Command> commands = Sets.newConcurrentHashSet();
    private static long OWNER_SERVER_ID = 809053595722842153L;
    private Map<String, Integer> spamMap = new ConcurrentHashMap<>();
    public static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService COMMAND_POOL = Executors.newFixedThreadPool(4, r ->
       new Thread(COMMAND_THREADS, r, "Command Pool-"+COMMAND_THREADS.activeCount())
    );
    private GuildService guildService;
    private static ModuleCommand instance;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;
    private static final List<Long> removedByMe = new ArrayList<>();
    private final AtomicInteger commandCounter = new AtomicInteger(0);

    private final Pattern multiSpace = Pattern.compile(" {2,}");

    public ModuleCommand(GuildService guildService) {
        super("command", false);
        this.guildService = guildService;
        instance = this;
    }


    public static ModuleCommand getInstance() {
        return instance;
    }

    public Set<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toSet());
    }

    @Scheduled(fixedRate = 3000)
    public void clearSpam(){
        this.getSpamMap().clear();
    }

    public Map<String, Integer> getSpamMap() {
        return spamMap;
    }

    @Override
    protected void onEnable() {
        registerListenerThis();
        getLogger().info("Loading commands...");
        CommandListUpdateAction commands1 = getModuleDsBot().getJda().updateCommands();
        GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuild(OWNER_SERVER_ID+"");
        Guild guild = guildWrapper.getGuild();
        CommandListUpdateAction commands2 = guild.updateCommands();
        configurableApplicationContext.getBeansOfType(Command.class).forEach((s, command) -> {
            commands.add(command);
            if(command.getType() != CommandType.ADMIN) {
                if(command.getSubCommands(true).isEmpty()) {
                    if (command.parameters().length == 0) {
                        commands1.addCommands(new CommandData(command.getCommand(),
                                command.getDescription(Language.RUSSIAN))
                                .addSubcommandGroups(command.getSubCommandGruops().stream()
                                        .map(subCommandGroups ->
                                                subCommandGroups.getSubcommandGroup().addSubcommands(
                                                        subCommandGroups.subcommands().stream().map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters()))
                                                                .toArray(SubcommandData[]::new))).toArray(SubcommandGroupData[]::new))
                                .addSubcommands(command.getSubCommands(false).stream().
                                        map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters())).toArray(SubcommandData[]::new)));
                    } else {
                        commands1.addCommands(new CommandData(command.getCommand(),
                                command.getDescription(Language.RUSSIAN)).
                                addOptions(command.parameters()));
                    }
                }else {
                    if (command.parameters().length == 0) {
                        if (!command.getSubCommands(false).isEmpty()) {
                            commands1.addCommands(new CommandData(command.getCommand(),
                                    command.getDescription(Language.RUSSIAN))
                                    .addSubcommandGroups(command.getSubCommandGruops().stream()
                                            .map(subCommandGroups ->
                                                    subCommandGroups.getSubcommandGroup().addSubcommands(
                                                            subCommandGroups.subcommands().stream().map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters()))
                                                                    .toArray(SubcommandData[]::new))).toArray(SubcommandGroupData[]::new))
                                    .addSubcommands(command.getSubCommands(false).stream().
                                            map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters())).toArray(SubcommandData[]::new)));
                        }
                        commands2.addCommands(new CommandData(command.getCommand(),
                                command.getDescription(Language.RUSSIAN))
                                .addSubcommandGroups(command.getSubCommandGruops().stream()
                                        .map(subCommandGroups ->
                                                subCommandGroups.getSubcommandGroup().addSubcommands(
                                                        subCommandGroups.subcommands().stream().map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters()))
                                                                .toArray(SubcommandData[]::new))).toArray(SubcommandGroupData[]::new))
                                .addSubcommands(command.getSubCommands(true).stream().
                                        map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters())).toArray(SubcommandData[]::new)));
                    }
                }
            }else{
                    if (command.parameters().length == 0) {
                        commands2.addCommands(new CommandData(command.getCommand(),
                                command.getDescription(Language.RUSSIAN))
                                .addSubcommandGroups(command.getSubCommandGruops().stream()
                                        .map(subCommandGroups ->
                                                subCommandGroups.getSubcommandGroup().addSubcommands(
                                                        subCommandGroups.subcommands().stream().map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters()))
                                                                .toArray(SubcommandData[]::new))).toArray(SubcommandGroupData[]::new))
                                .addSubcommands(command.getSubCommands(true).stream().
                                        map(subCommand -> subCommand.getSubCommands().addOptions(subCommand.parameters())).toArray(SubcommandData[]::new)));
                    } else {
                        commands2.addCommands(new CommandData(command.getCommand(),
                                command.getDescription(Language.RUSSIAN)).
                                addOptions(command.parameters()));
                    }
            }
        });
        commands2.queue(commands3 -> {
            System.out.println("guild commands " + commands3.size());
        });
        commands1.queue(commands4 -> System.out.println(commands4.size()));
        guildService.findall().forEach(guildEntity -> {
            GuildWrapper guildWrapper1 = JustBotManager.instance().getGuildNoCache(guildEntity.getGuildid()+"");
            if(guildWrapper1.getGuild() != null){
                Guild guild1 = guildWrapper1.getGuild();
                if(guildWrapper1.getSettings().getBlacklistCommands().size() != 0){
                    guildWrapper1.getSettings().getBlacklistCommands().forEach(command -> {
                        guild1.retrieveCommands().queue(commands3 -> {
                            commands3.forEach(command1 -> {
                                if(command1.getName().equalsIgnoreCase(command)){
                                    command1.delete().queue();
                                }
                            });
                        });
                    });
                }
            }
        });
        registerListener(new Events());
    }

    public void registerCommand(Command command){
        commands.add(command);
    }
//
//    public void unregisterCommand(Command command){
//        commands.remove(command);
//    }

//    private String getGuildId(GenericGuildMessageEvent e) {
//        return e.getChannel().getGuild() != null ? e.getChannel().getGuild().getId() : null;
//    }

    @Nullable
    public static Command getCommand(String s) {
        for (Command cmd : getCommands()) {
            if (cmd.getCommand().equalsIgnoreCase(s))
                return cmd;
        }
        return null;
    }

    public static Set<Command> getCommands() {
        return commands;
    }

    private void handleCommand(SlashCommandEvent event, Command cmd) {
        GuildWrapper guild = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if(guild.getSettings().getBlacklistCommands().contains(cmd)){
            return;
        }

        if (guild.isBlocked() && System.currentTimeMillis() > guild.getUnBlockTime() && guild.getUnBlockTime() != -1)
            guild.revokeBlock();

        handleSpamDetection(event, guild);

        if (handleMissingPermission(cmd,guild, event, event.getMember())) return;

        if (guild.isBlocked()) return;

        if(cmd.getType().equals(CommandType.NSWF) && guild.getNswf() != null && guild.getNswf() != event.getChannel().getIdLong()) return;
        if(cmd.getType().equals(CommandType.ANIME) && guild.getAnime() != null && guild.getAnime() != event.getChannel().getIdLong()) return;

        dispatchCommand(cmd, event, guild);
    }

    private void handleCommand(SlashCommandEvent event, Command cmd, SubCommand sub) {
        GuildWrapper guild = getModuleDsBot().getManager().getGuild(event.getGuild().getId());
        if(guild.getSettings().getBlacklistCommands().contains(cmd)){
            return;
        }

        if (guild.isBlocked() && System.currentTimeMillis() > guild.getUnBlockTime() && guild.getUnBlockTime() != -1)
            guild.revokeBlock();

        handleSpamDetection(event, guild);

        if (handleMissingPermission(cmd,guild, event, event.getMember())) return;

        if (guild.isBlocked()) return;

        if(cmd.getType().equals(CommandType.NSWF) && guild.getNswf() != null && guild.getNswf() != event.getChannel().getIdLong()) return;
        if(cmd.getType().equals(CommandType.ANIME) && guild.getAnime() != null && guild.getAnime() != event.getChannel().getIdLong()) return;

        dispatchCommand(sub, event, guild);
    }

    /**
     * This handles if the user has permission to run a command. This should return <b>true</b> if the user does NOT
     * have permission to run the command.
     *
     * @param cmd The command to be ran.
     * @param e   The event this came from.
     * @return If the user has permission to run the command, this will return <b>true</b> if they do NOT have permission.
     */
    private boolean handleMissingPermission(Command cmd,GuildWrapper guild, SlashCommandEvent e, Member member) {
            if (cmd.getPermission() != null) {
                if (!cmd.getPermissions(e.getTextChannel()).hasPermission(member, cmd.getPermission())) {
                    e.replyEmbeds(MessageUtils.getEmbed(e.getUser()).setColor(Color.red)
                                    .setDescription(
                                            guild.getMessage("NEED_PERMISSION",
                                                    cmd.getPermission())).build()).queue();
                    return true;
                }
            }

            return !cmd.getPermissions(e.getTextChannel()).hasPermission(
                    member,
                    ru.rien.bot.permission.Permission.getPermission(cmd.getType())
            ) && cmd.getPermission() == null;
    }


    private void handleSpamDetection(SlashCommandEvent event, GuildWrapper guild) {
        if (spamMap.containsKey(event.getGuild().getId())) {
            int messages = spamMap.get(event.getGuild().getId());
            double allowed = Math.floor(Math.sqrt(GuildUtils.getGuildUserCount(event.getGuild()) / 2.5));
            allowed = allowed == 0 ? 1 : allowed;
            if (messages > allowed) {
                if (!guild.isBlocked()) {
                    MessageUtils.sendErrorMessage(guild.getMessage("SPAM_DETECT"), event.getTextChannel());
                    guild.addBlocked("Спам командами", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                }
            } else {
                spamMap.put(event.getGuild().getId(), messages + 1);
            }
        } else {
            spamMap.put(event.getGuild().getId(), 1);
        }
    }

    private void dispatchCommand(SubCommand cmd, SlashCommandEvent event, GuildWrapper guild) {
        COMMAND_POOL.submit(() -> {
            Map<String, String> mdcContext = (MDC.getCopyOfContextMap() == null ? new HashMap<>() : MDC.getCopyOfContextMap());
            mdcContext.put("command", cmd.getSubCommands().getName());
//            mdcContext.put("args", Arrays.toString(args).replace("\n", "\\n"));
            mdcContext.put("guild", event.getGuild().getId());
            mdcContext.put("user", event.getUser().getId());
            MDC.setContextMap(mdcContext);

            getLogger().info(
                    "Использование команды '" + cmd.getSubCommands().getName() + "' " + " в " + event.getChannel() + "! Отправитель: " +
                            event.getUser().getName() + '#' + event.getUser().getDiscriminator());
            commandCounter.incrementAndGet();
            try {
                CommandEvent commandEvent = new CommandEvent(cmd, event.getUser(), guild, event.getTextChannel(), event.getOptions(), event
                        .getMember(), event);
                cmd.execute(commandEvent);

//                MessageEmbed.Field field = null;
//                if (args.length > 0) {
//                    String s = MessageUtils.getMessage(args, 0).replaceAll("`", "'");
//                    if (s.length() > 1000)
//                        s = s.substring(0, 1000) + "...";
////                    field = new MessageEmbed.Field("Аргументы", "`" + s + "`", false);
////                    event.getChannel().sendMessage(field).queue();
//                }
            } catch (Exception ex) {
                getLogger().error("Ошибка в guild " + event.getGuild().getId() + "!\n" + '\'' + cmd.getSubCommands().getName() + "' "
                        + " в " + event.getChannel() + "! Отправитель: " +
                        event.getUser().getName() + '#' + event.getUser().getDiscriminator(), ex);
            }

//            if (cmd.deleteMessage()) {
//                delete(event.getMessage());
//                removedByMe.add(event.getMessageIdLong());
//            }
        });
    }

    private void dispatchCommand(Command cmd, SlashCommandEvent event, GuildWrapper guild) {
        COMMAND_POOL.submit(() -> {
            Map<String, String> mdcContext = (MDC.getCopyOfContextMap() == null ? new HashMap<>() : MDC.getCopyOfContextMap());
            mdcContext.put("command", cmd.getCommand());
//            mdcContext.put("args", Arrays.toString(args).replace("\n", "\\n"));
            mdcContext.put("guild", event.getGuild().getId());
            mdcContext.put("user", event.getUser().getId());
            MDC.setContextMap(mdcContext);

            getLogger().info(
                    "Использование команды '" + cmd.getCommand() + "' " + " в " + event.getChannel() + "! Отправитель: " +
                            event.getUser().getName() + '#' + event.getUser().getDiscriminator());
            commandCounter.incrementAndGet();
            try {
                CommandEvent commandEvent = new CommandEvent(cmd, event.getUser(), guild, event.getTextChannel(), event.getOptions(), event
                        .getMember(), event);
                cmd.execute(commandEvent);

//                MessageEmbed.Field field = null;
//                if (args.length > 0) {
//                    String s = MessageUtils.getMessage(args, 0).replaceAll("`", "'");
//                    if (s.length() > 1000)
//                        s = s.substring(0, 1000) + "...";
////                    field = new MessageEmbed.Field("Аргументы", "`" + s + "`", false);
////                    event.getChannel().sendMessage(field).queue();
//                }
            } catch (Exception ex) {
                getLogger().error("Ошибка в guild " + event.getGuild().getId() + "!\n" + '\'' + cmd.getCommand() + "' "
                        + " в " + event.getChannel() + "! Отправитель: " +
                        event.getUser().getName() + '#' + event.getUser().getDiscriminator(), ex);
            }

//            if (cmd.deleteMessage()) {
//                delete(event.getMessage());
//                removedByMe.add(event.getMessageIdLong());
//            }
        });
    }

    private void delete(Message message) {
        if(message.getTextChannel().getGuild().getSelfMember()
                    .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE))
                message.delete().queue();
    }

    @Override
    public void onSlashCommandEvent(@NotNull SlashCommandEvent event) {
        if(event.getGuild() == null){
            return;
        }
        String command = event.getName();
//        EnumSet<Permission> perms = event.getGuild().getSelfMember().getPermissions(event.getTextChannel());
//        if (!perms.contains(Permission.ADMINISTRATOR)) {
//            if (!perms.contains(Permission.MESSAGE_WRITE)) {
//                return;
//            }
//            if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
//                event.getChannel().sendMessage("Я не могу приклепать." +
//                        "\nДай пожалуйста мне права `." +
//                        "\nСпасибо :D").queue();
//                return;
//            }
//        }
        Command cmd = this.getCommand(command);
        if (cmd != null) {
            SubCommandGroups subCommandGroups = cmd.getSubCommandGruops().stream().filter(subCommand1 ->
                    (subCommand1.getSubcommandGroup().getName().equals(event.getSubcommandGroup())
                    )).findFirst().orElse(null);
            if(subCommandGroups != null){
                SubCommand subCommand = subCommandGroups.subcommands().stream().filter(subCommand1 ->
                        (subCommand1.getSubCommands().getName().equals(event.getSubcommandName())))
                        .findFirst().orElse(null);
                if(subCommand != null) {
                    handleCommand(event, cmd, subCommand);
                    return;
                }
            }

            SubCommand subCommand = cmd.getSubCommands(false).stream().filter(subCommand1 ->
                    (subCommand1.getSubCommands().getName().equals(event.getSubcommandName())
            )).findFirst().orElse(null);
            if(subCommand != null){
                handleCommand(event, cmd, subCommand);
                return;
            }
            subCommand = cmd.getSubCommands(true).stream().filter(subCommand2 ->
                    (subCommand2.getSubCommands().getName().equals(event.getSubcommandName())
                    )).findFirst().orElse(null);
            if(subCommand != null){
                handleCommand(event, cmd, subCommand);
                return;
            }
            handleCommand(event, cmd);
        }
    }

//    @Override
//    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
//        if (event.getAuthor().isBot()) return;
//        String message = multiSpace.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
//        if (message.startsWith("" + JustBotManager.instance().getGuild(getGuildId(event)).getPrefix())) {
//
//        }else{
//            if (message.startsWith("_prefix") || message.replace("!","").startsWith(event.getGuild().getSelfMember().getAsMention())) {
//                event.getChannel().sendMessage(MessageUtils.getEmbed(event.getAuthor())
//                        .setDescription("Префикс на сервере `" + JustBotManager
//                                .instance().getGuild(getGuildId(event)).getPrefix() + "`")
//                        .build()).queue();
//            }
//        }
//    }
}
