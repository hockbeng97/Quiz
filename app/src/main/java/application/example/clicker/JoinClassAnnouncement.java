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

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;

public class JoinClassAnnouncement extends AppCompatActivity {

    TextView className, hosted, homework, result1;
    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    String cid, chost, uid;
    RecyclerView recyclerView;
    private  FirestoreRecyclerAdapter <ClassroomModel, AnnouncementViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_join_class_announcement );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        uid = intent.getStringExtra( "userId" );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.txt_hosted );
        homework = findViewById( R.id.homework );

        homework.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomework();
            }
        } );

        result1 = findViewById( R.id.result );

        result1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResult();
            }
        } );



        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.document(cid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cname = documentSnapshot.getString( "name" );
                chost = documentSnapshot.getString( "userId" );
                getUsername( chost );
                className.setText( cname );
            }
        } );


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

            }
            @NonNull
            @Override
            public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_join_announcement, parent, false );
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
        JoinClassAnnouncement.this.finish();
    }

    public void getUsername(String chost){

        userRef = objectFirebaseFirestore.collection( "user" );
        userRef.document( chost ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String username = documentSnapshot.getString( "username" );
                hosted.setText( username );
            }
        } );

    }

    public void openHomework(){
        Intent intent = new Intent( this, StudentHomework.class );
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    public void openResult(){
        Intent intent = new Intent (this, HomeworkResult.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }


}