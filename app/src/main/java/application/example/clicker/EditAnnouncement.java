package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class EditAnnouncement extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    String aid;
    Map <String, Object> objectMap;
    TextView announcement_input;
    Button updateBtn, deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_announcement );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        aid = intent.getStringExtra( "announcementId" );

        announcement_input = findViewById( R.id.announcement_input );
        updateBtn = findViewById( R.id.updateBtn );
        deleteBtn = findViewById( R.id.deleteBtn );

        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( EditAnnouncement.this );
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
                        deleteAnnouncement();
                    }
                } );
                builder.show();

            }
        } );

        retrieveAnoouncement();

        updateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnnouncement();
            }
        } );
    }


    public void retrieveAnoouncement(){

        userRef = objectFirebaseFirestore.collection( "announcement" );

        userRef.document(aid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String announcement = documentSnapshot.getString( "announcement" );
                announcement_input.setText( announcement );
            }
        } );
    }




    public void updateAnnouncement(){

        String txt_announcement = announcement_input.getText().toString();

        userRef = objectFirebaseFirestore.collection( "announcement" );
        objectMap = new HashMap<>();
        objectMap.put( "announcement",  txt_announcement);
        userRef.document(aid).update( objectMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText( EditAnnouncement.this, "Announcement Updated", Toast.LENGTH_SHORT ).show();
                EditAnnouncement.this.finish();
            }
        } );
    }

    public void deleteAnnouncement(){

        userRef = objectFirebaseFirestore.collection( "announcement" );
        userRef.document(aid).delete();
        Toast.makeText( this, "Announcement Deleted", Toast.LENGTH_SHORT ).show();
        EditAnnouncement.this.finish();
    }


}
