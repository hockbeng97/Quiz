package application.example.clicker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class CreateFragment extends Fragment {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    Intent userId;
    static String joinGameID;
    EditText enterPin;
    String pinNumber;
    Button btnPin;
    Map <String, String> objectMap;
    Map <String, Object> objectMap1;
    Map <String, Integer> objectMap2;
    String uid;
    static String gameId;


    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_message, container, false);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        userId = getActivity().getIntent();
        uid = userId.getStringExtra("userId");

        enterPin = root.findViewById( R.id.enterPin );
        btnPin = root.findViewById( R.id.btnPin );



        btnPin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinNumber = enterPin.getText().toString();
                if(pinNumber.equals( null )){
                    enterPin.setError( "Please fill in this field" );
                    enterPin.requestFocus();
                }else if (! pinNumber.equals( null )){
                    checkPin();
                }
            }
        } );

        enterPin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPin.setError( null );
            }
        } );

        RelativeLayout rl_create = (RelativeLayout) root.findViewById(R.id.create);
            rl_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreate();
            }
        });


        RelativeLayout rl_myQuiz = (RelativeLayout) root.findViewById(R.id.myQuiz);
            rl_myQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                openQuiz();
            }
        });

            RelativeLayout report = root.findViewById( R.id.report );
            report.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openReport();
                }
            } );

        RelativeLayout classroom = root.findViewById( R.id.classroom );
        classroom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClass();
            }
        } );

        return root;
    }

    public void openClass(){
        Intent classroom = new Intent (getActivity(), Classroom.class);
        classroom.putExtra( "userId", uid );
        startActivity( classroom );
    }

    public void openReport(){
        Intent report = new Intent (getActivity(), Report.class);
        report.putExtra( "userId", uid );
        startActivity( report );
    }


    public void openCreate(){
        Intent quiz = new Intent (getActivity(), CreateQuiz.class);
        quiz.putExtra("userId", uid);
        startActivity(quiz);
    }

    public void openQuiz(){
        Intent myQuiz = new Intent (getActivity(), MyQuiz.class);
        myQuiz.putExtra("userId", uid);
        startActivity(myQuiz);
    }

    public void checkPin(){

        userRef = objectFirebaseFirestore.collection( "game" );

        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int count = 0;

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String dbgamePin = documentSnapshot.getString( "pin" );
                    if(pinNumber.equals( dbgamePin )){
                        count++;
                    }
                }

                if(count == 0){
                    enterPin.setError( "Please enter correct PIN" );
                    Toast.makeText( getActivity(), "PIN "+pinNumber, Toast.LENGTH_SHORT ).show();
                    enterPin.requestFocus();
                }else{
                    getGameId();
                    enterPin.clearFocus();
                    enterPin.setText( "" );
                }
            }
        } );

    }

    public void waitingPlayer(){
        Intent intent = new Intent (getActivity(), WaitingPlayer.class);
        intent.putExtra( "joinGameID", joinGameID );
        intent.putExtra( "gameId", gameId );
        intent.putExtra( "gamePin", pinNumber );
        intent.putExtra( "userId", uid );
        startActivity( intent );
    }

    public void insertJoinGame() {

        objectMap = new HashMap<>();
        objectMap1 = new HashMap<>();

        DocumentReference ref = objectFirebaseFirestore.collection("joinGame").document();
        joinGameID = ref.getId();

        objectMap.put( "gameId", gameId );
        objectMap.put( "userId", uid );
        objectMap.put("pin", pinNumber);

        ref.set(objectMap);
    }

    public void getGameId(){

        userRef = objectFirebaseFirestore.collection( "game" );

        userRef.whereEqualTo( "pin", pinNumber ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){{
                    gameId = documentSnapshot.getId();
                    insertJoinGame();
                    getUsername();
                    waitingPlayer();
                    break;
                }
                }
            }
        } );

    }

    public void getUsername(){

        userRef = objectFirebaseFirestore.collection( "user" );

        userRef.document(uid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String username = documentSnapshot.getString( "username" );
                    insertUsername( username );
            }
        } );

    }


    public void insertUsername(String username){
        Toast.makeText( getActivity(), "JG ID : "+ joinGameID + " " + username, Toast.LENGTH_SHORT ).show();
        DocumentReference documentReference = objectFirebaseFirestore.collection( "joinGame" ).document(joinGameID);

        objectMap1 = new HashMap<>();

        objectMap1.put("username", username);

        documentReference.update( objectMap1 );
    }

}
