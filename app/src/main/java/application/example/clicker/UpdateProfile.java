package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    TextView txtemail, txtusername, txtuni, txtpassword, txtpassword1;
    Button saveBtn;
    String userId;
    Map <String, Object> objectMap;
    String email, username, uni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_profile );

        Intent intent = getIntent();
        userId = intent.getStringExtra( "userId" );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        txtemail = findViewById( R.id.txtEmail );
        txtusername = findViewById( R.id.txtusername );
        txtuni = findViewById( R.id.txtuni );
        txtpassword = findViewById( R.id.txtpassword );
        txtpassword1 = findViewById( R.id.txtpassword1 );
        saveBtn = findViewById( R.id.saveBtn );

        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
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

                txtemail.setText( dbemail );
                txtusername.setText( dbusername );
                txtuni.setText( dbuni );
            }
        } );

    }


    public void updateUser(){

        objectMap = new HashMap<>();

        email = txtemail.getText().toString();
        username = txtusername.getText().toString();
        uni = txtuni.getText().toString();

        objectMap.put("email", email);
        objectMap.put("username", username);
        objectMap.put("school", uni);

        userRef = objectFirebaseFirestore.collection( "user" );

        userRef.document(userId).update( objectMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        } ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                openHome();
            }
        } );

    }

    public void openHome(){
        this.finish();
        Intent back = new Intent( this, view_Profile.class );
        back.putExtra( "userId", userId );
        startActivity( back );
    }



}
