package net.starpye.quiz.discordimpl.util;

import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.api.database.ByteEntryUpdater;
import net.starype.quiz.api.database.EntryUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputUtils {

    public static Collection<? extends EntryUpdater> loadEntryUpdaters(String urlName, TextChannel channel) {
        Set<EntryUpdater> updaters = new HashSet<>();

        try {
            URL url = new URL(urlName);
            InputStream fileStream = url.openStream();
            ZipInputStream zipStream = new ZipInputStream(fileStream);


            ZipEntry current;
            while((current = zipStream.getNextEntry()) != null) {
                long size = current.getSize();
                if(size == 0 || size >= 10E6) {
                    continue;
                }
                String virtualPath = current.getName();
                byte[] fileData = new byte[(int) size];
                zipStream.read(fileData);
                updaters.add(new ByteEntryUpdater(virtualPath, fileData));
            }

        } catch (IOException ignored) {
            channel.createMessage("Error: couldn't load the provided zip archive").subscribe();
        }

        return updaters;
    }
}
