package application.example.clicker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.eventbus.EventBus;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView2;
    DocumentReference objectDocumentReference;
    CollectionReference userRef;
    Map<String, String> objectMap;
    EditText txtemail;
    EditText txtpass;
    String email;
    String pass;
    String dbusername;
    String documentId;
    FirebaseFirestore objectFirebaseFirestore;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });


        txtemail = (EditText) findViewById(R.id.editText10);
        txtpass = (EditText) findViewById(R.id.editText11);



        userRef = objectFirebaseFirestore.collection("user");

        Button btnlogin = (Button) findViewById(R.id.button2);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int count = 0;
                        int countId = 0;
                        email = txtemail.getText().toString();
                        pass = txtpass.getText().toString();


                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            User user = documentSnapshot.toObject(User.class);
                            String dbemail = user.getEmail();
                            String dbpass = user.getPassword();


                            if(email.equals(dbemail) && pass.equals(dbpass)) {
                                dbusername = user.getUsername();
                                documentId = queryDocumentSnapshots.getDocuments().get(countId).getId();
                                count++;
                            }
                            countId++;
                        } // for loop close


                        if(count>0) {
                            Toast.makeText(MainActivity.this, "Welcome " + dbusername + " !", Toast.LENGTH_SHORT).show();
                            openHome();
                            MainActivity.this.finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }


    public void openRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void openHome(){
        Intent intent = new Intent (this, Home.class);
        intent.putExtra("userId", documentId);
        Toast.makeText(MainActivity.this, "UID " + documentId, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    //----- Broadcast ---
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if(dialog == null){
                dialog = new Dialog( context );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_check_internet);
            }

            if(ni != null){
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable( true );
                dialog.dismiss();
            }else{
                Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable( false );
                dialog.show();
            }
        }
    };

    //-- Broadcast ------


}