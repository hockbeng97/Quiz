package application.example.clicker;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClassroomModel {

    String name;
    String pin;
    String created_date;
    String joined_date;
    String announcement;

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public String getJoined_date() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse(joined_date);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf1.format(d);

        return date;
    }

    public String getCreated_date() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse(created_date);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf1.format(d);

        return date;
    }

    public String getAnnouncement(){
        return announcement;
    }
}
