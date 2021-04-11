package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.entities.*;
import org.springframework.util.StringUtils;
import ru.rien.bot.objects.GuildWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Выполнение команды
 */
public class CommandEvent {

    private Command command;
    private User sender;
    private GuildWrapper guild;
    private TextChannel channel;
    private Message message;
    private String[] args;
    private Member member;

    public CommandEvent(Command command,User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        this.command = command;
        this.sender = sender;
        this.guild = guild;
        this.channel = channel;
        this.message = message;
        this.args = args;
        this.member = member;
    }

    public Command getCommand() {
        return command;
    }

    public User getSender() {
        return sender;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    public GuildWrapper getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public boolean isFromType(@Nonnull ChannelType type) {
        return this.channel.getType() == type;
    }


    public String arg(int index) {
        this.checkSizeArguments(index + 1);
        return args[index];
    }

    public String[] getArgs() {
        return args;
    }

    public int getInt(String arg) throws CommandException {
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * Является ли аргумент по индексу name
     *
     * @param index индект аргумента
     * @param name  имя ignore case
     * @return true, если да
     */
    public boolean has(int index, String name) {
        this.checkSizeArguments(index + 1);
        return args[index].equalsIgnoreCase(name);
    }


    public void argsToLowerCase0() {
        argsToLowerCase(0);
    }


    public void argsToLowerCase(int i) {
        try {
            this.args[i] = this.args[i].toLowerCase();
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    }
    /**
     * Является ли аргумент по индексу name
     *
     * @param index     индект аргумента
     * @param variables имя ignore case
     * @return true, если да
     */
    public boolean has(int index, String... variables) {
        this.checkSizeArguments(index + 1);
        return Stream.of(variables).anyMatch(variable -> variable.equalsIgnoreCase(args[index]));
    }

    /**
     * @return Выполнена ли команда без аргументов
     */
    public boolean isEmpty() {
        return args.length == 0;
    }

    public int size() {
        return args.length;
    }

    public boolean hasSize(int size) {
        return this.size() == size;
    }

    private String getDisplay(double d) {
        return Double.compare(d, (int) d) == 0 ? String.valueOf((int) d) : String.valueOf(d);
    }


    /**
     * Сколько минимум должно быть аргументов
     *
     * @param min минимум аргументов
     * @
     */
    public void checkSizeArguments(int min) throws CommandException {
        if (args.length < min) {
            String message = guild.getMessage("ERROR_ARGUMENTS");
            message = StringUtils.replace(message, "%usage%", command.getDescription(guild));
            message = StringUtils.replace(message, "%cmd%", guild.getPrefix()+command.getCommand());
            this.message.reply(message).queue();
            throw new CommandException();
        }
    }


    /**
     * Объеденить аргументы в строку через пробел
     *
     * @param start первый аргумент
     * @return
     */
    public String getArguments(int start) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            s.append(i == start ? args[i] : ' ' + args[i]);
        }
        return s.toString();
    }

    /**
     * Объеденить аргументы в строку через пробел
     *
     * @param start первый аргумент
     * @return args
     */
    public String getArguments(int start, String[] args) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            s.append(i == start ? args[i] : ' ' + args[i]);
        }
        return s.toString();
    }

    /**
     * Объеденить аргументы в строку через пробел
     *
     * @param start первый аргумент
     * @param end последний аргумент
     * @return
     */
    public String getArguments(int start, int end) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < end; i++) {
            s.append(i == start ? args[i] : ' ' + args[i]);
        }
        return s.toString();
    }



    private static Map<Character, Long> timeValue = new HashMap<>();
    private static List<Character> numbers = Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );

    static {
        timeValue.put('г', 60L * 60 * 24 * 30 * 12);
        timeValue.put('Г', 60L * 60 * 24 * 30 * 12);
        timeValue.put('y', 60L * 60 * 24 * 30 * 12);
        timeValue.put('Y', 60L * 60 * 24 * 30 * 12);

        timeValue.put('М', 60L * 60 * 24 * 30);
        timeValue.put('M', 60L * 60 * 24 * 30);

        timeValue.put('д', 60L * 60 * 24);
        timeValue.put('Д', 60L * 60 * 24);
        timeValue.put('d', 60L * 60 * 24);
        timeValue.put('D', 60L * 60 * 24);

        timeValue.put('ч', 60L * 60);
        timeValue.put('Ч', 60L * 60);
        timeValue.put('h', 60L * 60);
        timeValue.put('H', 60L * 60);

        timeValue.put('м', 60L);
        timeValue.put('m', 60L);

        timeValue.put('с', 1L);
        timeValue.put('С', 1L);
        timeValue.put('s', 1L);
        timeValue.put('S', 1L);
    }

    public <T extends Enum> List<String> getEnumNames(T[] values) {
        return Stream.of(values).map(Enum::name).collect(Collectors.toList());
    }


}
