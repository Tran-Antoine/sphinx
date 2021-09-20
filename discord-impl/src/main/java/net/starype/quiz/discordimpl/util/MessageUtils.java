package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
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

    public static void sendAndTrack(String text, CommandInteraction channel, LogContainer container) {
        channel.getHook().editOriginal(text)
                .map(Message::getId)
                .queue(container::trackMessage, null);
    }

    public static void sendAndTrack(String text, MessageChannel channel, LogContainer container) {
        channel.sendMessage(text)
                .map(Message::getId)
                .queue(container::trackMessage, null);
    }

    public static void sendAndTrack(InputStream image, String name, CommandInteraction channel, LogContainer container) {
        channel.getHook()
                .editOriginal(image, name)
                .queue(message -> container.trackMessage(message.getId()), null);
    }

    public static void sendAndTrack(InputStream image, String name, MessageChannel channel, LogContainer container) {
        channel
                .sendFile(image, name)
                .queue(message -> container.trackMessage(message.getId()), null);
    }

    public static void createTemporaryMessage(String value, CommandInteraction channel) {
        Message message = channel.getHook().editOriginal(value).complete();
        if(message == null) {
            return;
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message.getId(), channel), 5, TimeUnit.SECONDS);
    }

    public static void createTemporaryMessage(String value, MessageChannel channel) {
        Message message = channel.sendMessage(value).complete();
        if(message == null) {
            return;
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message.getId(), channel), 5, TimeUnit.SECONDS);
    }

    public static void makeTemporary(CommandInteraction channel, Message message) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> delete(message.getId(), channel), 5, TimeUnit.SECONDS);
    }

    private static void delete(String messageId, CommandInteraction channel) {
        channel.getChannel()
                .retrieveMessageById(messageId)
                .flatMap(Message::delete)
                .queue();
    }

    private static void delete(String messageId, MessageChannel channel) {
        channel
                .retrieveMessageById(messageId)
                .flatMap(Message::delete)
                .queue();
    }
}
