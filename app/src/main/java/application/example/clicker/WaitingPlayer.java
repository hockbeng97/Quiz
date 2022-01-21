package application.example.clicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skyfishjy.library.RippleBackground;

public class WaitingPlayer extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    String gamePin, userId, joinGameID, gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_waiting_player );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        Intent intent = getIntent();
        gamePin = intent.getStringExtra( "gamePin" );
        userId = intent.getStringExtra( "userId" );
        joinGameID = intent.getStringExtra( "joinGameID" );
        gameId = intent.getStringExtra( "gameId" );

        Toast.makeText(this, "join game id : "+joinGameID, Toast.LENGTH_SHORT).show();


        userRef = objectFirebaseFirestore.collection( "game" );


                userRef.document( gameId ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        try {
                        String state = documentSnapshot.getString( "state" );
                        String quizId = documentSnapshot.getString( "quizId" );
                        if (state.equals( "start" )) {
                            startGame( quizId );
                        } else {
                            Toast.makeText( WaitingPlayer.this, "Waiting Game Start", Toast.LENGTH_SHORT ).show();
                        }
                        }catch(Exception ex){
                            WaitingPlayer.this.finish();
                            Toast.makeText( WaitingPlayer.this, "Game not exists", Toast.LENGTH_SHORT ).show();
                    }
                } });

    }


    public void startGame(String quizId){
        Intent intent = new Intent (this, GameData.class);
        intent.putExtra( "quizId", quizId );
        intent.putExtra( "userId", userId );
        intent.putExtra( "joinGameId", joinGameID );
        intent.putExtra( "gameId", gameId );
        intent.putExtra( "gamePin", gamePin );
        startActivity(intent);
        WaitingPlayer.this.finish();
    }

    public void deleteJoinGame(){

        userRef = objectFirebaseFirestore.collection( "joinGame" );

        userRef.document(joinGameID).delete();
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder( WaitingPlayer.this );
        builder.setTitle( "Alert" );
        builder.setMessage( "Are you sure to quit ?" );
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
                deleteJoinGame();
                WaitingPlayer.this.finish();
            }
        } );
        builder.show();
    }


}
