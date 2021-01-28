package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import net.starype.quiz.api.game.answer.*;
import net.starype.quiz.api.game.question.DefaultQuestion;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.game.question.QuestionTag;
import net.starype.quiz.api.util.RandomComparator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WikipediaQuestionLoader {
    private static final String prefix = "https://wiki-quiz.herokuapp.com/v1/quiz?";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static String getPathWith(String topics) {
        return prefix + "topics=" + httpEncode(topics);
    }

    private static String httpEncode(String topics) {
        return topics.replace(" ", "%20")
                .replace("!", "%21")
                .replace("\"", "%22")
                .replace("#", " %23")
                .replace("$", "%24")
                .replace("%", "%25")
                .replace("&", "%26")
                .replace("'", "%27")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("*", "%2A")
                .replace("+", "%2B")
                .replace(",", "%2C")
                .replace("-", "%2D")
                .replace(".", "%2E")
                .replace("/", "%2F");
    }

    private static Optional<String> requestGET(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Now it's "open", we can set the request method, headers etc.
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            InputStream responseStream = connection.getInputStream();
            String data = new String(responseStream.readAllBytes());
            responseStream.close();
            return Optional.of(data);
        } catch (IOException ioException) {
            return Optional.empty();
        }
    }

    public static Optional<List<Question>> queryAll(String topics) {
        Optional<String> s = requestGET(getPathWith(topics));
        if(s.isEmpty())
            return Optional.empty();

        // Retrieve the data
        String data = s.get();

        // Parse it to json
        ConfigParser<Config> parser = JsonFormat.fancyInstance().createParser();
        Config parsedData = parser.parse(data);

        // For each of the question
        return Optional.of(parsedData.<List<Config>>get("quiz")
                .stream()
                .filter(config -> config.get("type").equals("mcq"))
                .map(config -> createQuestion(config, topics))
                .collect(Collectors.toList()));
    }

    private static Question createQuestion(Config config, String topics) {
        // Retrieve the question
        String rawQuestion = config.<String>get("question").toLowerCase();

        // Retrieve raw Options
        List<String> rawOptions = config.<List<String>>get("options")
                .stream()
                .sorted(new RandomComparator<>())
                .collect(Collectors.toList());

        try {
            // Retrieve all options (parsed)
            List<String> options = IntStream.range(0, rawOptions.size())
                    .mapToObj(id -> "Options " + ALPHABET.charAt(id) + ": " + rawOptions.get(id))
                    .collect(Collectors.toList());

            // Retrieve the question
            String question = rawQuestion + "\n" + options.stream().reduce((s1, s2) -> s1 + "\n" + s2).orElseThrow();
            String correctAnswer = config.get("answer");

            AnswerProcessor processor = new CleanStringProcessor()
                    .combine(answer -> Answer.fromString(answer.getAnswerText().toUpperCase()))
                    .combine(answer -> Answer.fromString(answer.getAnswerText().replace("Option", "")
                            .replace(" ", "")));

            MCQAnswerFactory factory = new MCQAnswerFactory()
                    .withPunitiveRatio(0.0F)
                    .withInterpolation(new BinaryLossFunction());

            int correctAnswerId = IntStream.range(0,rawOptions.size())
                    .filter(id -> rawOptions.get(id).equals(correctAnswer))
                    .findAny().getAsInt();
            String correctAnswerLetter = String.valueOf(ALPHABET.charAt(correctAnswerId));
            AnswerEvaluator answerEvaluator = factory
                    .createCorrectAnswer(Set.of(Answer.fromString(correctAnswerLetter)), processor);

            return new DefaultQuestion.Builder()
                    .withDifficulty(QuestionDifficulty.HARD)
                    .withTags(Set.of(new QuestionTag(topics)))
                    .withRawAnswer("Options " + correctAnswerLetter)
                    .withRawText(question)
                    .withAnswerEvaluator(answerEvaluator)
                    .build();
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Doesn't support more than 24 possible answers");
        }
    }
}