package application.example.clicker;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeworkModel {

    String title;
    Date due_date_time;


    public String getTitle(){
        return title;
    }

    public Date getDue_date_time()  {

        return due_date_time;
    }



}
