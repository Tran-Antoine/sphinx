package net.starype.quiz.api.game.answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerParserList implements AnswerParser {

    private List<AnswerParser> answerParsers;

    @Override
    public Answer process(Answer str) {
        Answer currentStateStr = str;
        for(AnswerParser answerParsers : answerParsers) {
            currentStateStr = answerParsers.process(currentStateStr);
        }
        return currentStateStr;
    }

    private AnswerParserList(List<AnswerParser> answerParsers) {
        this.answerParsers = answerParsers;
    }

    public class Builder
    {
        private List<AnswerParser> answerParsers = new ArrayList<AnswerParser>();

        public Builder withAnswerParser(AnswerParser answerParser) {
            answerParsers.add(answerParser);
            return this;
        }

        public AnswerParserList build() {
            return new AnswerParserList(answerParsers);
        }
    }
}
