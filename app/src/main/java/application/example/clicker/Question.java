package application.example.clicker;

public class Question {

    private String option1;
    private boolean is_true1;
    private String questionId;


    public Question(){}

    public Question (String option){
        this.option1 = option;
    }

    public Question (boolean is_true){
        this.is_true1 = is_true;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setIs_true1(boolean is_true1) {
        this.is_true1 = is_true1;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getOption1(){ return option1; }

    public boolean getIs_true1() { return is_true1; }

    public String getQuestionId() { return questionId; }


}
