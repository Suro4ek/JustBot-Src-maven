package ru.rien.bot.module;

import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.rien.bot.JustBotApplication;
import ru.rien.bot.utils.Config;

import java.io.File;

public abstract class CommonModule implements InitializingBean, DisposableBean {

    private String name;
    private Config config;
    private Logger logger = JustBotApplication.getLog();

    public CommonModule(String name, boolean config){
        this.name = name;
        if(config){
          this.config = new Config(new File(this.getDataFolder(), generateNameConfig(name)+".yml"));
        }
    }

    public Config getConfig() {
        return config;
    }

    public File getDataFolder() {
        File file = new File( "."+File.separator + "DiscordBot");
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public static String generateNameConfig(String nameModule) {
        final char[] chars = nameModule.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                if (i == 0 || (Character.isUpperCase(chars[i - 1]) && (i == chars.length - 1 || Character.isUpperCase(chars[i + 1])))) {
                    builder.append(Character.toLowerCase(chars[i]));
                } else {
                    builder.append('-').append(Character.toLowerCase(chars[i]));
                }
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }


    public Logger getLogger() {
        return logger;
    }

    @Override
    public void destroy() throws Exception {
        getLogger().info("Выключение модуля "+name);
        onDisable();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onEnable();
    }

    protected void onFirstEnable() {
    }

    protected void onAfterEnable() {
    }

    protected abstract void onEnable();

    protected void onDisable() {

    }
}
