package net.starpye.quiz.discordimpl.input;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.server.GameServer;

public class DiscordGameServer implements GameServer {

    private Guild guild;
    private Snowflake channelID;

    public DiscordGameServer(Guild guild, Snowflake channelID) {
        this.guild = guild;
        this.channelID = channelID;
    }

    @Override
    public void onQuestionReleased(Question question) {
        getChannel().createMessage(question.getRawQuestion()).block();
    }

    @Override
    public void onRoundEnded(GameRoundReport report, QuizGame game) {
        TextChannel channel = getChannel();
        channel.createMessage("Round ended").block();
        game.nextRound();
    }

    @Override
    public void onGameOver() {
        TextChannel channel = getChannel();
        channel.createMessage("Game over").block();
    }

    @Override
    public void onPlayerGuessed(PlayerGuessContext context) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(context.getPlayer().getUUID()).append(" sent a guess\n")
                .append("Guess accuracy: ").append(context.getCorrectness())
                .append("\nPlayer might try to guess again : ").append(context.isEligible());
        getChannel().createMessage(builder.toString()).block();
    }

    @Override
    public void onNonEligiblePlayerGuessed(UUIDHolder player) {
        getChannel().createMessage(
                "Player with ID " + player.getUUID()+" : you may not participate in this round anymore");
    }

    @Override
    public void onPlayerGaveUp(UUIDHolder player) {
        getChannel().createMessage("Player with ID " + player.getUUID()+" gave up on this round");
    }

    @Override
    public void onPlayerScoreUpdated(Player player) {
        getChannel().createMessage(
                "Score update for player with ID " + player.getUUID() + ": " + player.getScore());
    }

    private TextChannel getChannel() {
        TextChannel channel = guild.getChannelById(channelID).cast(TextChannel.class).block();
        if(channel == null) {
            throw new IllegalStateException("Couldn't find channel");
        }
        return channel;
    }
}
