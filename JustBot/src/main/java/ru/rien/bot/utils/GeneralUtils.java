package ru.rien.bot.utils;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.dsBot.ModuleDsBot;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class GeneralUtils {

    private static final DecimalFormat percentageFormat = new DecimalFormat("#.##");
    /**
     * Resolves an {@link AudioItem} from a string.
     * This can be a url or search terms
     *
     * @param player The music player
     * @param input  The string to get the AudioItem from.
     * @return {@link AudioItem} from the string.
     * @throws IllegalArgumentException If the Item couldn't be found due to it not existing on Youtube.
     * @throws IllegalStateException    If the Video is unavailable for Flare, for example if it was published by VEVO.
     */
    public static AudioItem resolveItem(Player player, String input) throws IllegalArgumentException, IllegalStateException {
        Optional<AudioItem> item = Optional.empty();
        boolean failed = false;
        int backoff = 2;
        Throwable cause = null;
        for (int i = 0; i <= 2; i++) {
            try {
                item = Optional.ofNullable(player.resolve(input));
                failed = false;
                break;
            } catch (FriendlyException | InterruptedException | ExecutionException e) {
                failed = true;
                cause = e;
                if (e.getMessage().contains("Vevo")) {
                    throw new IllegalStateException(Jsoup.clean(cause.getMessage(), Whitelist.none()), cause);
                }
                ModuleDsBot.getInstance().getLogger().error("Cannot get video '" + input + "'");
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ignored) {
                }
                backoff ^= 2;
            }
        }
        if (failed) {
            throw new IllegalStateException(Jsoup.clean(cause.getMessage(), Whitelist.none()), cause);
        } else if (!item.isPresent()) {
            throw new IllegalArgumentException();
        }
        return item.get();
    }

    /**
     * Returns a lookup map for an enum, using the passed transform function.
     *
     * @param clazz  The clazz of the enum
     * @param mapper The mapper. Must be bijective as it otherwise overwrites keys/values.
     * @param <T>    the enum type
     * @param <R>    the type of map key
     * @return a map with the given key and the enum value associated with it
     * @apiNote Thanks to I Al Istannen#1564 for this
     */
    public static <T extends Enum, R> Map<R, T> getReverseMapping(Class<T> clazz, Function<T, R> mapper) {
        Map<R, T> result = new HashMap<>();

        for (T t : clazz.getEnumConstants()) {
            result.put(mapper.apply(t), t);
        }

        return result;
    }

    /**
     * Gets the progress bar for the current {@link Track} including the percent played.
     *
     * @param track The {@link Track} to get a progress bar for.
     * @return A string the represents a progress bar that represents the time played.
     */
    public static String getProgressBar(Track track) {
        float percentage = (100f / track.getTrack().getDuration() * track.getTrack().getPosition());
        return "[" + StringUtils.repeat("▬", (int) Math.round((double) percentage / 10)) +
                "]" +
                StringUtils.repeat("▬", 10 - (int) Math.round((double) percentage / 10)) +
                " " + GeneralUtils.percentageFormat.format(percentage) + "%";
    }


    /**
     * Orders a Collection alphabetic by whatever {@link String#valueOf(Object)} returns.
     *
     * @param strings The Collection to order.
     * @return The ordered List.
     */
    public static <T extends Comparable> List<T> orderList(Collection<? extends T> strings) {
        List<T> list = new ArrayList<>(strings);
        list.sort(Comparable::compareTo);
        return list;
    }

    /**
     * Gets an int from a String.
     * The default value you pass is what it return if their was an error parsing the string.
     * This happens when the string you enter isn't an int. For example if you enter in 'no'.
     *
     * @param s            The string to parse an int from.
     * @param defaultValue The default int value to get in case parsing fails.
     * @return The int parsed from the string or the default value.
     */
    public static int getInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
