package application.example.clicker;

public class User {

    private String email, password, username, school;

    public User(){

    }

    public User(String email, String password, String username, String school){
        this.email = email;
        this.password = password;
        this.username = username;
        this.school = school;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getUsername(){
        return username;
    }

    public String getSchool(){
        return school;
    }

}
