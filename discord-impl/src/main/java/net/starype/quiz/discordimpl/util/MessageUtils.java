package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LogContainer;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        channel.getHook()
                .editOriginal(value)
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
    }

    public static void createTemporaryMessage(String value, MessageChannel channel) {
        channel.sendMessage(value)
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
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

    public static void sendAndTrack(MessageEmbed embed, CommandInteraction interaction, GameLobby lobby) {
        interaction.getHook().editOriginalEmbeds(embed)
                .map(Message::getId)
                .queue(lobby::trackMessage);
    }
}
