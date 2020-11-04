import java.util.regex.Matcher;

/**
 * @brief This define a answer format
 */
public class AnswerFormat {
    private Matcher matcher;
    private AnswerType type;

    public AnswerType getType() {
        return type;
    }

    public AnswerFormat(Matcher format, AnswerType type) {
        this.type = type;
        this.matcher = format;
    }

    public boolean isValidAnswer(String answer) {
        if(matcher.find()) {
            String outputString = matcher.group();

            if(type.equals(AnswerType.Double)) {
                return outputString.strip().matches("[+-]?([0-9]*[.])?[0-9]+");
            }
            else if(type.equals(AnswerType.Integer)) {
                return outputString.strip().matches("[-+]?\\d+");
            }
            else if(type.equals(AnswerType.Letters)) {
                return outputString.strip().matches("[a-zA-Z]");
            }
            else if(type.equals(AnswerType.PlainText)) {
                return true;
            }
            else if(type.equals(AnswerType.TrueOrFalse)) {
                return outputString.strip().toLowerCase().equals("true") ||
                        outputString.strip().toLowerCase().equals("false") ||
                        outputString.strip().toLowerCase().equals("t") ||
                        outputString.strip().toLowerCase().equals("f") ||
                        outputString.strip().toLowerCase().equals("y") ||
                        outputString.strip().toLowerCase().equals("n");
            }
            else if(type.equals(AnswerType.Words)) {
                return outputString.matches("\\w+");
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean compareAnswer(String answer, String secondAnswer) {
        if(!isValidAnswer(answer) || !isValidAnswer(secondAnswer))
            return false;

        if(type.equals(AnswerType.Letters) ||
                type.equals(AnswerType.PlainText) ||
                type.equals(AnswerType.Words)) {
            return answer.strip().toLowerCase().equals(secondAnswer.strip().toLowerCase());
        }
        else if(type.equals(AnswerType.Double)) {
            double answerValue = Double.valueOf(answer.strip());
            double secondAnswerValue = Double.valueOf(secondAnswer.strip());
            return answerValue == secondAnswerValue;
        }
        else if(type.equals(AnswerType.Integer)) {
            int answerValue = Integer.valueOf(answer.strip());
            int secondAnswerValue = Integer.valueOf(secondAnswer.strip());
            return answerValue == secondAnswerValue;
        }
        return false;
    }
}
