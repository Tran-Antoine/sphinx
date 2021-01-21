package net.starpye.quiz.discordimpl.command;

import discord4j.core.object.entity.channel.TextChannel;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class StopConditions {

    public static boolean shouldStop(Map<Supplier<Boolean>, String> stopConditions, TextChannel channel) {
        for(Entry<Supplier<Boolean>, String> entry : stopConditions.entrySet()) {
            if(entry.getKey().get()) {
                channel.createMessage(entry.getValue()).block();
                return true;
            }
        }
        return false;
    }
}
