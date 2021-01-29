package net.starpye.quiz.discordimpl.command;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class StopConditions {

    public static boolean shouldStop(Map<Supplier<Boolean>, String> stopConditions, TextChannel channel, Message original) {
        for(Entry<Supplier<Boolean>, String> entry : stopConditions.entrySet()) {
            if(entry.getKey().get()) {
                createTemporaryMessage(channel, entry.getValue(), original);
                return true;
            }
        }
        return false;
    }

    private static void createTemporaryMessage(TextChannel channel, String value, Message original) {
        Optional<Message> optMessage = channel.createMessage(value).blockOptional();
        if(optMessage.isEmpty()) {
            return;
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> { delete(original); delete(optMessage.get()); }, 5, TimeUnit.SECONDS);
    }

    private static void delete(Message message) {
        message.delete().subscribe();
    }
}
