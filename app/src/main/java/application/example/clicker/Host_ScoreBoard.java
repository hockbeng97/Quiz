package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Host_ScoreBoard extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore objectFirebaseFirestore;
    private FirestoreRecyclerAdapter<ScoreModel, ScoreViewHolder> adapter;
    String gameId, quizId, userId, gamePin;
    CollectionReference userRef;
    Map <String, Object> objectMap;
    Button endQuiz, closeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_host_score_board );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        endQuiz = findViewById( R.id.next );
        endQuiz.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGame();
            }
        } );

        closeBtn = findViewById( R.id.closeBtn );
        closeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( Host_ScoreBoard.this );
                builder.setTitle( "Alert" );
                builder.setMessage( "Are you sure to Quit ? The Quiz will be ended" );
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
                        finishGame();
                        Host_ScoreBoard.this.finish();
                    }
                } );
                builder.show();
            }
        } );

        Intent intent = getIntent();
        gameId = intent.getStringExtra( "gameId" );
        quizId = intent.getStringExtra( "quizId" );
        userId = intent.getStringExtra( "userId" );
        gamePin = intent.getStringExtra( "gamePin" );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "joinGame" );

        Query query1 = userRef.whereEqualTo( "gameId", gameId ).orderBy( "score", Query.Direction.DESCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ScoreModel>().setQuery( query1, ScoreModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ScoreModel, ScoreViewHolder>( options1 ) {
            @Override
            protected void onBindViewHolder(@NonNull final ScoreViewHolder scoreViewHolder, int i, @NonNull ScoreModel scoreModel) {
                scoreViewHolder.username.setText( scoreModel.getUsername() );
                scoreViewHolder.score.setText( String.valueOf( scoreModel.getScore() ) );

            }

            @NonNull
            @Override
            public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_score, parent, false );
                return new ScoreViewHolder( v );
            }
        };
        recyclerView.setAdapter( adapter );
    }

    private class ScoreViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView username;
        TextView score;

        public ScoreViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            username = itemView.findViewById( R.id.list_name );
            score = itemView.findViewById( R.id.list_score );
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

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder( Host_ScoreBoard.this );
        builder.setTitle( "Alert" );
        builder.setMessage( "Are you sure to Quit ? The Quiz will be ended" );
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
                finishGame();
                Host_ScoreBoard.this.finish();
            }
        } );
        builder.show();
    }

    public void finishGame(){

        objectMap = new HashMap<>();

        userRef = objectFirebaseFirestore.collection( "game" );
        objectMap.put( "state", "END" );
        userRef.document(gameId).update( objectMap );

        deletePin();
    }

    public void deletePin(){
        userRef = objectFirebaseFirestore.collection( "game" );
        userRef.document(gameId).update( "pin", FieldValue.delete() );
    }



}