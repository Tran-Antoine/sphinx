package net.starype.quiz.api.game.answer;

/**
 * Processor that replaces all potential separator markers by ";". <br>
 * Recognized separators: <br>
 * <ul>
 *     <li>
 *         foo,bar
 *     </li>
 *     <li>
 *         foo bar
 *     </li>
 *     <li>
 *         foo-bar
 *     </li>
 *     <li>
 *         foo|bar
 *     </li>
 * </ul>
 */
public class CleanSeparatorProcessor implements AnswerProcessor {

    @Override
    public Answer process(Answer answer) {
        String parsedAnswer = answer.getAnswerText()
                .strip()
                .replace(",", ";")
                .replace(" ", ";")
                .replace("-", ";")
                .replace("|", ";")
                .replaceAll("[\\;]+", ";");
        return Answer.fromString(parsedAnswer);
    }
}
