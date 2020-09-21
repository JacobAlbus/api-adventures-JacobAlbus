package student.adventure;

import java.util.List;
import java.util.Scanner;

public class GameQuestions {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private boolean isTestOccuring = false;
    private int correctAnswerCount = 0;

    public GameQuestions(List<Question> gameQuestions){
        this.questions = gameQuestions;
    }

    public Question getGameQuestions(int i) {
        return questions.get(i);
    }

    public int getListSize(){
        return questions.size();
    }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }

    public boolean isTestOccuring(){ return isTestOccuring; }

    public void setTestOccuring(boolean isTestOccuring) { this.isTestOccuring = isTestOccuring; }

    /**
     * Simulates a math test with basic problems; returns boolean depending on whether or not player passes test
     * @return boolean representing whether or not player passed test
     */
    public boolean didPlayerAceMathTest(Room room){
        isTestOccuring = true;
        int numCorrect = 0;
        int passingGrade = 4;

        System.out.println("You have begun the eternal math test, pick your answers wisely!");

        for(Question question : questions){
            numCorrect += this.askMathQuestionScanner(question.getQuestion(), question.getCorrectAnswer());
        }
        isTestOccuring = false;
        room.setPrimaryDescription(room.getPrimaryDescription());

        if(numCorrect >= passingGrade){
            System.out.println("Congratulations, you have passed the test");
            return true;
        } else {
            System.out.println("You failed stupid, try again");
            return false;
        }

    }

    /**
     * Asks player a math question, takes input, and determines if it's correct
     * Made public for testing purposes
     * @param question the question being asked of the player
     * @param correctAnswer correct answer to question
     * @return 1 if player is right, 0 otherwise
     */
    // TODO ask elizabeth about configuring this function for booleans
    public int askMathQuestionScanner(String question, String correctAnswer){
        System.out.println(question);
        System.out.print("> ");

        Scanner playerInput = new Scanner(System.in);
        String playerAnswer = playerInput.nextLine();

        if(playerAnswer.equals(correctAnswer)){
            System.out.println("Correct!");
            return 1;
        } else {
            System.out.println("Wrong!");
            return 0;
        }

    }

    public String askMathQuestionUI(Room room, Player player, String playerAnswer){
        int passingGrade = 4;
        String playerMessage = "Wrong!";

        if(player.isTesting()){
            String correctAnswer = questions.get(currentQuestionIndex).getCorrectAnswer();
            if(playerAnswer.equals(correctAnswer)){
                correctAnswerCount++;
                playerMessage = "Correct!";
            }
            currentQuestionIndex++;

            if(currentQuestionIndex == questions.size()){
                player.setPlayerIsTesting(false);
                currentQuestionIndex = 0;
            }
            if(!player.isTesting() && correctAnswerCount >= passingGrade){
                playerMessage = "Congratualtion, you passed the test!";
                room.addAvailableDoors(room.getUnavailableDoors().get(0));
                room.setPrimaryDescription(room.getSecondaryDescription());
                player.removeItem("calculator");
                correctAnswerCount = 0;
            } else if(!player.isTesting()){
                playerMessage = "You failed, try again";
                correctAnswerCount = 0;
            }
        }

        return playerMessage;
    }

}
