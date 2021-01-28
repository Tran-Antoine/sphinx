package net.starype.quiz.api.parser;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.TrackedDatabase.UpdatableEntry;
import net.starype.quiz.api.util.RandomComparator;
import net.starype.quiz.api.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleQuestionDatabase implements QuestionDatabase {

    private TrackedDatabase database;

    public final static DBTable TABLE = new DBTable.Builder()
                    .registerIndexedArguments("text")
                    .registerIndexedArguments("difficulty")
                    .registerIndexedArguments("tags")
                    .registerArgument("processors")
                    .registerArgument("answers")
                    .registerArgument("evaluator")
                    .registerArgument("processors")
                    .create();

    public SimpleQuestionDatabase(List<? extends UpdatableEntry> updaters, SerializedIO serializedIO, boolean standAlone) {
        this.database = new TrackedDatabase.Builder()
                .setTable(TABLE)
                .setTrackedFiles(updaters)
                .setIO(serializedIO)
                .setStandalone(standAlone)
                .create();
    }

    public void sync() {
        database.sync();
    }

    private boolean getQuery(QuestionQuery queryMatcher, Set<? extends ArgumentValue<String>> argumentValues) {
        Map<String,String> map = argumentValues.stream()
                .collect(Collectors.toMap(ArgumentValue::getName, ArgumentValue::getValue));
        return queryMatcher.apply(new QuestionQuery.QueryData(new HashSet<>(StringUtils.unpack(map.get("tags"))),
                map.get("text"), map.get("difficulty"), map.get("file")));
    }

    @Override
    public List<Question> randomizedQuery(QuestionQuery queryMatcher, int maxCount) {
        return database.query(argumentValues -> getQuery(queryMatcher, argumentValues))
                .stream()
                .sorted(new RandomComparator<>())
                .map(s -> QuestionParser.parseRaw(
                        s.get("difficulty").orElse("NORMAL"),
                        s.get("tags").orElse(""),
                        s.get("text").orElse(""),
                        s.get("answers").orElse(""),
                        s.get("evaluator").orElse(""),
                        s.get("processors").orElse("")
                ))
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
