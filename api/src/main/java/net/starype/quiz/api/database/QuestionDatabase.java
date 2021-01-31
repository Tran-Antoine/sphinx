package net.starype.quiz.api.database;

import net.starype.quiz.api.database.QuestionQuery.QueryData;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.QuestionParser;
import net.starype.quiz.api.util.RandomComparator;
import net.starype.quiz.api.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class QuestionDatabase extends TrackedDatabase implements QuizQueryable {

    public final static DatabaseTable TABLE = new DatabaseTable.Builder()
                    .registerIndexedArguments("text")
                    .registerIndexedArguments("difficulty")
                    .registerIndexedArguments("tags")
                    .registerArgument("processors")
                    .registerArgument("answers")
                    .registerArgument("evaluator")
                    .registerArgument("processors")
                    .create();

    public QuestionDatabase(Collection<? extends EntryUpdater> updaters, SerializedIO serializedIO, boolean standalone) {
        super(updaters, serializedIO, TABLE, standalone);
    }

    private boolean getQuery(QuestionQuery queryMatcher, Map<String, String> map) {
        return queryMatcher.apply(new QueryData(
                new HashSet<>(StringUtils.unpack(map.get("tags"))),
                map.get("text"),
                map.get("difficulty"),
                map.get("file"))
        );
    }

    @Override
    public List<Question> randomizedQuery(QuestionQuery queryMatcher, int maxCount) {
        return query(argumentValues -> getQuery(queryMatcher, argumentValues))
                .stream()
                .sorted(new RandomComparator<>())
                .map(QuestionParser::parse)
                .limit(maxCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> listQuery(QuestionQuery queryMatcher) {
        return randomizedQuery(queryMatcher, Integer.MAX_VALUE);
    }

    @Override
    public Optional<Question> pickQuery(QuestionQuery queryMatched) {
        return randomizedQuery(queryMatched,1).stream().findAny();
    }
}
