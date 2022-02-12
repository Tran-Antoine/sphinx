package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.ByteEntryUpdater;
import net.starype.quiz.api.database.EntryUpdater;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputUtils {

    private static final long maxFileSize = 1024;

    private static final CounterLimiter downloadingLimiter = new CounterLimiter(5);

    public static Collection<? extends EntryUpdater> loadEntryUpdatersFromUrl(String urlName, TextChannel channel) {

        Collection<? extends EntryUpdater> updaters = new HashSet<>();

        try {
            URL url = new URL(urlName);
            if(!downloadingLimiter.register(urlName.hashCode())) {
                channel.sendMessage("Error: The limit of downloading zip has been reached").queue();
                return updaters;
            }
            updaters = loadEntryUpdaters(url.openStream(), channel);

        } catch (IOException ignored) {
            channel.sendMessage("Error: couldn't load the provided zip archive").queue(null, null);
        }

        // Release the instance of the current thread (as we finished the download process)
        downloadingLimiter.unregister(urlName.hashCode());
        return updaters;
    }

    public static Collection<? extends EntryUpdater> loadEntryUpdatersFromLocalPath(String path, TextChannel channel) {
        try {
            return loadEntryUpdaters(new FileInputStream(path), channel);
        } catch (FileNotFoundException e) {
            channel.sendMessage("Error: local file not found").queue(null, null);
        }
        return Collections.emptySet();
    }

    public static Collection<? extends EntryUpdater> loadEntryUpdaters(InputStream input, TextChannel channel) {
        Set<EntryUpdater> updaters = new HashSet<>();
        try {
            ZipInputStream zipStream = new ZipInputStream(input);
            ZipEntry current;
            while ((current = zipStream.getNextEntry()) != null) {
                readEntry(zipStream, current, updaters);
            }
        } catch (IOException e) {
            channel.sendMessage("Error: couldn't load the provided zip archive").queue(null, null);
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
