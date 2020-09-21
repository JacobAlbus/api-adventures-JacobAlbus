package student.adventure;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String question;
    private String correctAnswer;
    private ArrayList<String> playerAnswers;

    public Question(String question, String answer, ArrayList<String> playerAnswers){
        this.question = question;
        this.correctAnswer = answer;
        this.playerAnswers = playerAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public ArrayList<String> getPlayerAnswers() {
        return playerAnswers;
    }
}
