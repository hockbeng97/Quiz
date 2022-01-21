package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class JoinClass extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ClassroomModel, JoinClassViewHolder> adapter;
    Button joinBtn;
    LayoutInflater customView;
    View view;
    Map <String, String> objectMap;
    Map <String, Object> objectMap1;
    String uid;
    static String classId, class_pin;
    static TextView classPin;
    static int count;
    AlertDialog.Builder dialog;
    static int check_user_exist, check_host;
    TextView myClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_join_class );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra( "userId" );

        myClass = findViewById( R.id.myClass );
        myClass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyClass();
            }
        } );

        //-------- Join BUTTON Start ----------------------
        joinBtn = findViewById( R.id.joinBtn );

        joinBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(JoinClass.this);
                customView =  JoinClass.this.getLayoutInflater();
                view = customView.inflate(R.layout.floating_join_class, null);
                dialog.setView(view);
                dialog.setTitle( "Add Classroom" );
                View title_view = customView.inflate(R.layout.classroom_join, null);
                dialog.setCustomTitle( title_view );

                classPin = (TextView) view.findViewById( R.id.name_input );

                dialog.setPositiveButton( "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPin();
                    }
                } );

                dialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );

                dialog.show();

            }
        } );

        //-------- Join BUTTON End ----------------------


        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "joinClass" );

        Query query1 = userRef.whereEqualTo( "userId", uid ).orderBy( "joined_date", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ClassroomModel>().setQuery( query1, ClassroomModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ClassroomModel, JoinClassViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final JoinClassViewHolder joinClassViewHolder, final int position, @NonNull final ClassroomModel classroomModel) {

                String cid = getSnapshots().getSnapshot(position).getString( "classId" ) ;

                Toast.makeText( JoinClass.this, "CID " + cid, Toast.LENGTH_SHORT ).show();

                userRef = objectFirebaseFirestore.collection( "classrooms" );

                if( cid != null ) {

                    userRef.document( cid ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            String name = documentSnapshot.getString( "name" );
                            String pin = documentSnapshot.getString( "pin" );
                            joinClassViewHolder.name.setText( name );
                            joinClassViewHolder.pin.setText( pin );

                            try {
                                joinClassViewHolder.date.setText( classroomModel.getJoined_date() );
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } );
                }

                joinClassViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cid = getSnapshots().getSnapshot( position ).getString( "classId" );
                        openAnnouncement( cid );
                    }
                } );

            }

            @NonNull
            @Override
            public JoinClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_join_class, parent, false );
                return new JoinClassViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class JoinClassViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        TextView pin;
        TextView date;

        public JoinClassViewHolder(@NonNull View itemView) {
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
        JoinClass.this.finish();
    }

    public void checkPin(){

        class_pin = classPin.getText().toString();
        count = 0;

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String dbgamePin = documentSnapshot.getString( "pin" );
                    if(class_pin.equals( dbgamePin )){
                        classId = documentSnapshot.getId();
                        count++;
                        break;
                    }
                }


                //Toast.makeText( JoinClass.this, "count "+ count, Toast.LENGTH_SHORT ).show();
                if(count == 0){
                    Toast.makeText( JoinClass.this, "Please Enter Correct Pin ", Toast.LENGTH_SHORT ).show();
                }else{
                    Toast.makeText( JoinClass.this, "Valid PIN ", Toast.LENGTH_SHORT ).show();
                    getClassId();
                }
            }
        } );

    }

    public void getClassId(){

        userRef = objectFirebaseFirestore.collection( "classrooms" );

        userRef.whereEqualTo( "pin", class_pin ).get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for(DocumentSnapshot documentSnapshot: task.getResult()) {
                    String dbcid = documentSnapshot.getId();
                    String dbuid = documentSnapshot.getString( "userId" );

                    if (dbcid.equals( classId ) && dbuid.equals( uid )) {
                        check_host = 1;
                        //Toast.makeText( JoinClass.this, "class host: ", Toast.LENGTH_SHORT ).show();
                        break;
                    } else {
                        check_host = 0;
                    }
                }
            }
        } );

        //-- check user exist in class or not
        userRef = objectFirebaseFirestore.collection( "joinClass" );
        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){

                    String dbuserId = documentSnapshot.getString( "userId" );
                    String dbclassId = documentSnapshot.getString( "classId" );

                    if(dbuserId.equals( uid ) && dbclassId.equals( classId )) {
                        check_user_exist = 1;
                        Toast.makeText( JoinClass.this, "You already in the Class ", Toast.LENGTH_SHORT ).show();
                        break;
                    }else{
                        check_user_exist = 0;
                    }
                }
            }
        }).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                checkHost();
            }
        } );


    }

    public void checkHost(){

        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    String dbcid = documentSnapshot.getId();
                    String dbuid = documentSnapshot.getString( "userId" );

                    if(dbcid.equals( classId ) && dbuid.equals( uid )){
                        check_host = 1;
                        Toast.makeText( JoinClass.this, "You are the Class Host ", Toast.LENGTH_SHORT ).show();
                        break;
                    }else{
                        check_host = 0;
                    }
                }

            }
        } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Toast.makeText( JoinClass.this, "joinclass: " + check_user_exist + "host " + check_host, Toast.LENGTH_SHORT ).show();

                if( check_user_exist == 1 || check_host == 1 ){
                    Toast.makeText( JoinClass.this, "You already in the class", Toast.LENGTH_SHORT ).show();
                }else{
                    insertClassId();
                }

            } });


    }


    public void insertClassId(){

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        class_pin = classPin.getText().toString();

        objectMap = new HashMap<>();
        userRef = objectFirebaseFirestore.collection( "joinClass" );
        DocumentReference ref = userRef.document();

        objectMap.put("joined_date", timestamp.toString());
        objectMap.put("userId", uid);
        objectMap.put("classId", classId);

        ref.set( objectMap );

        Toast.makeText( JoinClass.this, "You have joined", Toast.LENGTH_SHORT ).show();
        //-----------------------
    }

    public void openMyClass(){
        Intent intent = new Intent(this, Classroom.class);
        intent.putExtra("userId", uid);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
        this.finish();
    }

    public void openAnnouncement(String cid){

        Intent intent = new Intent( this, JoinClassAnnouncement.class );
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
    }

}