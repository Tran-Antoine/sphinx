package net.starype.quiz.api.reader;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.parser.*;
import net.starype.quiz.api.parser.TrackedDatabase.UpdatableEntry;
import net.starype.quiz.api.util.CheckSum;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleQuestionDatabaseTest {
    private static final String TOML_FILE = "difficulty = \"NORMAL\"\r\ntags = [\"Linear Algebra\", \"Math\", \"BA1\"]" +
            "\r\n\r\n[question]\r\n\r\ntitle = \"Image de matrices \u00E9quivalentes\"\r\ntext = \"\\\\text{Vrai ou Faux:" +
            " \\\\\r\n        Soient A et B deux matrices \u00E0 coefficients r\u00E9els colonne-\u00E9quivalentes. " +
            "\\\\\r\n        Alors} Im(A) = Im(B)\"\r\n\r\n[answer]\r\n\r\nprocessors = [\"clean-string\", \"true-false\"]" +
            "\r\ncorrect = [\"True\"]\r\n\r\n[answer.evaluator]\r\nname = \"exact-answer\"\r\n\r\n";

    private static final String DB_PARSED = "AAAAAQAAAS4AAAAGTk9STUFMAAAABFRydWUAAACECWV4dHtWcmFpIG91IEZhdXg6IFwNCiAgICA" +
            "gICAgU29pZW50IEEgZXQgQiBkZXV4IG1hdHJpY2VzIOAgY29lZmZpY2llbnRzIHLpZWxzIGNvbG9ubmUt6XF1aXZhbGVudGVzLiBcDQogIC" +
            "AgICAgIEFsb3JzfSBJbShBKSA9IEltKEIpAAAAKmFuc3dlci5wcm9jZXNzb3JzPWNsZWFuLXN0cmluZ1w7dHJ1ZS1mYWxzZQAAABdMaW5lY" +
            "XIgQWxnZWJyYTtNYXRoO0JBMQAAACJhbnN3ZXIuZXZhbHVhdG9yLm5hbWU9ZXhhY3QtYW5zd2VyAAAAFJzOSn0sIYm+qnQzqr+ObcEnozuo" +
            "AAAACXRlc3QudG9tbA==";

    @Test
    public void database_test_parsing() {

        FilePathReader filePathReader = file -> file.equals("test.toml")
                ? Optional.of(TOML_FILE)
                : Optional.empty();
        AtomicBoolean dbHasBeenWritten = new AtomicBoolean(Boolean.FALSE);
        AtomicBoolean fileHasBeenReadFrom = new AtomicBoolean(Boolean.FALSE);

        SerializedIO serializedIO = new SerializedIO() {
            @Override
            public Optional<ByteBuffer> read() {
                return Optional.empty();
            }

            @Override
            public void write(ByteBuffer buffer) {
                dbHasBeenWritten.set(Boolean.TRUE);
                // Assert.assertEquals(new String(Base64.getEncoder().encode(buffer.array())), DB_PARSED);
            }
        };

        UpdatableEntry singleUpdater = new UpdatableEntry() {

            @Override
            public boolean needsUpdate(Set<DBEntry> entries) {
                return true;
            }

            @Override
            public String getId() {
                return "test.toml";
            }

            @Override
            public CheckSum computeCheckSum() {
                return filePathReader.read(getId()).map(CheckSum::fromString).get();
            }

            @Override
            public Set<DBEntry> generateNewEntries() {
                fileHasBeenReadFrom.set(Boolean.TRUE);
                return QuestionParser.getDatabaseEntries(getId(), SimpleQuestionDatabase.TABLE, filePathReader);
            }
        };

        // Create the database
        QuestionDatabase simpleQuestionDatabase = new SimpleQuestionDatabase(List.of(singleUpdater), serializedIO,false);
        simpleQuestionDatabase.sync();

        // CHeck the validity of the result question
        List<Question> questionListWithTagMath = simpleQuestionDatabase.listQuery(QuestionQueries.allWithTag("Math"));
        Assert.assertEquals(questionListWithTagMath.size(),1);
        Assert.assertEquals(questionListWithTagMath.get(0).getDifficulty(), QuestionDifficulty.NORMAL);
        System.out.println(questionListWithTagMath.get(0).getRawQuestion());

        // Assert that the db as been written
        Assert.assertTrue(dbHasBeenWritten.get());

        // Assert that the file as been written
        Assert.assertTrue(fileHasBeenReadFrom.get());
    }

    @Test
    public void database_test_loading() {

    }
}
