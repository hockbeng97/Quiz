package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.skyfishjy.library.RippleBackground;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostView extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<PlayerModel, PlayerViewHolder> adapter;
    TextView count, viewPin;
    String gamePin, userId, quizId, gameId;
    Map<String, Object> objectMap;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_host_view );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        count = findViewById( R.id.count );

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        Intent getValue = getIntent();
        gamePin = getValue.getStringExtra( "gamePin" );
        quizId = getValue.getStringExtra( "quizId" );
        userId = getValue.getStringExtra( "userId" );
        gameId = getValue.getStringExtra("gameId");

        viewPin = findViewById( R.id.viewPin );
        viewPin.setText( gamePin );

        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        Query query = objectFirebaseFirestore.collection( "joinGame" ).whereEqualTo( "gameId", gameId )
                .orderBy( "username", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions<PlayerModel> options = new FirestoreRecyclerOptions.Builder<PlayerModel>()
                .setQuery(query, PlayerModel.class)
                .build();


        // ----------  INSTANTIATE Recycle View ------

        adapter = new FirestoreRecyclerAdapter<PlayerModel, PlayerViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PlayerViewHolder playerViewHolder, int position, @NonNull PlayerModel playerModel) {
                playerViewHolder.username.setText( playerModel.getUsername() );
            }

            @NonNull
            @Override
            public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new PlayerViewHolder(view);
            }

        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // Display player joined
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
                int i = adapter.getItemCount();
                count.setText( i + " Joined ");
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved( positionStart, itemCount );
                int i = adapter.getItemCount();
                count.setText( i + " Joined ");
            }
        });

        // ----- End -----
        startBtn = findViewById( R.id.startBtn );
        startBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState();
            }
        } );
    }

private class PlayerViewHolder extends RecyclerView.ViewHolder{

         View view;
         TextView username;

    public PlayerViewHolder(@NonNull View itemView) {
        super( itemView );
        view = itemView;
        username = itemView.findViewById( R.id.list_name );
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

    public void gameState(){

        objectMap = new HashMap<>();

        userRef = objectFirebaseFirestore.collection( "game" );

        objectMap.put( "state", "start" );

        userRef.document(gameId).update( objectMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText( HostView.this, "Game Start", Toast.LENGTH_SHORT ).show();
            }
        } ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startGame();
            }
        } );
    }


    public void startGame(){
        Intent intent = new Intent (this, Host_ScoreBoard.class);
        intent.putExtra( "quizId", quizId );
        intent.putExtra( "gameId", gameId );
        intent.putExtra( "userId", userId );
        intent.putExtra( "gamePin", gamePin );
        startActivity(intent);
        HostView.this.finish();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder( HostView.this );
        builder.setTitle( "Are You Sure ?" );
        builder.setMessage( "The game data will be deleted and not be recorded !" );
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
                deleteGame();
                deleteJoinGame();
                deleteGameQuiz();
            }
        } );
        builder.show();
    }


    public void deleteGame(){
        userRef = objectFirebaseFirestore.collection( "game" );
        userRef.document(gameId).delete();
    }

    public void deleteJoinGame(){

        userRef = objectFirebaseFirestore.collection( "game" );

        userRef.whereEqualTo( "gameId", gameId ).get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    String id = documentSnapshot.getId();
                    userRef.document(id).delete();
                }
            }
        } );
    }

    public void deleteGameQuiz(){

        userRef = objectFirebaseFirestore.collection("gameQuiz");

        userRef.whereEqualTo( "gameId", gameId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    String id = documentSnapshot.getId();
                    userRef.document( id ).delete();
                    deleteGameQuestion( id );
                }
            }
        } );

    }

    public void deleteGameQuestion(final String qid){
        userRef = objectFirebaseFirestore.collection( "gameQuestion" );

        userRef.whereEqualTo( "quizId", qid ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                WriteBatch batch = FirebaseFirestore.getInstance().batch();

                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList){
                    batch.delete( documentSnapshot.getReference() );
                    deleteGameOption( documentSnapshot.getId() );
                }
                batch.commit();

            }
        } );
    }

    public void deleteGameOption(String questionId){
        userRef = objectFirebaseFirestore.collection( "gameOption" );

        userRef.whereEqualTo( "questionId", questionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot documentSnapshot: snapshotList){
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } );
        HostView.this.finish();
    }

}


