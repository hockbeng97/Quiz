package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ViewQuiz extends AppCompatActivity {

    String txttitle;
    String txtDocId, txtDesc, txtImg, txtuserId;
    FirebaseFirestore objectFirestore;
    CollectionReference userRef;
    RelativeLayout relativelayout;
    String dbimg, dbquestion, dbtime, dbQuestionId;
    String dbQuizImage, dbQuizTitle, dbQUizDescription;
    String KEY_IMG = "imageUri", KEY_QUESTION = "txt_question", KEY_TIME = "time";
    TextView txtview, txtview1;
    int topMargin, a;
    ImageView imgview;
    TextView getTitle;
    TextView getDesc;
    Button hostBtn, homeworkBtn;
    Map<String, Object> objectMap;
    String gamePin;
    static String gameId;
    ImageView imgLayout;
    List <String> pin;
    static int countPin;
    static String gameQuizId;
    String gameQuestionId;
    static int check_hostBtn;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuestionModel, ClassroomViewHolder> adapter;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_quiz );

        objectFirestore = FirebaseFirestore.getInstance();
        pin = new ArrayList<>();

        getTitle = (TextView) findViewById( R.id.title );
        getDesc = (TextView) findViewById( R.id.description );
        imgview = (ImageView) findViewById( R.id.show_img );

        Intent title = getIntent();
        txttitle = title.getStringExtra( "quiz_title" );
        txtDocId = title.getStringExtra( "docId" );
        txtDesc = title.getStringExtra( "description" );
        txtImg = title.getStringExtra( "imageUri" );
        txtuserId = title.getStringExtra( "userId" );

        // ---------------- GET QUIZ -------------------------

        userRef = objectFirestore.collection( "quiz" );

        userRef.document( txtDocId ).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                dbQuizImage = documentSnapshot.getString( "imageUri" );
                dbQuizTitle = documentSnapshot.getString( "title" );
                dbQUizDescription = documentSnapshot.getString( "description" );

                getTitle.setText( dbQuizTitle );
                getDesc.setText( dbQUizDescription );
                if (dbQuizImage != null)
                    Picasso.with( ViewQuiz.this ).load( dbQuizImage ).into( imgview );
                else {
                    imgview.setImageResource( R.drawable.blank_img );
                }
            }
        } );

        // ---------------- GET QUIZ END -------------------------

        final Intent view_quiz = new Intent( ViewQuiz.this, editQuiz.class );
        view_quiz.putExtra( "docId", txtDocId );
        view_quiz.putExtra( "description", txtDesc );
        view_quiz.putExtra( "imageUri", txtImg );
        view_quiz.putExtra( "quiz_title", txttitle );

        Button editBtn = findViewById( R.id.editBtn );
        editBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( view_quiz );
                //ViewQuiz.this.finish();
            }
        } );

        Button deleteBtn = findViewById( R.id.deleteBtn );
        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( ViewQuiz.this );
                builder.setTitle( "Alert" );
                builder.setMessage( "Are you sure to delete ?" );
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
                        deleteQuiz();
                    }
                } );
                builder.show();
            }
        } );

        generatePin();

        check_hostBtn = 0;
        hostBtn = findViewById( R.id.hostBtn );
        hostBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPin();
            }
        } );

        homeworkBtn = findViewById( R.id.homeworkBtn );

        homeworkBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateHomework();
            }
        } );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );


        userRef = objectFirestore.collection( "question" );

        Query query1 = userRef.whereEqualTo( "quizId", txtDocId );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query1, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, ClassroomViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ClassroomViewHolder classroomViewHolder, final int position, @NonNull QuestionModel classroomModel) {

                String image = classroomModel.getImageUri();
                classroomViewHolder.quizTitle.setText( classroomModel.getTxt_question() );

                if(image == null){
                    classroomViewHolder.image.setImageResource( R.drawable.blank_img );
                }else{
                    Picasso.with( ViewQuiz.this ).load( image ).into( classroomViewHolder.image );
                }


                // -----  Click Relative Layout to view question -------------------------

                dialog = new Dialog(ViewQuiz.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.floating_question);


                classroomViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = getSnapshots().getSnapshot( position ).getId();

                        userRef = objectFirestore.collection("question");

                        userRef.document(id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                TextView question = dialog.findViewById( R.id.question );
                                ImageView image1 = dialog.findViewById( R.id.imageInput );
                                TextView timer = dialog.findViewById( R.id.timer );

                                String dbquestion = documentSnapshot.getString( "txt_question" );
                                String dbimg = documentSnapshot.getString( "imageUri" );
                                String dbtime = documentSnapshot.getString( "time" );

                                question.setText( dbquestion );

                                if(dbimg != null)
                                    Picasso.with( ViewQuiz.this ).load( dbimg ).into( image1 );
                                else
                                    image1.setImageResource( R.drawable.blank_img );

                                timer.setText( dbtime + " sec" );
                            }
                        } );


                        userRef = objectFirestore.collection( "option" );
                        userRef.whereEqualTo( "questionId", id )
                                .get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                int b = 1;

                                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                    String option = documentSnapshot.getString( "option1" );
                                    boolean is_true = documentSnapshot.getBoolean( "is_true1" );

                                    if (b == 1) {
                                        TextView op1 = dialog.findViewById( R.id.option1 );
                                        ImageView tick = dialog.findViewById( R.id.tick1 );
                                        tick.setImageResource( R.drawable.grey_tick );
                                        op1.setText(option);
                                        if(is_true == true)
                                            tick.setImageResource( R.drawable.green_tick );
                                    }

                                    if (b == 2) {
                                        TextView op1 = dialog.findViewById( R.id.option2 );
                                        ImageView tick = dialog.findViewById( R.id.tick2 );
                                        tick.setImageResource( R.drawable.grey_tick );
                                        op1.setText(option);
                                        if(is_true == true)
                                            tick.setImageResource( R.drawable.green_tick );
                                    }

                                    if (b == 3) {
                                        TextView op1 = dialog.findViewById( R.id.option3 );
                                        ImageView tick = dialog.findViewById( R.id.tick3 );
                                        tick.setImageResource( R.drawable.grey_tick );
                                        op1.setText(option);
                                        if(is_true == true)
                                            tick.setImageResource( R.drawable.green_tick );
                                    }

                                    if (b == 4) {
                                        TextView op1 = dialog.findViewById( R.id.option4 );
                                        ImageView tick = dialog.findViewById( R.id.tick4 );
                                        tick.setImageResource( R.drawable.grey_tick );
                                        op1.setText(option);
                                        if(is_true == true)
                                            tick.setImageResource( R.drawable.green_tick );
                                    }
                                    b++;
                                }// end for
                            }
                        } );

                        dialog.show();
                    }
                });

                //  =====  End ========


            }

            @NonNull
            @Override
            public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_view_quiz, parent, false );
                return new ClassroomViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class ClassroomViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView image;
        TextView quizTitle;

        public ClassroomViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            image = itemView.findViewById( R.id.image );
            quizTitle = itemView.findViewById( R.id.quizTitle );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void retrieveGameQuestion(){

        userRef = objectFirestore.collection( "question" );

        userRef.whereEqualTo( "quizId", txtDocId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    dbimg = documentSnapshot.getString( KEY_IMG );
                    dbquestion = documentSnapshot.getString( KEY_QUESTION );
                    dbtime = documentSnapshot.getString( KEY_TIME );
                    dbQuestionId = documentSnapshot.getId();

                        insertGameQuestion( dbquestion, dbtime, dbimg );
                }
            }
        });
    }

    public void deleteQuiz() {

        DocumentReference quizRef = objectFirestore.getInstance().collection( "quiz" ).document( txtDocId );

        quizRef.delete().addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText( ViewQuiz.this, "Quiz Deleted ", Toast.LENGTH_SHORT ).show();
            }
        } );


        // ------------------ Delete Option ------------------------------------------

        objectFirestore.getInstance().collection( "question" )
                .whereEqualTo( "quizId", txtDocId )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            batch.delete( snapshot.getReference() );
                            deleteOption( snapshot.getId() );
                        }
                        batch.commit().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( ViewQuiz.this, "Deleted Quiz", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                refresh();
            }
        } );


    }

    public void deleteOption(String questionId) {

        objectFirestore.getInstance().collection( "option" )
                .whereEqualTo( "questionId", questionId )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            batch.delete( snapshot.getReference() );
                        }
                        batch.commit().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( ViewQuiz.this, "Deleted Option", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                } );

    }


    public void refresh() {
        Intent refresh = new Intent( this, MyQuiz.class );
        refresh.putExtra( "userId", txtuserId );
        startActivity( refresh );
        this.finish();
    }

    // --- Generate Game Pin
    public void generatePin() {

        Random random = new Random();

        int number = random.nextInt( 999999 );

        gamePin = String.format( "%06d", number );
    }


    public void insertPin() {
        DocumentReference ref = objectFirestore.collection( "game" ).document();
        gameId = ref.getId();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        insertGameQuiz();

        objectMap = new HashMap<>();
        objectMap.put( "pin", gamePin );
        objectMap.put( "quizId", gameQuizId );
        objectMap.put( "userId", txtuserId );
        objectMap.put("state", "wait");
        objectMap.put("hosted_date", timestamp.toString());

        ref.set( objectMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                retrieveGameQuestion();
            }
        } );

    }


    public void checkPin() {

        userRef = objectFirestore.collection( "game" );

        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    String dbpin = documentSnapshot.getString( "pin" );
                    pin.add( dbpin );

                }

                countPin = 0;

                for(int a = 0; a < pin.size(); a++ ){

                    if(gamePin.equals( pin.get( a ) )){
                        countPin = 0;
                        break;
                    }else{
                        countPin = 1;
                    }
                }

                if(countPin == 0){
                    generatePin();
                    checkPin();
                }
                else{
                    insertPin( );
                    waitingPlayer();
                }
            }
        } );

    }

    public void waitingPlayer(){
        Intent intent = new Intent(this, HostView.class);
        intent.putExtra( "gamePin", gamePin );
        intent.putExtra( "gameId", gameId );
        intent.putExtra( "quizId", gameQuizId );
        intent.putExtra( "userId", txtuserId );
        startActivity( intent );
    }

    public void insertGameQuiz(){

        objectMap = new HashMap<>();

        objectMap.put( "title", txttitle );
        objectMap.put( "description", txtDesc );
        objectMap.put("imageUri", txtImg);
        objectMap.put( "gameId", gameId );

        DocumentReference ref = objectFirestore.collection( "gameQuiz" ).document();
        gameQuizId = ref.getId();
        ref.set( objectMap );

    }

    public void insertGameQuestion(String question, String time, String imageUri){

        objectMap = new HashMap<>();

        objectMap.put( "txt_question",  question);
        objectMap.put( "time", time );
        objectMap.put( "quizId", gameQuizId );
        objectMap.put( "imageUri", imageUri );

        DocumentReference ref = objectFirestore.collection( "gameQuestion" ).document();
        gameQuestionId = ref.getId();

        ref.set( objectMap );

        retrieveOption( dbQuestionId, gameQuestionId );
    }

    public void retrieveOption(final String dbQuestionId, final String gameQuestionId){

        userRef = objectFirestore.collection( "option" );

        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String option = documentSnapshot.getString( "option1" );
                    Boolean is_true = documentSnapshot.getBoolean( "is_true1" );

                    insertGameOption( option, is_true, gameQuestionId);
                }

            }
        } );

    }

    public void insertGameOption(String option, Boolean is_true, String gameQuestionId ){

        userRef = objectFirestore.collection( "gameOption" );

        objectMap = new HashMap<>();

        objectMap.put( "option", option );
        objectMap.put("is_true", is_true);
        objectMap.put( "questionId", gameQuestionId );

        userRef.add( objectMap );

    }

    public void openCreateHomework(){
        Intent intent = new Intent(this, CreateHomework.class);
        intent.putExtra( "quizId", txtDocId );
        intent.putExtra( "userId", txtuserId );
        startActivity( intent );
    }



}

