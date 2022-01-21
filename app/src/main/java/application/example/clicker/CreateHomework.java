package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CreateHomework extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String quizId;
    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    List<String> classes;
    List<String> classesId;
    ArrayAdapter <String> adapter;
    Spinner spinner;
    String uid, date;
    Calendar c;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    TextView clickDate, txt_title, clickTime;
    Button assign;
    Map <String, Object> objectMap;
    int mHour, mMinute;
    String dbQuestionId;
    static String homeworkQuizId, homeworkId, classId;
    String homeworkQuestionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_create_homework );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        classes = new ArrayList<>();
        classesId = new ArrayList<>();
        spinner = findViewById( R.id.spinner );
        clickDate = findViewById( R.id.clickDate );
        clickTime = findViewById( R.id.clickTime );
        assign = findViewById( R.id.assign );
        txt_title = findViewById( R.id.txt_title );

        Intent intent = getIntent();
        quizId = intent.getStringExtra( "quizId" );
        uid = intent.getStringExtra( "userId" );

        clickDate.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        clickTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        } );

        adapter = new ArrayAdapter<String>( getApplicationContext(), R.layout.list_spinner_item, classes);
        spinner.setAdapter( adapter );

        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.whereEqualTo( "userId", uid ).get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String name = documentSnapshot.getString( "name" );
                    String cid = documentSnapshot.getId();
                    classes.add(name);
                    classesId.add(cid);
                }
                adapter.notifyDataSetChanged();

            }
        } );

        assign.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    retrieveClassId();
                    retrieveQuiz();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } );


    }

    private void showDatePicker(){
        dpd = new DatePickerDialog( this,this,
                Calendar.getInstance().get( Calendar.YEAR ),
                Calendar.getInstance().get( Calendar.MONTH ),
                Calendar.getInstance().get( Calendar.DAY_OF_MONTH )
        );
        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        String curDate = String.format("%02d/%02d/%02d", day, month + 1, year);
        clickDate.setText( curDate );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void retrieveClassId() throws ParseException {

        spinner.getSelectedItem().toString();

        int selectedPosition = spinner.getSelectedItemPosition();

        classId = classesId.get( selectedPosition );

        Toast.makeText( this, "position: " + selectedPosition + classId, Toast.LENGTH_SHORT ).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertHomework() throws ParseException {

        String dueDate = clickDate.getText().toString();
        String title = txt_title.getText().toString();
        String time = clickTime.getText().toString();

        String due_date_time = dueDate + " " + time;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date parsedDate = dateFormat.parse(due_date_time);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

            objectMap = new HashMap<>();
            objectMap.put("title", title);
            objectMap.put( "due_date_time", timestamp );
            objectMap.put("classId", classId);
            objectMap.put("quizId", homeworkQuizId);
            objectMap.put("userId", uid);
            objectMap.put("state", "progress");

            DocumentReference ref = objectFirebaseFirestore.collection( "homework" ).document();
            homeworkId = ref.getId();

            ref.set( objectMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText( CreateHomework.this, "You have successfully assigned the homework ! ", Toast.LENGTH_SHORT ).show();
                }
            } );


        } catch(Exception e) { //this generic but you can control another types of exception
            Toast.makeText( CreateHomework.this, "APA LANJIAO ! " + e, Toast.LENGTH_SHORT ).show();
        }

        //-------------- Timestamp format end ------------------------

    }

    private void showTimePicker(){

        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        tpd = new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                clickTime.setText( curTime );
            }
        }, mHour, mMinute, false);

        tpd.show();
    }

    public void retrieveQuiz(){

        userRef = objectFirebaseFirestore.collection( "quiz" );

        userRef.document(quizId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String dbimg = documentSnapshot.getString( "imageUri" );
                String dbdesc = documentSnapshot.getString( "description" );
                String dbtitle = documentSnapshot.getString( "title" );

                try {
                    insertHomeworkQuiz( dbtitle, dbdesc, dbimg );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } );

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertHomeworkQuiz(String title, String description, String imageUri) throws ParseException {

        DocumentReference ref = objectFirebaseFirestore.collection( "homeworkQuiz" ).document();
        homeworkQuizId = ref.getId();

        insertHomework();

        objectMap = new HashMap<>();
        objectMap.put("title", title);
        objectMap.put( "description", description);
        objectMap.put( "imageUri", imageUri );
        objectMap.put( "homeworkId", homeworkId );


        ref.set( objectMap );

        retrieveHomeworkQuestion();

    }

    public void retrieveHomeworkQuestion(){

        userRef = objectFirebaseFirestore.collection( "question" );

        userRef.whereEqualTo( "quizId", quizId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String dbimg = documentSnapshot.getString( "imageUri" );
                    String dbquestion = documentSnapshot.getString( "txt_question" );
                    String dbtime = documentSnapshot.getString( "time" );
                    dbQuestionId = documentSnapshot.getId();

                    insertHomeworkQuestion( dbquestion, dbtime, dbimg );
                }
            }
        });
    }

    public void insertHomeworkQuestion(String question, String time, String imageUri){

        objectMap = new HashMap<>();

        objectMap.put( "txt_question",  question);
        objectMap.put( "time", time );
        objectMap.put( "quizId", homeworkQuizId );
        objectMap.put( "imageUri", imageUri );

        DocumentReference ref = objectFirebaseFirestore.collection( "homeworkQuestion" ).document();
        homeworkQuestionId = ref.getId();

        ref.set( objectMap );

        retrieveOption( dbQuestionId, homeworkQuestionId );
    }

    public void retrieveOption(final String dbQuestionId, final String gameQuestionId){

        userRef = objectFirebaseFirestore.collection( "option" );

        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String option = documentSnapshot.getString( "option1" );
                    Boolean is_true = documentSnapshot.getBoolean( "is_true1" );

                    insertHomeworkOption( option, is_true, gameQuestionId);
                }

            }
        } );

    }

    public void insertHomeworkOption(String option, Boolean is_true, String gameQuestionId ){

        userRef = objectFirebaseFirestore.collection( "homeworkOption" );

        objectMap = new HashMap<>();

        objectMap.put( "option", option );
        objectMap.put("is_true", is_true);
        objectMap.put( "questionId", gameQuestionId );

        userRef.add( objectMap );

    }




}
