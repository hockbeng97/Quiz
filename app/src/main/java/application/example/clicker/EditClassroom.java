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

public class EditClassroom extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    TextView classroomName;
    Button updateBtn, deleteBtn;
    String cid;
    CollectionReference userRef;
    Map<String, Object> objectMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_classroom );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );

        classroomName = findViewById( R.id.classroomName );
        updateBtn = findViewById( R.id.updateBtn );
        deleteBtn = findViewById( R.id.deleteBtn );

        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( EditClassroom.this );
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
                        deleteClassroom();
                    }
                } );
                builder.show();
            }
        } );

        retrieveAnoouncement();

        updateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClassroom();
            }
        } );
    }


    public void retrieveAnoouncement(){

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        userRef.document(cid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString( "name" );
                classroomName.setText( name );
            }
        } );
    }




    public void updateClassroom(){

        String txt_name = classroomName.getText().toString();

        userRef = objectFirebaseFirestore.collection( "classrooms" );
        objectMap = new HashMap<>();
        objectMap.put( "name",  txt_name);
        userRef.document(cid).update( objectMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText( EditClassroom.this, "Classroom Updated", Toast.LENGTH_SHORT ).show();
                EditClassroom.this.finish();
            }
        } );
    }

    public void deleteClassroom(){

        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.document(cid).delete();
        Toast.makeText( this, "Classroom Deleted", Toast.LENGTH_SHORT ).show();

        EditClassroom.this.finish();
    }

}
