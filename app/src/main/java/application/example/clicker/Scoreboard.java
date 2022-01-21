package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Scoreboard extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore objectFirebaseFirestore;
    private FirestoreRecyclerAdapter<ScoreModel, ScoreViewHolder> adapter;
    String gameId, quizId, userId, gamePin;
    CollectionReference userRef;
    Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_scoreboard );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        gameId = intent.getStringExtra( "gameId" );
        quizId = intent.getStringExtra( "quizId" );
        userId = intent.getStringExtra( "userId" );
        gamePin = intent.getStringExtra( "gamePin" );

        next = findViewById( R.id.next );
        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scoreboard.this.finish();
            }
        } );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "joinGame" );

        Query query1 = userRef.whereEqualTo( "gameId", gameId ).orderBy( "score", Query.Direction.DESCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ScoreModel>().setQuery( query1, ScoreModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ScoreModel, ScoreViewHolder>( options1 ) {
            @Override
            protected void onBindViewHolder(@NonNull ScoreViewHolder scoreViewHolder, int i, @NonNull ScoreModel scoreModel) {
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
        super.onBackPressed();
        Scoreboard.this.finish();
    }


}
