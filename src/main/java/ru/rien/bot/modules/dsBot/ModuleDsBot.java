package ru.rien.bot.modules.dsBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.JDAMultiShard;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.libraries.LibraryFactory;
import ru.rien.bot.api.music.libraries.UnknownBindingException;
import ru.rien.bot.module.CommonModule;
import ru.rien.bot.music.QueueListener;
import ru.rien.bot.objects.Getters;
import ru.rien.bot.services.GuildService;
import ru.rien.bot.utils.web.DataInterceptor;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

@Component
public class ModuleDsBot extends CommonModule {

    public ModuleDsBot() {
        super("dsbot", true);
        instance = this;
    }

    private JDA jda;
    private JustBotManager manager;
    private static ModuleDsBot instance;
    private PlayerManager musicManager;

    public static ModuleDsBot getInstance() {
        return instance;
    }

    private static final DataInterceptor dataInterceptor = new DataInterceptor(DataInterceptor.RequestSender.JDA);
    private static OkHttpClient client =
            new OkHttpClient.Builder().connectionPool(new ConnectionPool(4, 10, TimeUnit.SECONDS))
                    .addInterceptor(dataInterceptor).build();

    @Autowired
    private GuildService guildService;

    //test token  ODA5MDUzODI3MTUxNjkxODA4.YCPgFw.xBiasLgkdRtxL-Lm9cUeYsZlKo0
    @Override
    protected void onEnable() {
        JDABuilder builder = JDABuilder.createDefault(getConfig().getOrSet("ds.token1","Nzk4ODQ0MDQ0MDEyMTU5MDA2.X_67fQ.adMhtrAhzRKmF50mO2xvG4FEaC4"));
        manager = new JustBotManager(this, guildService);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("_prefix"));

        builder.setLargeThreshold(50);
        try {
            jda = builder.build().awaitReady();
            musicManager = PlayerManager.getPlayerManager(LibraryFactory.getLibrary(new JDAMultiShard(Getters.getShardManager())));
            musicManager.getPlayerCreateHooks()
                    .register(player -> player.getQueueHookManager().register(new QueueListener()));
            musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new PlayerListener(player)));
            manager.executeCreations();
        } catch (LoginException | InterruptedException | UnknownBindingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDisable() {
        manager.getGuilds().asMap().forEach((s, wrapper) ->{
            System.out.println(wrapper.getGuild().getName());
            manager.saveGuild(s,wrapper);
        });
    }

    public PlayerManager getMusicManager() {
        return musicManager;
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public GuildService getGuildService() {
        return guildService;
    }

    public JustBotManager getManager() {
        return manager;
    }

    public JDA getJda() {
        return jda;
    }
}
