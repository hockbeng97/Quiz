package application.example.clicker;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportModel {

    String title;
    String hosted_date;
    Date due_date_time;

    public ReportModel() {

    }

    public ReportModel(String title, String hosted_date) {

        this.title = title;
        this.hosted_date = hosted_date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getHosted_date() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date d = sdf.parse( hosted_date );
        SimpleDateFormat sdf1 = new SimpleDateFormat( "dd-MM-yyyy" );
        String date = sdf1.format( d );

        return date;
    }

    public Date getDue_date_time() {
        return due_date_time;
    }

}