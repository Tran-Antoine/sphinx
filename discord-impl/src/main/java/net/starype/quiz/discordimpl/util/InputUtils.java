package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.starype.quiz.api.database.ByteEntryUpdater;
import net.starype.quiz.api.database.EntryUpdater;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputUtils {

    private static final CounterLimiter downloadingLimiter = new CounterLimiter(5);

    public static Collection<? extends EntryUpdater> loadEntryUpdaters(String urlName, CommandInteraction interaction) {
        Set<EntryUpdater> updaters = new HashSet<>();

        try {
            URL url = new URL(urlName);
            if(!downloadingLimiter.register(urlName.hashCode())) {
                interaction.getHook().sendMessage("Error: The limit of downloading zip has been reached").queue();

                return updaters;
            }

            ZipInputStream zipStream = new ZipInputStream(url.openStream());
            ZipEntry current;

            while ((current = zipStream.getNextEntry()) != null) {
                readEntry(zipStream, current, updaters);
            }
        } catch (IOException ignored) {
            interaction.getHook().sendMessage("Error: couldn't load the provided zip archive").queue(null, null);
        }

        // Release the instance of the current thread (as we finished the download process)
        downloadingLimiter.unregister(urlName.hashCode());
        return updaters;
    }

    private static int readEntry(ZipInputStream zipStream, ZipEntry current, Set<EntryUpdater> updaters) throws IOException {

        long size = current.getSize();

        if(size == 0) {
            return 0;
        }

        byte[] fileData = zipStream.readAllBytes();
        String virtualPath = current.getName();
        updaters.add(new ByteEntryUpdater(virtualPath, fileData));
        return fileData.length;
    }
}
