package application.example.clicker;

public class ScoreModel {

    String username;
    int score;

    public ScoreModel(){

    }

    public ScoreModel(String username, int score){
        this.username = username;
        this.score = score;
    }

    public String getUsername(){
        return username;
    }

    public int getScore() { return score; }


}
