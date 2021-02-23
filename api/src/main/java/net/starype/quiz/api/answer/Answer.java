package net.starype.quiz.api.answer;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper for character string answers. <br>
 * Mainly used to avoid primitive obsession and offer quick conversion methods
 */
public class Answer {

    private final String answer;

    private Answer(String answer) {
        this.answer = answer;
    }

    /**
     * Construct an Answer object from the given string
     * @param answer the character string corresponding to the answer
     * @return the Answer object created
     */
    public static Answer fromString(String answer) {
        return new Answer(answer);
    }

    /**
     * Maps a collection of string to a set of answers
     * @param stringCollection the collection of character strings
     * @return the set of answers computed
     */
    public static Set<Answer> fromStringCollection(Collection<String> stringCollection) {
        return stringCollection.stream()
                .map(Answer::fromString)
                .collect(Collectors.toSet());
    }

    /**
     * @return the text value of the answer
     */
    public String getAnswerText() {
        return answer;
    }

    /**
     * @return the text value of the answer as an integer
     */
    public int asInt() {
        return Integer.parseInt(answer);
    }

    /**
     * @return the text value of the answer as a double
     */
    public double asDouble() {
        return Double.parseDouble(answer.replace(',', '.'));
    }

    /**
     * Split the answer using the given regex
     * @param regex the regex used to split the text value of the answer object
     * @return a list containing the result of the split
     */
    public List<Answer> split(String regex) {
        return Stream.of(answer.split(regex))
                .map(Answer::fromString)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return answer.equals(answer1.answer);
    }

    public Answer mapText(Function<String, String> mapping) {
        return Answer.fromString(mapping.apply(answer));
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }

    @Override
    public String toString() {
        return answer;
    }
}
