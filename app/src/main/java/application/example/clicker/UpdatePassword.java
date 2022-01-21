package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdatePassword extends AppCompatActivity {

    String userId;
    FirebaseFirestore objectFirebaseFirestore;
    DocumentReference docRef;
    EditText txtoldpw, txtpassword, txtpassword1;
    CollectionReference userRef;
    String dbpw;
    String oldpw, pw, pw1;
    Button saveBtn;
    boolean check = true;
    Map<String, Object> objectMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_password );

        Intent intent = getIntent();
        userId = intent.getStringExtra( "userId" );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        txtoldpw = findViewById( R.id.txtoldpw );
        txtpassword = findViewById( R.id.txtpassword );
        txtpassword1 = findViewById( R.id.txtpassword1 );

        saveBtn = findViewById( R.id.saveBtn );

        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPassword();
            }
        } );


    }


    public void checkPassword(boolean check){

        userRef = objectFirebaseFirestore.collection( "user" );

        pw = txtpassword.getText().toString();
        pw1 = txtpassword1.getText().toString();

        objectMap = new HashMap<>();

        objectMap.put("password", pw);

        if(!pw.equals( pw1 )){
            Toast.makeText( UpdatePassword.this, "New Password not match", Toast.LENGTH_SHORT ).show();
        }

        else if(pw.equals( pw1 ) && check == true){
            userRef.document(userId).update( objectMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText( UpdatePassword.this, "Update Successfully", Toast.LENGTH_SHORT ).show();
                }
            } ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    openHome();
                }
            } );
        }

    }


    public void getPassword(){

        userRef = objectFirebaseFirestore.collection( "user" );

        oldpw = txtoldpw.getText().toString();

        userRef.document(userId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dbpw = documentSnapshot.getString( "password" );

            }
        } ).addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Toast.makeText( UpdatePassword.this, "DB PW : " + dbpw + " old pw : " + oldpw, Toast.LENGTH_SHORT ).show();

                if(!oldpw.equals( dbpw )) {
                    check = false;
                    checkPassword( check );
                    Toast.makeText( UpdatePassword.this, "Your Old Password is Wrong", Toast.LENGTH_SHORT ).show();
                }else {
                    check = true;
                    checkPassword( check );
                }
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
