package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashSet;
import java.util.Set;

public class DiscordLogContainer implements LogContainer {

    private final Set<String> logs;
    private TextChannel channel;

    public DiscordLogContainer(TextChannel channel) {
        this.channel = channel;
        this.logs = new HashSet<>();
    }

    @Override
    public void trackMessage(String id) {
        logs.add(id);
    }

    @Override
    public void deleteMessages() {
        synchronized (this) {
            logs.forEach(this::deleteLog);
            logs.clear();
        }
    }

    private void deleteLog(String id) {
        channel.retrieveMessageById(id).flatMap(Message::delete).queue();
    }
}
