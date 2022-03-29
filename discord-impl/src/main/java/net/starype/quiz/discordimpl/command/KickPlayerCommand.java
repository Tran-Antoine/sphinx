package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class KickPlayerCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        TextChannel channel = context.getChannel();
        GameList gameList = context.getGameList();
        String[] args = context.getArgs();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, author, args, channel);
        if(StopConditions.shouldStop(conditions, channel, context.getMessage())) {
            return;
        }

        User target = channel.getGuild().getMemberByTag(args[0]).getUser();
        DiscordQuizGame game = gameList.getFromPlayer(author).get();
        game.removePlayer(target.getId());
        game.checkEndOfRound();

        channel.sendMessage("Player successfully excluded from the game")
                .map(Message::getId)
                .queue(game::addLog, null);
    }


    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "kick a player from a game";
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList,
                                                                       Member author, String[] args, TextChannel channel) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> gameList.getFromPlayer(author).isEmpty(),
                "You must be in a game to use this");
        conditions.put(
                () -> !gameList.getFromPlayer(author).get().isAuthor(author.getId()),
                "You must be the other of the game to use this");
        conditions.put(
                () -> args.length != 1,
                "You need to tag the member you want to kick");

        conditions.put(
                () -> channel.getGuild().getMemberByTag(args[0]) == null,
                "Invalid selected member");
        return conditions;
    }
}
