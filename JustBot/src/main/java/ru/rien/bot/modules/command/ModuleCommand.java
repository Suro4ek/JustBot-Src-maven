package ru.rien.bot.modules.command;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
import ru.rien.bot.objects.GuildWrapper;
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
    private Map<String, Integer> spamMap = new ConcurrentHashMap<>();
    public static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService COMMAND_POOL = Executors.newFixedThreadPool(4, r ->
       new Thread(COMMAND_THREADS, r, "Command Pool-"+COMMAND_THREADS.activeCount())
    );
    private static ModuleCommand instance;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;
    private static final List<Long> removedByMe = new ArrayList<>();
    private final AtomicInteger commandCounter = new AtomicInteger(0);

    private final Pattern multiSpace = Pattern.compile(" {2,}");

    public ModuleCommand() {
        super("command", false);
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
        configurableApplicationContext.getBeansOfType(Command.class).forEach((s, command) -> {
            registerCommand(command);
        });
        registerListener(new Events());
    }

    public void registerCommand(Command command){
        commands.add(command);
    }

    public void unregisterCommand(Command command){
        commands.remove(command);
    }

    private String getGuildId(GenericGuildMessageEvent e) {
        return e.getChannel().getGuild() != null ? e.getChannel().getGuild().getId() : null;
    }

    @Nullable
    public Command getCommand(String s, User user) {
        for (Command cmd : getCommands()) {
            if (cmd.getCommand().equalsIgnoreCase(s))
                return cmd;
            for (String alias : cmd.getAliases())
                if (alias.equalsIgnoreCase(s)) return cmd;
        }
        return null;
    }

    public static Set<Command> getCommands() {
        return commands;
    }

    private void handleCommand(GuildMessageReceivedEvent event, Command cmd, String[] args) {
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

        dispatchCommand(cmd, args, event, guild);
    }

    /**
     * This handles if the user has permission to run a command. This should return <b>true</b> if the user does NOT
     * have permission to run the command.
     *
     * @param cmd The command to be ran.
     * @param e   The event this came from.
     * @return If the user has permission to run the command, this will return <b>true</b> if they do NOT have permission.
     */
    private boolean handleMissingPermission(Command cmd,GuildWrapper guild, GuildMessageReceivedEvent e, Member member) {
            if (cmd.getPermission() != null) {
                if (!cmd.getPermissions(e.getChannel()).hasPermission(member, cmd.getPermission())) {
                    MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(e.getAuthor()).setColor(Color.red)
                                    .setDescription(
                                            guild.getMessage("NEED_PERMISSION",
                                                    cmd.getPermission())).build(), 5000,
                            e.getChannel());
                    delete(e.getMessage());
                    return true;
                }
            }

            return !cmd.getPermissions(e.getChannel()).hasPermission(
                    member,
                    ru.rien.bot.permission.Permission.getPermission(cmd.getType())
            ) && cmd.getPermission() == null;
    }


    private void handleSpamDetection(GuildMessageReceivedEvent event, GuildWrapper guild) {
        if (spamMap.containsKey(event.getGuild().getId())) {
            int messages = spamMap.get(event.getGuild().getId());
            double allowed = Math.floor(Math.sqrt(GuildUtils.getGuildUserCount(event.getGuild()) / 2.5));
            allowed = allowed == 0 ? 1 : allowed;
            if (messages > allowed) {
                if (!guild.isBlocked()) {
                    MessageUtils.sendErrorMessage(guild.getMessage("SPAM_DETECT"), event.getChannel());
                    guild.addBlocked("Спам командами", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                }
            } else {
                spamMap.put(event.getGuild().getId(), messages + 1);
            }
        } else {
            spamMap.put(event.getGuild().getId(), 1);
        }
    }

    private void dispatchCommand(Command cmd, String[] args, GuildMessageReceivedEvent event, GuildWrapper guild) {
        COMMAND_POOL.submit(() -> {
            Map<String, String> mdcContext = (MDC.getCopyOfContextMap() == null ? new HashMap<>() : MDC.getCopyOfContextMap());
            mdcContext.put("command", cmd.getCommand());
            mdcContext.put("args", Arrays.toString(args).replace("\n", "\\n"));
            mdcContext.put("guild", event.getGuild().getId());
            mdcContext.put("user", event.getAuthor().getId());
            MDC.setContextMap(mdcContext);

            getLogger().info(
                    "Использование команды '" + cmd.getCommand() + "' " + Arrays
                            .toString(args).replace("\n", "\\n") + " в " + event.getChannel() + "! Отправитель: " +
                            event.getAuthor().getName() + '#' + event.getAuthor().getDiscriminator());
            commandCounter.incrementAndGet();
            try {
                CommandEvent commandEvent = new CommandEvent(cmd,event.getAuthor(), guild, event.getChannel(), event.getMessage(), args, event
                        .getMember());
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
                        + Arrays.toString(args) + " в " + event.getChannel() + "! Отправитель: " +
                        event.getAuthor().getName() + '#' + event.getAuthor().getDiscriminator(), ex);
            }

            if (cmd.deleteMessage()) {
                delete(event.getMessage());
                removedByMe.add(event.getMessageIdLong());
            }
        });
    }

    private void delete(Message message) {
        if(message.getTextChannel().getGuild().getSelfMember()
                    .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE))
                message.delete().queue();
    }



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = multiSpace.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        if (message.startsWith("" + JustBotManager.instance().getGuild(getGuildId(event)).getPrefix())) {
            EnumSet<Permission> perms = event.getChannel().getGuild().getSelfMember().getPermissions(event.getChannel());
            if (!perms.contains(Permission.ADMINISTRATOR)) {
                if (!perms.contains(Permission.MESSAGE_WRITE)) {
                    return;
                }
                if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
                    event.getChannel().sendMessage("Я не могу приклепать." +
                            "\nДай пожалуйста мне права `." +
                            "\nСпасибо :D").queue();
                    return;
                }
            }

            String command = message.substring(1);
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, message.indexOf(" ") - 1);
                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            Command cmd = this.getCommand(command, event.getAuthor());
            if (cmd != null)
                handleCommand(event, cmd, args);
        }else{
            if (message.startsWith("_prefix") || message.replace("!","").startsWith(event.getGuild().getSelfMember().getAsMention())) {
                event.getChannel().sendMessage(MessageUtils.getEmbed(event.getAuthor())
                        .setDescription("Префикс на сервере `" + JustBotManager
                                .instance().getGuild(getGuildId(event)).getPrefix() + "`")
                        .build()).queue();
            }
        }
    }
}
