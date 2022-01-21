package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class HomeworkResult extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    String cid, uid, chost;
    TextView className, hosted, homework, announement;
    CollectionReference userRef;
    private FirestoreRecyclerAdapter<HomeworkModel, HomeworkResultViewHolder> adapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_homework_result );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        uid = intent.getStringExtra( "userId" );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.txt_hosted );
        homework = findViewById( R.id.homework );
        announement = findViewById( R.id.announcement );

        announement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice();
            }
        } );

        homework.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomework();
            }
        } );


        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.document( cid ).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
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

        userRef = objectFirebaseFirestore.collection( "homework" );

        Query query = userRef.whereEqualTo( "classId", cid )
                .whereEqualTo( "state", "due" )
                .orderBy( "due_date_time", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<HomeworkModel>().setQuery( query, HomeworkModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<HomeworkModel, HomeworkResultViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final HomeworkResultViewHolder homeworkResultViewHolder, final int position, @NonNull final HomeworkModel homeworkModel) {

                homeworkResultViewHolder.title.setText( homeworkModel.getTitle() );
                String quizId = getSnapshots().getSnapshot( position ).getString( "quizId" );

                userRef = objectFirebaseFirestore.collection( "homeworkQuiz" );

                userRef.document(quizId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String quizTitle = documentSnapshot.getString( "title" );
                        homeworkResultViewHolder.quizName.setText( quizTitle );
                    }
                } );

                homeworkResultViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quizId  = getSnapshots().getSnapshot( position ).getString( "quizId" );
                        String homeworkTitle = getSnapshots().getSnapshot( position ).getString( "title" );
                        String homeworkId = getSnapshots().getSnapshot( position ).getId();
                        openResultDetail( quizId, homeworkTitle , homeworkId);
                    }
                } );

            }

            @NonNull
            @Override
            public HomeworkResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_homework_result, parent, false );
                return new HomeworkResultViewHolder( v );
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter( adapter );
    }


    private class HomeworkResultViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView title;
        TextView quizName;

        public HomeworkResultViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            title = itemView.findViewById( R.id.homework_title );
            quizName = itemView.findViewById( R.id.quizName );
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
        HomeworkResult.this.finish();
    }

    public void getUsername(String chost) {

        userRef = objectFirebaseFirestore.collection( "user" );
        userRef.document( chost ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String username = documentSnapshot.getString( "username" );
                hosted.setText( username );
            }
        } );
    }

    public void openResultDetail(String quizId, String homeworkTitle, String homeworkId){
        Intent intent = new Intent(this, HomeworkResultDetail.class);
        intent.putExtra( "quizId",  quizId);
        intent.putExtra( "homeworkTitle",  homeworkTitle);
        intent.putExtra( "homeworkId", homeworkId );
        intent.putExtra( "userId", uid );
        startActivity(intent);
    }

    public void openNotice(){
        Intent intent = new Intent (this, HomeworkResult.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
        this.finish();
    }

    public void openHomework(){
        Intent intent = new Intent( this, StudentHomework.class );
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();
    }

}
