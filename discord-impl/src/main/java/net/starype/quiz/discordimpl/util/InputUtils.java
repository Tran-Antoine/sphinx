package net.starype.quiz.discordimpl.util;

import com.neovisionaries.ws.client.ProxySettings;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.ByteEntryUpdater;
import net.starype.quiz.api.database.EntryUpdater;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputUtils {

    public static final int maxDownloadSize = 1048576;

    public static long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return maxDownloadSize + 1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static Collection<? extends EntryUpdater> loadEntryUpdaters(String urlName, TextChannel channel, DiscordContext.CounterLimiter limiter) {
        Set<EntryUpdater> updaters = new HashSet<>();

        try {
            URL url = new URL(urlName);
            if(!limiter.acquireInstance(Thread.currentThread().getId())) {
                channel.sendMessage("Error: The limit of downloading zip as been reached").queue();
                return updaters;
            }
            if(getFileSize(url) > maxDownloadSize) {
                channel.sendMessage("Error: The download limit has been reached. Cannot download file over than " + maxDownloadSize + " bytes").queue();
                return updaters;
            }

            InputStream fileStream = new BufferedInputStream(url.openStream(), 1024);
            ZipInputStream zipStream = new ZipInputStream(fileStream);

            ZipEntry current;
            while ((current = zipStream.getNextEntry()) != null) {
                readEntry(zipStream, current, updaters);
            }

        } catch (IOException ignored) {
            channel.sendMessage("Error: couldn't load the provided zip archive").queue();
        }

        return updaters;
    }

    private static void readEntry(ZipInputStream zipStream, ZipEntry current, Set<EntryUpdater> updaters) throws IOException {

        long size = current.getSize();

        if(size == 0 || size >= 10E6) {
            return;
        }

        byte[] fileData = zipStream.readAllBytes();
        String virtualPath = current.getName();
        updaters.add(new ByteEntryUpdater(virtualPath, fileData));
    }
}
