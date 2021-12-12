package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.util.Map;
import java.util.Map.Entry;
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
        MessageUtils.createTemporaryMessage(value, channel);
        MessageUtils.makeTemporary(channel, original);
    }
}
