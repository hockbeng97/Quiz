package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Classroom extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ClassroomModel, ClassroomViewHolder> adapter;
    String uid;
    Button addBtn;
    Map <String, String> objectMap;
    Map <String, Object> objectMap1;
    LayoutInflater customView;
    View view;
    static String gamePin, classroomId;
    TextView joinedClass;
    List<String> pin;
    static int countPin;
    TextView className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_classroom );

        Intent intent = getIntent();
        uid = intent.getStringExtra( "userId" );
        Toast.makeText( this, "UID : "+ uid, Toast.LENGTH_SHORT ).show();

        pin = new ArrayList<>();

        generatePin();

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        addBtn = findViewById( R.id.addBtn );

        joinedClass = findViewById( R.id.joinedClass );
        joinedClass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinClass();
            }
        } );

        addBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Classroom.this);
                customView =  Classroom.this.getLayoutInflater();
                view = customView.inflate(R.layout.floating_add_classroom, null);
                dialog.setView(view);
                dialog.setTitle( "Add Classroom" );
                View title_view = customView.inflate(R.layout.classroom_title, null);
                dialog.setCustomTitle( title_view );
                className = (TextView) view.findViewById( R.id.name_input );

                dialog.setPositiveButton( "Add", null);
                dialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );

                // Check whether is null or not
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strUserInput = className.getText().toString().trim();
                        if(TextUtils.isEmpty(strUserInput)) {
                            Snackbar snackbar = Snackbar.make(view, "Field cannot be left blank", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else{
                            final String class_name = className.getText().toString();

                            userRef = objectFirebaseFirestore.collection( "classrooms" );
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                            DocumentReference ref = userRef.document();
                            classroomId = ref.getId();

                            objectMap = new HashMap<>();
                            objectMap.put("name", class_name);
                            objectMap.put("userId", uid);
                            objectMap.put("created_date", timestamp.toString());
                            ref.set( objectMap );

                            checkPin( );

                            Toast.makeText( Classroom.this, "Class " + class_name + " added", Toast.LENGTH_SHORT ).show();
                            alertDialog.cancel();
                        }
                    }
                } );

            }
        } );


        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        Query query1 = userRef.whereEqualTo( "userId", uid ).orderBy( "created_date", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ClassroomModel>().setQuery( query1, ClassroomModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ClassroomModel, ClassroomViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ClassroomViewHolder classroomViewHolder, final int position, @NonNull ClassroomModel classroomModel) {
                classroomViewHolder.name.setText( classroomModel.getName() );
                classroomViewHolder.pin.setText( classroomModel.getPin() );

                try {
                    classroomViewHolder.date.setText( (CharSequence) classroomModel.getCreated_date() );
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                classroomViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cid = getSnapshots().getSnapshot(position).getId();
                        String className = getSnapshots().getSnapshot( position ).getString( "name" );
                        String classHost = getSnapshots().getSnapshot( position ).getString( "userId" );
                        openClassroomDetails(cid, className, classHost);
                    }
                } );

                classroomViewHolder.view.setOnLongClickListener( new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String cid = getSnapshots().getSnapshot(position).getId();
                        Intent intent = new Intent (Classroom.this, EditClassroom.class);
                        intent.putExtra( "classId", cid );
                        startActivity( intent );
                        return false;
                    }
                } );
            }

            @NonNull
            @Override
            public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_class, parent, false );
                return new ClassroomViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class ClassroomViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        TextView pin;
        TextView date;

        public ClassroomViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            name = itemView.findViewById( R.id.name );
            pin = itemView.findViewById( R.id.enter_pin );
            date = itemView.findViewById( R.id.enter_date );
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
    public void onBackPressed() {
        super.onBackPressed();
        Classroom.this.finish();
    }

    public void generatePin() {

        Random random = new Random();

        int number = random.nextInt( 999999 );

        gamePin = String.format( "%06d", number );
    }

    public void checkPin() {

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String dbpin = documentSnapshot.getString( "pin" );
                    pin.add(dbpin);
                }

                countPin = 0;

                for(int a = 0; a<pin.size(); a++ ){
                    if(gamePin.equals( pin.get( a ) )){
                        countPin = 0;
                        break;
                    }else{
                        countPin = 1;
                    }
                }


                if(countPin == 0){
                    generatePin();
                    checkPin();
                }
                else{
                    insertPin( );
                }
            }


        } );

    }

    public void insertPin() {

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        objectMap1 = new HashMap<>();
        objectMap1.put( "pin", gamePin );

        DocumentReference ref = userRef.document(classroomId);
        ref.update( objectMap1 );
    }

    public void openJoinClass(){
        Intent intent = new Intent (this, JoinClass.class);
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }

    public void openClassroomDetails(String cid, String className, String classHost){
        Intent intent = new Intent (this, ClassroomDetail.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "className", className );
        intent.putExtra( "classHost", classHost );
        //Toast.makeText( this, "CH IS " + classHost, Toast.LENGTH_SHORT ).show();
        startActivity( intent );
    }

}