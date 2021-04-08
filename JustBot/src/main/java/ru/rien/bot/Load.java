package ru.rien.bot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Load {

    public void onLoad() {
        int count = 0;
        File library = new File("." + File.separator + "library");
        if (!library.exists())
            library.mkdir();
        for (File file : library.listFiles((dir, name) -> name.endsWith(".jar"))) {
            try {
                long start = System.currentTimeMillis();
                loadLib(file);
                JustBotApplication.log.info("Библиотека '" + file.getName() + "' была загружено за " + (System.currentTimeMillis() - start) + "ms.");
                count++;
            } catch (Exception e) {
                JustBotApplication.log.info("Ошибка загрузки библиотеки '" + file.getName() + "'.");
                e.printStackTrace();
            }
        }
        JustBotApplication.log.info("Была загружено " + count + " библиотек");
    }
    private  void loadLib(File file) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        URLClassLoader loader = (URLClassLoader)getClass().getClassLoader();
        Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrl.setAccessible(true);
        addUrl.invoke(loader, file.toURI().toURL());
        addUrl.setAccessible(false);
    }

}
