package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.starype.quiz.discordimpl.game.LogContainer;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageUtils {

    public static void sendAndTrack(String text, TextChannel channel, LogContainer container) {
        channel.sendMessage(text)
                .map(Message::getId)
                .queue(container::trackMessage);
    }

    public static void sendAndTrack(InputStream image, String name, TextChannel channel, LogContainer container) {
        channel
                .sendFile(image, name)
                .queue(message -> container.trackMessage(message.getId()));
    }

    public static void createTemporaryMessage(String value, TextChannel channel) {
        Message message = channel.sendMessage(value).complete();
        if(message == null) {
            return;
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message.getId(), channel), 5, TimeUnit.SECONDS);
    }

    public static void makeTemporary(TextChannel channel, Message message) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message.getId(), channel), 5, TimeUnit.SECONDS);
    }

    private static void delete(String messageId, TextChannel channel) {
        channel
                .retrieveMessageById(messageId)
                .map(Message::delete)
                .queue();
    }
}
