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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    TextView className;
    TextView hosted, report, participant;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter <ClassroomModel, AnnouncementViewHolder> adapter;
    LayoutInflater customView;
    View view;
    Button addBtn;
    Map<String, String> objectMap;
    String announceId;
    String cid;
    String cname;
    String chost;
    TextView announce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_announcement );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        cname = intent.getStringExtra( "className" );
        chost = intent.getStringExtra( "classHost" );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.hosted );
        report = findViewById( R.id.report );
        participant = findViewById( R.id.participant );

        className.setText( cname );

        userRef = objectFirebaseFirestore.collection( "user" );
        userRef.document( chost ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String username = documentSnapshot.getString( "username" );
                hosted.setText( "Hosted by : " + username );
            }
        } );

        // Report clicked

        report.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeworkReport();
            }
        } );

        participant.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClassroomDetail();
            }
        } );


       // --------------  Add Announcement -------------------------------
        addBtn = findViewById( R.id.addBtn );
        addBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder( Announcement.this );
                customView = Announcement.this.getLayoutInflater();
                view = customView.inflate( R.layout.floating_add_announcement, null );
                dialog.setView( view );
                View title_view = customView.inflate( R.layout.classroom_title, null );
                dialog.setCustomTitle( title_view );
                announce = (TextView) view.findViewById( R.id.announcement_input );

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

                        String strUserInput = announce.getText().toString().trim();

                        if(TextUtils.isEmpty(strUserInput)) {
                            Snackbar snackbar = Snackbar.make(view, "Field cannot be left blank", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else{

                            final String txt_announce = announce.getText().toString();

                            userRef = objectFirebaseFirestore.collection( "announcement" );
                            Timestamp timestamp = new Timestamp( System.currentTimeMillis() );

                            DocumentReference ref = userRef.document();
                            announceId = ref.getId();

                            objectMap = new HashMap<>();
                            objectMap.put( "announcement", txt_announce );
                            objectMap.put( "classId", cid );
                            objectMap.put( "created_date", timestamp.toString() );
                            ref.set( objectMap );

                            Toast.makeText( Announcement.this, "Announcement Added", Toast.LENGTH_SHORT ).show();
                            alertDialog.cancel();
                        }
                    }
                } );


            }
        });
        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "announcement" );

        Query query1 = userRef.whereEqualTo( "classId", cid ).orderBy( "created_date", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ClassroomModel>().setQuery( query1, ClassroomModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ClassroomModel, AnnouncementViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final AnnouncementViewHolder classroomViewHolder, final int position, @NonNull final ClassroomModel classroomModel) {

                classroomViewHolder.announcement.setText( classroomModel.getAnnouncement() );
                try {
                    classroomViewHolder.created_date.setText( classroomModel.getCreated_date() );
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                classroomViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String announceId = getSnapshots().getSnapshot( position ).getId();
                        openEditAnnouncement(announceId);
                    }
                } );
            }
            @NonNull
            @Override
            public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_announcement, parent, false );
                return new AnnouncementViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class AnnouncementViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView announcement;
        TextView created_date;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            announcement = itemView.findViewById( R.id.announcement );
            created_date = itemView.findViewById( R.id.created_date1 );
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
        Announcement.this.finish();
    }

    public void openEditAnnouncement(String announceId){
        Intent intent = new Intent (this, EditAnnouncement.class);
        intent.putExtra( "announcementId", announceId );
        startActivity(intent);
    }

    public void openHomeworkReport(){
        Intent intent = new Intent( this, HomeworkReport.class );
        intent.putExtra( "classId", cid );
        intent.putExtra( "className", cname );
        intent.putExtra( "classHost", chost );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }

    public void openClassroomDetail(){
        Intent intent = new Intent(this, ClassroomDetail.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "className", cname );
        intent.putExtra( "classHost", chost );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
        this.finish();
    }


}
