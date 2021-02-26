
package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.*;
import net.starype.quiz.discordimpl.util.DiscordContext;
import net.starype.quiz.discordimpl.util.InputUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class GenerateDatabaseCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        Message message = context.getMessage();
        Collection<Attachment> files = message.getAttachments();
        Map<Supplier<Boolean>, String> conditions = createStopConditions(message);
        TextChannel channel = context.getChannel();

        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        String[] args = context.getArgs();
        String fileName = args.length == 2
                ? args[1]
                : "database";

        Optional<InputStream> database = generateFile(files.iterator().next().getUrl(), channel, context.getDiscordContext());
        if(database.isEmpty()) {
            return;
        }
        channel
                .sendMessage("Here is your output!")
                .addFile(database.get(), fileName + ".sphinx")
                .queue();
    }

    private static Optional<InputStream> generateFile(String urlName, TextChannel channel, DiscordContext discordContext) {
        Collection<? extends EntryUpdater> updaters = InputUtils.loadEntryUpdaters(urlName, channel, discordContext.downloadingLimiter());
        AtomicReference<ByteBuffer> output = new AtomicReference<>();
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], output);
        TrackedDatabase db = new QuestionDatabase(updaters, serializedIO, false);
        try {
            db.sync();
        } catch(RuntimeException ignored) {
            channel.sendMessage("Error: couldn't parse the given zip archive").queue();
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
