
package net.starpye.quiz.discordimpl.command;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import io.netty.buffer.ByteBufInputStream;
import net.starype.quiz.api.database.*;
import scala.util.control.Exception.By;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GenerateDatabaseCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        Set<Attachment> files = context.getMessage().getAttachments();
        Map<Supplier<Boolean>, String> conditions = createStopConditions(context.getMessage());
        TextChannel channel = context.getChannel();

        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        String[] args = context.getArgs();
        String fileName = args.length == 2
                ? args[1]
                : "database";

        Optional<InputStream> database = generateFile(files.iterator().next().getUrl(), channel);
        if(database.isEmpty()) {
            return;
        }
        channel
                .createMessage(spec -> spec
                        .addFile(fileName + ".sphinx", database.get())
                        .setContent("Here is your output!"))
                .subscribe();
    }

    private static Optional<InputStream> generateFile(String urlName, TextChannel channel) {
        Collection<? extends EntryUpdater> updaters = loadEntryUpdaters(urlName, channel);
        AtomicReference<ByteBuffer> output = new AtomicReference<>();
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], output);
        TrackedDatabase db = new QuestionDatabase(updaters, serializedIO, false);
        try {
            db.sync();
        } catch(RuntimeException ignored) {
            channel.createMessage("Error: couldn't parse the given zip archive").subscribe();
            return Optional.empty();
        }
        return Optional.of(new ByteArrayInputStream(output.get().array()));
    }

    private static Collection<? extends EntryUpdater> loadEntryUpdaters(String urlName, TextChannel channel) {

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

    private Map<Supplier<Boolean>, String> createStopConditions(Message message) {
        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> message.getAttachments().size() != 1,
                "Please attach one unique file");
        return conditions;
    }

    @Override
    public String getName() {
        return "gen-db";
    }

    @Override
    public String getDescription() {
        return "Generates a DB file from a zip of questions put into TOML files";
    }
}
