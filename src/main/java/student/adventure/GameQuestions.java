package student.adventure;

import java.util.List;

public class GameQuestions {
    private List<Question> questions;

    public GameQuestions(List<Question> gameQuestions){
        this.questions = gameQuestions;
    }

    public Question getGameQuestions(int i) {
        return questions.get(i);
    }

    public int getListSize(){
        return questions.size();
    }
}
