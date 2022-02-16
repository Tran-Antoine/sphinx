package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.game.QuizTimer;

import java.util.concurrent.TimeUnit;

public class DiscordQuizTimer extends QuizTimer {

    private final TextChannel channel;
    private boolean sent = false;

    public DiscordQuizTimer(TimeUnit unit, long time, TextChannel channel) {
        this(unit, time, time, channel);
    }

    public DiscordQuizTimer(TimeUnit unit, long time, long shortenedTime, TextChannel channel) {
        super(unit, time, shortenedTime);
        this.channel = channel;
    }

    @Override
    public void update(long deltaMillis) {
        double left = millisLeft() / 1000.0;
        if(left <= 30 && !sent) {
            channel.sendMessage("> :warning: 20 seconds left!").queue(null, null);
            sent = true;
        }
        super.update(deltaMillis);
    }
}
