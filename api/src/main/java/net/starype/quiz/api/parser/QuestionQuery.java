package net.starype.quiz.api.parser;

import java.util.Set;

public interface QuestionQuery {

    class QueryData {

        private final Set<String> tags;
        private final String text;
        private final String difficulty;
        private final String file;

        public QueryData(Set<String> tags, String text, String difficulty, String file) {
            this.tags = tags;
            this.text = text;
            this.difficulty = difficulty;
            this.file = file;
        }

        public Set<String> getTags() {
            return tags;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getText() {
            return text;
        }

        public String getFile() {
            return file;
        }
    }

    boolean apply(QueryData data);

    default QuestionQuery and(QuestionQuery other) {
        return (data) -> apply(data) && other.apply(data);
    }

    default QuestionQuery or(QuestionQuery other) {
        return (data) -> apply(data) || other.apply(data);
    }
}
