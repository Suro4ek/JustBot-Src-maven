package ru.rien.bot.music;



import ru.rien.bot.api.music.hooks.QueueHook;
import ru.rien.bot.api.music.player.Item;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Playlist;
import ru.rien.bot.api.music.player.Track;

import java.util.ArrayList;
import java.util.List;

public class QueueListener implements QueueHook {

    @Override
    public void execute(Player player, Item item) {
        List<Track> tracks = new ArrayList<>();
        if (item instanceof Playlist) {
            tracks.addAll(((Playlist) item).getPlaylist());
        } else {
            tracks.add((Track) item);
        }

//        sendQueueData(tracks);
    }

//    private void sendQueueData(List<Track> tracks) {
//        JsonArray array = new JsonArray();
//        for (Track t : tracks) {
//            JsonObject object = new JsonObject();
//            object.addProperty("title", t.getTrack().getInfo().title);
//            object.addProperty("id", t.getTrack().getIdentifier());
//            array.add(object);
//        }

        //FlareBot.getInstance().postToApi("updatePlaylistData", "playlist", array);
}