import java.util.List;

/**
 * @brief This class encapsulates each questions structure
 */
public class Question {
    private List<QuestionTags> tags;
    private String question;
    private Difficulty difficulty;
    private AnswerFormat format;
    private List<String> correctAnswers;

    public Question(String question, Difficulty difficulty, AnswerFormat format) {
        this.question = question;
        this.difficulty = difficulty;
        this.format = format;
    }

    public String getQuestion() {
        return question;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public List<QuestionTags> getTags() {
        return tags;
    }

    public boolean isTagged(QuestionTags tag) {
        return tags.contains(tag);
    }

    public void registerTag(QuestionTags tag) {
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public void forgetTag(QuestionTags tag) {
        while(tags.contains(tag))
            tags.remove(tag);
    }

    public boolean isAnswerValid(String answer) {
        for(int i = 0; i < correctAnswers.size(); ++i)
            if(format.compareAnswer(correctAnswers.get(i), answer))
                return true;
        return false;
    }

    public boolean registerAnswer(String answer) {
        if(format.isValidAnswer(answer))
            return false;

        if(!correctAnswers.contains(answer))
            correctAnswers.add(answer);

        return true;
    }

    public void forgetAnswer(String answer) {
        while(correctAnswers.contains(answer))
            correctAnswers.remove(answer);
    }
}
