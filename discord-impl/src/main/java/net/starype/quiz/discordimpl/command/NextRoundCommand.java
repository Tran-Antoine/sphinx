package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NextRoundCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        GameList gameList = context.getGameList();
        Member player = context.getAuthor();
        TextChannel channel = context.getChannel();
        Message message = context.getMessage();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, player);
        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }


        DiscordQuizGame game = gameList.getFromPlayer(player).get(); // value guaranteed to be present in our case

        game.addLog(message.getId());
        message.addReaction("\uD83D\uDC4D").queue(null, null);
        game.addVote(player.getId(), null);
    }

    public static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, Member player) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> gameList.getFromPlayer(player).isEmpty(),
                "You are not in any game");

        conditions.put(
                () -> !gameList.getFromPlayer(player).get().isWaitingForNextRound(),
                "You can't vote to begin the next round, since the current one is not finished yet");

        return conditions;
    }

    @Override
    public String getName() {
        return "next";
    }

    @Override
    public String getDescription() {
        return "Mention that you are ready for the next round";
    }
}
