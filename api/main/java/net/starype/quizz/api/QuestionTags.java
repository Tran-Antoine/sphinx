/**
 * @brief A class that define the question tags
 * Each questions is attach to a set of tags
 * Example : Geography, Math, ...
 */
public class QuestionTags {
    private String tagName;

    public QuestionTags(String tagName) {
        this.tagName = tagName.strip().replaceAll("[^a-zA-Z0-9 -]", "").replaceAll(" ", "");
    }

    public String getName() {
        return tagName;
    }
}

