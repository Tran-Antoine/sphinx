package net.starype.quiz.discordimpl.game;

import net.starype.quiz.api.answer.Answer;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.question.QuestionDifficulty;
import net.starype.quiz.api.question.QuestionTag;

import java.util.*;

public class Questions {

    public static Question pickRandom() {
        return MockQuestion.MOCK_QUESTIONS.get(new Random().nextInt(MockQuestion.MOCK_QUESTIONS.size()));
    }

    private static class MockQuestion implements Question {

        private static final List<MockQuestion> MOCK_QUESTIONS = Arrays.asList(
                new MockQuestion("\\textbf{Question:} \\\\ \\text{True or False: Let T a linear transformation that can be represented as a matrix.}" +
                        " \\\\ \\text{T injective} \\iff \\text{T surjective}", "true")
                /*new MockQuestion("True or False: There are as many elements in [0,1[ as in [0, 3[", "true"),
                new MockQuestion("True or False: Fields can be overridden in Java", "false"),
                new MockQuestion("True or False: Let f: E -> F continuous. f is derivable", "false"),
                new MockQuestion(
                        "True or False: The total mass of a sphere can be calculated by the following triple integral:\n" +
                        "Bounds: from 0 to R (d_r) from 0 to 2pi (d_theta) from 0 to 2pi (d_phi)\n" +
                                "Content of integration: rho(r, theta, phi)", "false")*/
        );

        private String question;
        private String textAnswer;
        private UUID uuid;

        public MockQuestion(String question, String answer) {
            this.question = question;
            this.textAnswer = answer;
            this.uuid = UUID.randomUUID();
        }
        @Override
        public Set<QuestionTag> getTags() {
            return Collections.emptySet();
        }

        @Override
        public void registerTag(QuestionTag tag) { }

        @Override
        public void unregisterTag(QuestionTag tag) { }

        @Override
        public QuestionDifficulty getDifficulty() {
            return QuestionDifficulty.EASY;
        }

        @Override
        public String getRawQuestion() {
            return question;
        }

        @Override
        public String getDisplayableCorrectAnswer() {
            return textAnswer;
        }

        @Override
        public Optional<Double> evaluateAnswer(Answer answer) {
            return answer.getAnswerText().equalsIgnoreCase(this.textAnswer)
                    ? Optional.of(1.0)
                    : Optional.of(0.0);
        }

        @Override
        public UUID getId() {
            return uuid;
        }
    }
}
