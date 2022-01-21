package application.example.clicker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData<click> extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    String quizId, joinGameID, gameId, userId, gamePin;
    ImageView image;
    TextView question;
    int a = 0;
    Button option1, option2, option3, option4;
    static boolean check1, check2, check3, check4;
    Map<String, Object> objectMap;
    Map<String, Object> objectMap1;

    ArrayList <String> dbquestion = new ArrayList<>();
    ArrayList <String> dbimageUri = new ArrayList<>();
    ArrayList <Integer> dbtime = new ArrayList<>();
    ArrayList <String> dbquestionId = new ArrayList<>();
    static int click = 0;
    static CountDownTimer countdown;
    static boolean option_is_clicked;
    Dialog dialog;
    ImageView imgView, imgView1;

    long futureTimestamp;
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_data );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        image = findViewById( R.id.imageInput );
        question = findViewById( R.id.question );
        option1 = findViewById( R.id.option1 );
        option2 = findViewById( R.id.option2 );
        option3 = findViewById( R.id.option3 );
        option4 = findViewById( R.id.option4 );

        Intent intent = getIntent();
        quizId = intent.getStringExtra( "quizId" );
        joinGameID = intent.getStringExtra( "joinGameId" );
        gameId = intent.getStringExtra( "gameId" );
        userId = intent.getStringExtra( "userId" );
        gamePin = intent.getStringExtra( "gamePin" );

            option1.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer( check1 );
                    insertScore( check1 );
                    option_is_clicked = true;
                    option1.setEnabled( false );
                    option2.setEnabled( false );
                    option2.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option3.setEnabled( false );
                    option3.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option4.setEnabled( false );
                    option4.setBackgroundColor( Color.parseColor( "#808080" ) );
                }
            } );

            option2.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer( check2 );
                    insertScore( check2 );
                    option_is_clicked = true;
                    option1.setEnabled( false );
                    option1.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option2.setEnabled( false );
                    option3.setEnabled( false );
                    option3.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option4.setEnabled( false );
                    option4.setBackgroundColor( Color.parseColor( "#808080" ) );
                }
            } );

            option3.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer( check3 );
                    insertScore( check3 );
                    option_is_clicked = true;
                    option1.setEnabled( false );
                    option1.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option2.setEnabled( false );
                    option2.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option3.setEnabled( false );
                    option4.setEnabled( false );
                    option4.setBackgroundColor( Color.parseColor( "#808080" ) );
                }
            } );

            option4.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer( check4 );
                    insertScore( check4 );
                    option_is_clicked = true;
                    option1.setEnabled( false );
                    option1.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option2.setEnabled( false );
                    option2.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option3.setEnabled( false );
                    option3.setBackgroundColor( Color.parseColor( "#808080" ) );
                    option4.setEnabled( false );
                }
            } );


        retrieveQuestion();
    }

    public void retrieveQuestion(){

        userRef = objectFirebaseFirestore.collection( "gameQuestion" );

        userRef.whereEqualTo( "quizId", quizId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(final DocumentSnapshot docSnap: queryDocumentSnapshots){

                    dbquestion.add( a, docSnap.getString( "txt_question" ) );
                    dbimageUri.add( a, docSnap.getString( "imageUri" ) );
                    dbtime.add( a, Integer.parseInt( docSnap.getString( "time" ) ) );
                    dbquestionId.add( a, docSnap.getId() );
                }
                showQuestion();
                retrieveOption( dbquestionId.get( click ) );
                }
            }
        );
    }

    public void retrieveOption(String questionId){

        userRef = objectFirebaseFirestore.collection( "gameOption" );

        userRef.whereEqualTo( "questionId", questionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                a = 1;
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {

                    String dboption = documentSnapshot.getString( "option" );
                    boolean dbtrue = documentSnapshot.getBoolean( "is_true" );

                    if (a == 1) {
                        option1.setText( dboption );
                        check1 = dbtrue;
                    }
                    else if (a == 2){
                        option2.setText(dboption);
                        check2 = dbtrue;
                    }
                    else if (a == 3) {
                        option3.setText( dboption );
                        check3 = dbtrue;
                    }
                    else if (a == 4) {
                        option4.setText( dboption );
                        check4 = dbtrue;
                    }
                    a++;

                }// end for


            }
        } );
    }

    public void showQuestion(){

        option_is_clicked = false;

        try {
            question.setText( dbquestion.get( click ) );

            // -- click to zoom image --

            dialog = new Dialog(GameData.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.floating_image);

            imgView = image;
            imgView1 = dialog.findViewById( R.id.fullImage );

            if( dbimageUri.get(click) == null ) {
                imgView.setImageResource( R.drawable.blank_img );
            }else{
                Picasso.with( GameData.this ).load( dbimageUri.get( click ) ).into( imgView );
            }

            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(dbimageUri.get( click ) == null) {
                        imgView1.setImageResource( R.drawable.blank_img );
                    }else{
                        Picasso.with( GameData.this ).load( dbimageUri.get( click ) ).into( imgView1 );
                    }
                    imgView1.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    imgView1.setAdjustViewBounds(true);
                    dialog.show();

                }
            });
            // ---- End Zoom Image ---

            retrieveOption( dbquestionId.get( click ) );

            futureTimestamp = (dbtime.get( click ) * 1000);
            timerText = findViewById(R.id.timer);

            final Dialog dialog = new Dialog(GameData.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.floating_layout);
            dialog.show();

            new Handler( Looper.getMainLooper()).postDelayed( new Runnable() {
                @Override
                public void run() {

                    dialog.cancel();

                    countdown = new CountDownTimer(futureTimestamp, 1000) {

                        public void onTick(long millisUntilFinished) {
                            timerText.setText(millisUntilFinished / 1000 + " seconds");
                        }

                        public void onFinish() {

                            try {
                                if(option_is_clicked == true){
                                click++;
                                showQuestion();
                                Toast.makeText( GameData.this, "Option is clicked", Toast.LENGTH_SHORT ).show();
                                }
                                else{
                                    click++;
                                    showQuestion();
                                    checkAnswer( false );
                                    insertScore( false );
                                    Toast.makeText( GameData.this, "Option is  not clicked", Toast.LENGTH_SHORT ).show();
                                }
                            }
                            catch(Exception exception) {
                                scoreBoard();
                            }

                        }
                    }.start();
                }
            }, 3000);

            option1.setEnabled( true );
            option2.setEnabled( true );
            option3.setEnabled( true );
            option4.setEnabled( true );

            option1.setBackgroundColor( Color.parseColor( "#66FF66" ) );
            option2.setBackgroundColor( Color.parseColor( "#B266FF" ) );
            option3.setBackgroundColor( Color.parseColor( "#FFFF33" ) );
            option4.setBackgroundColor( Color.parseColor( "#FF6666" ) );

        }catch(Exception e){
            Toast.makeText( this, "Question Ended", Toast.LENGTH_SHORT ).show();
            GameData.this.finish();
            dbquestionId.clear();
            dbquestion.clear();
            dbimageUri.clear();
            dbtime.clear();
            click = 0;
            scoreBoard();
        }

    }


    public void checkAnswer(final boolean check){

        objectMap = new HashMap<>();
        // get the user click
        userRef = objectFirebaseFirestore.collection( "user_score" );

        objectMap.put("gameId", gameId);
        objectMap.put( "userId", userId );
        objectMap.put("questionId", dbquestionId.get( click ));
        objectMap.put("check", check);
        objectMap.put("joinGameId", joinGameID);


        userRef.add( objectMap ).addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText( GameData.this, "Your Answer is " + check, Toast.LENGTH_SHORT ).show();
            }
        } );
    }


    public void insertScore(boolean check){

        userRef = objectFirebaseFirestore.collection( "joinGame" );

        objectMap1 = new HashMap<>();

        if(check == true) {
            userRef.document( joinGameID ).update( "score", FieldValue.increment(1000));
        }else{
            userRef.document( joinGameID ).update( "score", FieldValue.increment(200));
        }

    }

    public void scoreBoard(){
        Intent intent = new Intent (this, Scoreboard.class);
        intent.putExtra( "gameId", gameId );
        intent.putExtra( "gamePin", gamePin );
        GameData.this.finish();
        startActivity(intent);
    }


}
