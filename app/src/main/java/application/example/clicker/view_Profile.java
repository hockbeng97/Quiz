package application.example.clicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class view_Profile extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    TextView email, username, uni, logout, contact, term, about, delete;
    Button editBtn, changeBtn;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_profile );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        userId = intent.getStringExtra( "userId" );

        email = findViewById( R.id.txtEmail );
        username = findViewById( R.id.txtusername );
        uni = findViewById( R.id.txtuni );

        editBtn = findViewById( R.id.editBtn );
        editBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdate();
            }
        } );

        changeBtn = findViewById( R.id.changeBtn );
        changeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePassword();
            }
        } );

        logout = findViewById( R.id.logout );
        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        } );

        contact = findViewById(R.id.contact);
        contact.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData( Uri.parse("mailto:relah888@gmail.com"));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        } );

        term = findViewById( R.id.term );
        term.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder( view_Profile.this );
                builder.setTitle( "Terms and Conditions" );
                builder.setMessage( R.string.term );
                builder.setCancelable( true );

                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } );
                builder.show();
            }
        } );

        about = findViewById( R.id.about );
        about.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder( view_Profile.this );
                builder.setTitle( "About Us" );
                builder.setMessage( R.string.about );
                builder.setCancelable( true );

                builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );

                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } );
                builder.show();
            }
        } );

        delete = findViewById( R.id.delete );
        delete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder( view_Profile.this );
                builder.setTitle( "Delete Account" );
                builder.setMessage( "All the data of your account will be erased and cannot be recovered ! Are you sure to delete your account ? ");
                builder.setCancelable( true );

                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                        deleteQuiz();
                        deleteGame();
                        deleteUserScore();
                        deleteClassroom();
                        deleteHomework();
                        deleteJoinClass();
                        deleteJoinGame();
                        deleteJoinHomework();
                        deleteHomeworkScore();
                    }
                } );
                builder.show();
            }
        } );

        retrieveUser();
    }

    public void retrieveUser(){

        userRef = objectFirebaseFirestore.collection( "user" );

        userRef.document(userId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String dbusername = documentSnapshot.getString( "username" );
                String dbemail = documentSnapshot.getString( "email" );
                String dbuni = documentSnapshot.getString( "school" );

                email.setText( dbemail );
                username.setText( dbusername );
                uni.setText( dbuni );
            }
        } );

    }

    public void openUpdate(){
        this.finish();
        Intent intent = new Intent (this, UpdateProfile.class);
        intent.putExtra( "userId", userId );
        startActivity( intent );
    }

    public void openChangePassword(){
        this.finish();
        Intent intent = new Intent (this, UpdatePassword.class);
        intent.putExtra( "userId", userId );
        startActivity( intent );
    }

    public void openLogin(){

        AlertDialog.Builder builder = new AlertDialog.Builder( view_Profile.this );
        builder.setTitle( "Logout" );
        builder.setMessage( "Are you sure to proceed ?" );
        builder.setCancelable( true );

        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        } );

        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent (view_Profile.this, MainActivity.class);
                view_Profile.this.finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent );
                System.exit( 0 );
            }
        } );
        builder.show();
    }

    public void deleteUser(){
        userRef = objectFirebaseFirestore.collection( "user" );
        userRef.document(userId).delete();
    }

    public void deleteQuiz(){
        userRef = objectFirebaseFirestore.collection( "quiz" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String quizId = documentSnapshot.getId();
                    deleteQuestion( quizId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteQuestion(String quizId){
        userRef = objectFirebaseFirestore.collection( "question" );
        userRef.whereEqualTo( "quizId", quizId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String questionId = documentSnapshot.getId();
                    deleteOption( questionId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteOption(String questionId){
        userRef = objectFirebaseFirestore.collection( "option" );
        userRef.whereEqualTo( "questionId", questionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteGame(){
        userRef = objectFirebaseFirestore.collection( "game" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String gameId = documentSnapshot.getId();
                    deleteGameQuiz( gameId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteGameQuiz(String gameId){
        userRef = objectFirebaseFirestore.collection( "gameQuiz" );
        userRef.whereEqualTo( "gameId", gameId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String gameQuizId = documentSnapshot.getId();
                    deleteGameQuestion( gameQuizId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteGameQuestion(String gameQuizId){
        userRef = objectFirebaseFirestore.collection( "gameQuestion" );
        userRef.whereEqualTo( "quizId", gameQuizId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String gameQuestionId = documentSnapshot.getId();
                    deleteGameOption( gameQuestionId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteGameOption(String gameQuestionId){
        userRef = objectFirebaseFirestore.collection( "gameOption" );
        userRef.whereEqualTo( "questionId", gameQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteUserScore(){
        userRef = objectFirebaseFirestore.collection( "user_score" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteClassroom(){
        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String classId = documentSnapshot.getId();
                    deleteAnnouncement( classId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteAnnouncement(String classId){
        userRef = objectFirebaseFirestore.collection( "announcement" );
        userRef.whereEqualTo( "classId", classId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteHomework(){
        userRef = objectFirebaseFirestore.collection( "homework" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String homeworkId = documentSnapshot.getId();
                    deleteHomworkQuiz( homeworkId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteHomworkQuiz(String homeworkId){
        userRef = objectFirebaseFirestore.collection( "homeworkQuiz" );
        userRef.whereEqualTo( "homeworkId", homeworkId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String homeworkQuizId = documentSnapshot.getId();
                    deleteHomeworkQuestion( homeworkQuizId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteHomeworkQuestion(String homeworkQuizId){
        userRef = objectFirebaseFirestore.collection( "homeworkQuestion" );
        userRef.whereEqualTo( "quizId", homeworkQuizId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                    String homeworkQuestionId = documentSnapshot.getId();
                    deleteHomeworkOption( homeworkQuestionId );
                }
                batch.commit();
            }
        } );
    }

    public void deleteHomeworkOption(String homeworkQuestionId){
        userRef = objectFirebaseFirestore.collection( "homeworkOption" );
        userRef.whereEqualTo( "questionId", homeworkQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteJoinClass(){
        userRef = objectFirebaseFirestore.collection( "joinClass" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteJoinGame(){
        userRef = objectFirebaseFirestore.collection( "joinGame" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteJoinHomework(){
        userRef = objectFirebaseFirestore.collection( "joinHomework" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

    public void deleteHomeworkScore(){
        userRef = objectFirebaseFirestore.collection( "homework_score" );
        userRef.whereEqualTo( "userId", userId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
    }

}
