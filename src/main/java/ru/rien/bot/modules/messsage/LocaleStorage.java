package ru.rien.bot.modules.messsage;

import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class LocaleStorage {

    static Message RU_LOCALE = new Message("ru");
//    static Message EN_LOCALE = new Message("en");
//    static Message UA_LOCALE = new Message("ua");

    public static void updateLocales() { //обновление локализаций
        for (Language language : Language.values()) {
            try {
                language.getLocale().loadFromSite();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}