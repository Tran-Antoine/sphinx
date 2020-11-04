
import java.util.HashMap;
import java.util.Map;

/**
 * @brief This class encapsulates the information of any players
 * and the capacity to answer one question
 */
public class Players {
    private int score;
    private double accuracy;
    private int answeredQuestions;
    private String name;
    private Map<QuestionTags, Double> accuracyByTags;
    private Map<QuestionTags, Integer> answeredQuestionsByTags;

    /**
     * @brief Default construction for the players
     * @param playerName specify the players username
     */
    public Players(String playerName) {
        name = playerName;

        // By default score and answeredQuestions are set to 0
        // accuracy is set to -1 because it could be either 100 or 0
        score = 0;
        answeredQuestions = 0;
        accuracy = -1.0;

        // Setting the accuracy and answeredQuestions by tags
        accuracyByTags = new HashMap<QuestionTags, Double>();
        answeredQuestionsByTags = new HashMap<QuestionTags, Integer>();
    }

    public int getScore() {
        return score;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getName() {
        return name;
    }

    public double getAccuracyByTags(QuestionTags tags) {
        return accuracyByTags.getOrDefault(tags, -1.0);
    }

    public int getAnsweredQuestionsByTags(QuestionTags tags) {
        return answeredQuestionsByTags.getOrDefault(tags, 0);
    }
}
