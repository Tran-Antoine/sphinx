package net.starype.quiz.discordimpl.util;

import com.neovisionaries.ws.client.ProxySettings;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.ByteEntryUpdater;
import net.starype.quiz.api.database.EntryUpdater;

import java.io.*;
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

    private static final long maxFileSize = 1024;

    private static final CounterLimiter downloadingLimiter = new CounterLimiter(5, 5.);

    public static Collection<? extends EntryUpdater> loadEntryUpdaters(String urlName, TextChannel channel) {
        Set<EntryUpdater> updaters = new HashSet<>();

        try {
            URL url = new URL(urlName);
            if(!downloadingLimiter.acquireInstance(urlName.hashCode())) {
                channel.sendMessage("Error: The limit of downloading zip as been reached").queue();
                return updaters;
            }

            ZipInputStream zipStream = new ZipInputStream(url.openStream());

            ZipEntry current;
            while ((current = zipStream.getNextEntry()) != null) {
                readEntry(zipStream, current, updaters);
            }
        } catch (IOException ignored) {
            channel.sendMessage("Error: couldn't load the provided zip archive").queue();
        }

        // Release the instance of the current thread (as we finished the download process)
        downloadingLimiter.releaseInstance(urlName.hashCode());
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
