package ru.rien.bot.modules.messsage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Language {
    RUSSIAN(0, "Русский", LocaleStorage.RU_LOCALE, "RUSSIA");

    private final int id;
    private final String name;
    private final Message locale;
    private final String headName;


    public List<String> getList(String key, Object... replaced) {
        List<String> list = locale.getListMessages().get(key);

        if (list == null) {
            list = DEFAULT.locale.getListMessages().get(key);

            if (list == null) {
                list = Collections.singletonList(ERROR);

                System.out.println("[Localization] Невозможно найти локализацию с ключом " + key);
            }
        }

        if (replaced.length == 0) {
            return list;
        }

        return format(list, replaced);
    }

    public String getMessage(String key, Object... replaced) {
        String message = locale.getMessages().get(key);

        if (message == null) {
            message = DEFAULT.locale.getMessages().get(key);

            if (message == null) {
                message = ERROR;

                System.out.println("[Localization] Невозможно найти локализацию с ключом " + key);
            }
        }

        if (replaced.length == 0) {
            return message;
        }

        return String.format(message, replaced);
    }

    @Override
    public String toString() {
        return "Language{name=" + name + "}";
    }

    private static List<String> format(List<String> list, Object... objects) {
        String string = String.join("±", list);
        string = String.format(string, objects);
        return Arrays.asList(string.split("±"));
    }

    private static final HashMap<Integer, Language> LOCALES = new HashMap<>();
    private static final Language DEFAULT = RUSSIAN;
    private static final String ERROR = "§cNot found";

    public static Language getLanguage(int id) {
        Language language = LOCALES.get(id);
        if (language != null) {
            return language;
        }

        return DEFAULT;
    }

    public static Language getDefault() {
        return DEFAULT;
    }

    static {
        for (Language locale : values()) {
            LOCALES.put(locale.id, locale);
        }
    }
}