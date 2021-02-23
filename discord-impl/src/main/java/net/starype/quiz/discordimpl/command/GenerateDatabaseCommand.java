
package net.starype.quiz.discordimpl.command;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.discordimpl.util.InputUtils;
import net.starype.quiz.api.database.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class GenerateDatabaseCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        Message message = context.getMessage();
        Set<Attachment> files = message.getAttachments();
        Map<Supplier<Boolean>, String> conditions = createStopConditions(message);
        TextChannel channel = context.getChannel();

        if(StopConditions.shouldStop(conditions, channel, message)) {
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
        Collection<? extends EntryUpdater> updaters = InputUtils.loadEntryUpdaters(urlName, channel);
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
        return "Generate a DB file from a zip of questions put into TOML files";
    }
}
