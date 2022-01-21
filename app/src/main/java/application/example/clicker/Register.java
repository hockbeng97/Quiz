package application.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    FirebaseFirestore objectFirebaseFirestore;
    DocumentReference objectDocumentReference;
    int count = 0;
    CollectionReference userRef;

    EditText txtemail;
    String email;

    EditText txtpassword;
    String password;

    EditText txtusername;
    String username;

    EditText txtschool;
    String school;

    TextView txtcheck;

    Map<String, String> objectMap;

    User user;
    String dbemail;

    int check=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        txtemail = findViewById(R.id.editText6);
        email = txtemail.getText().toString();

        txtpassword = findViewById(R.id.editText7);
        password = txtpassword.getText().toString();

        txtusername = findViewById(R.id.editText3);
        username = txtusername.getText().toString();

        txtschool = findViewById(R.id.editText4);
        school = txtschool.getText().toString();


        txtemail.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtemail.setError( null );

            }
        } );

        txtpassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtpassword.setError( null );
            }
        } );

        txtusername.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtusername.setError( null );
            }
        } );


    }



    public void addValue(View view){

        txtemail = findViewById(R.id.editText6);
        email = txtemail.getText().toString();

        txtpassword = findViewById(R.id.editText7);
        password = txtpassword.getText().toString();

        txtusername = findViewById(R.id.editText3);
        username = txtusername.getText().toString();

        txtschool = findViewById(R.id.editText4);
        school = txtschool.getText().toString();

        try{
            //-------------------------- Insert ----------------------------------------

            objectMap = new HashMap<>();
            userRef = objectFirebaseFirestore.collection("user");

                objectMap.put("email", email);
                objectMap.put("password", password);
                objectMap.put("username", username);
                objectMap.put("school", school);
                //------- checking process start-----------


            userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int count = 0;
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        User user = documentSnapshot.toObject(User.class);
                        String dbemail = user.getEmail();

                        if(email.equals(dbemail)) {
                            count++;
                        }

                    } // for loop close

                    Button btnreg = (Button) findViewById(R.id.button);

                    if(count>0){
                        txtemail.setError("This email address has been registered");
                        txtemail.requestFocus();
                    }
                    if(email.isEmpty()){
                        txtemail.setError("This field is required to fill");
                        txtemail.requestFocus();
                    }

                    if(password.isEmpty()){
                        txtpassword.setError("This field is required to fill");
                        txtpassword.requestFocus();
                    }

                    if(username.isEmpty()){
                        txtusername.setError("This field is required to fill");
                        txtusername.requestFocus();
                    }

                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() != true){
                        txtemail.setError("Please Enter a Valid Email");
                        txtemail.requestFocus();
                    }

                    if(password.length() < 6){
                        txtpassword.setError("You password must be more than 6 characters");
                        txtpassword.requestFocus();
                    }

                     if (!email.equals(dbemail) && !email.isEmpty() && !password.isEmpty() && !username.isEmpty()
                             && password.length() > 6 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == true){
                        // ------------------ INSERT PROCESS START -------------------------------
                        objectFirebaseFirestore
                                .collection("user")
                                .add(objectMap)
                                .addOnSuccessListener(
                                        new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                openLogin();
                                                Toast.makeText(Register.this, "Account is created. Please Login", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(Register.this, "Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //----------------------- INSERT PROCESS END ---------------------
                    }
                }
            });
//------- checking process end -----------

        }//try close

        catch(Exception e){
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }




    //------------------------------------------- RETRIEVE DATA -------------------------------



    public void getValue(View view) {

        userRef = objectFirebaseFirestore.collection("user");
        txtcheck = (TextView) findViewById(R.id.txtcheck);


            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String data="";

                            for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                User user = documentSnapshot.toObject(User.class);
                                String dbemail = user.getEmail();

                                data += "email: " + dbemail +"\n";
                            }
                            txtcheck.setText(data);
                        }
                    });
    }

    public void openLogin(){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }



}

