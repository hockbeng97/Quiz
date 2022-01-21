package application.example.clicker;

public class QuestionModel {

    String txt_question;
    String imageUri;

    public QuestionModel(){

    }

    public QuestionModel(String txt_question, String imageUri){
        this.txt_question = txt_question;
        this.imageUri = imageUri;
    }

    public String getTxt_question(){ return txt_question; }

    public String getImageUri(){ return imageUri; }

}
