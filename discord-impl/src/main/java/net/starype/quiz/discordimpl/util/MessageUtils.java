package net.starype.quiz.discordimpl.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import net.starype.quiz.discordimpl.game.LogContainer;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MessageUtils {

    public static void sendAndTrack(String text, TextChannel channel, LogContainer container) {
        channel.createMessage(text)
                .map(Message::getId)
                .subscribe(container::trackMessage);
    }

    public static void sendAndTrack(Consumer<MessageCreateSpec> spec, TextChannel channel, LogContainer container) {
        channel.createMessage(spec)
                .map(Message::getId)
                .subscribe(container::trackMessage);
    }

    public static void createTemporaryMessage(String value, TextChannel channel) {
        Optional<Message> optMessage = channel.createMessage(value).blockOptional();
        if(optMessage.isEmpty()) {
            return;
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(optMessage.get(), channel), 5, TimeUnit.SECONDS);
    }

    public static void makeTemporary(TextChannel channel, Message message) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message, channel), 5, TimeUnit.SECONDS);
    }

    private static void delete(Message message, TextChannel channel) {
        boolean stillExists = channel.getMessageById(message.getId())
                .map(Objects::nonNull)
                .blockOptional()
                .orElse(false);
        if(stillExists) {
            message.delete().subscribe();
        }
    }
}
