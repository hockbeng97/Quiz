package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.ParseException;

public class ClassroomDetail extends AppCompatActivity {

    private FirestoreRecyclerAdapter<ClassroomModel, ClassroomDetailViewHolder> adapter;
    RecyclerView recyclerView;
    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    TextView className, hosted, report;
    TextView announceClick;
    String cid;
    String cname;
    String chost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_classroom_detail );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        announceClick = findViewById( R.id.announcement );

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        cname = intent.getStringExtra( "className" );
        chost = intent.getStringExtra( "classHost" );


        // ------  Direct to Announcement Activity ------------------------

        announceClick.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAnnouncement();
            }
        } );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.hosted );
        report = findViewById( R.id.report );

        className.setText( cname );

        userRef = objectFirebaseFirestore.collection( "user" );

        userRef.document(chost).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String username = documentSnapshot.getString( "username" );
                hosted.setText( "Hosted by : " + username );
            }
        } );

        report.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeworkReport();
            }
        } );


        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "joinClass" );

        Query query1 = userRef.whereEqualTo( "classId", cid ).orderBy( "joined_date", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ClassroomModel>().setQuery( query1, ClassroomModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ClassroomModel, ClassroomDetailViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ClassroomDetailViewHolder classroomViewHolder, final int position, @NonNull final ClassroomModel classroomModel) {

                String dbuid = getSnapshots().getSnapshot(position).getString( "userId" );

                userRef = objectFirebaseFirestore.collection( "user" );

                userRef.document(dbuid).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        String username = documentSnapshot.getString( "username" );
                        classroomViewHolder.name.setText( username );
                    }
                } );

                classroomViewHolder.kickLayout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder( ClassroomDetail.this );
                        builder.setTitle( "Alert" );
                        builder.setMessage( "Are you sure to remove this student ?" );
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
                                String joinClassId = getSnapshots().getSnapshot( position ).getId();
                                kickParticipant(joinClassId);
                            }
                        } );
                        builder.show();
                    }
                } );
            }
            @NonNull
            @Override
            public ClassroomDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_student, parent, false );
                return new ClassroomDetailViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class ClassroomDetailViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        RelativeLayout kickLayout;

        public ClassroomDetailViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            name = itemView.findViewById( R.id.list_name );
            kickLayout = itemView.findViewById( R.id.kickLayout );
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
        ClassroomDetail.this.finish();
    }

    public void kickParticipant(String joinClassId){

        userRef = objectFirebaseFirestore.collection( "joinClass" );

        userRef.document(joinClassId).delete();
    }

    public void openAnnouncement(){
        Intent intent = new Intent (this, Announcement.class);
        intent.putExtra("classId", cid);
        intent.putExtra("className", cname);
        intent.putExtra("classHost", chost);
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }

    public void openHomeworkReport(){
        Intent intent = new Intent( this, HomeworkReport.class );
        intent.putExtra( "classId", cid );
        intent.putExtra("className", cname);
        intent.putExtra("classHost", chost);
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }

}
