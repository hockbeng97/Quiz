package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeworkReport extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore objectFirebaseFirestore;
    private FirestoreRecyclerAdapter<ReportModel, HomeworkReportViewHolder> adapter;
    CollectionReference userRef;
    String cid, cname, chost;
    TextView className, hosted, report, participant, announcement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_homework_report );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        cname = intent.getStringExtra( "className" );
        chost = intent.getStringExtra( "classHost" );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.hosted );
        report = findViewById( R.id.report );
        participant = findViewById( R.id.participant );
        announcement = findViewById( R.id.announcement );

        className.setText( cname );

        userRef = objectFirebaseFirestore.collection( "user" );
        userRef.document( chost ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String username = documentSnapshot.getString( "username" );
                hosted.setText( "Hosted by : " + username );
            }
        } );

        participant.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClassroomDetail();
            }
        } );

        announcement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAnnouncement();
            }
        } );


        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "homework" );

        Query query1 = userRef.whereEqualTo( "classId", cid ).orderBy( "due_date_time", Query.Direction.DESCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ReportModel>().setQuery( query1, ReportModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ReportModel, HomeworkReportViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final HomeworkReportViewHolder homeworkReportViewHolder, final int position, @NonNull final ReportModel reportModel) {

                homeworkReportViewHolder.homeworkTitle.setText( reportModel.getTitle() );

                // get due date

                Date due_date = reportModel.getDue_date_time();
                SimpleDateFormat sdf1 = new SimpleDateFormat( "dd-MM-yyyy HH:mm" );
                String date = sdf1.format( due_date );

                homeworkReportViewHolder.date.setText( "Due Date:  " + date );

                String qid = getSnapshots().getSnapshot(position).getString( "quizId" ) ;

                userRef = objectFirebaseFirestore.collection( "homeworkQuiz" );

                userRef.document( qid ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        String title = documentSnapshot.getString("title");
                        homeworkReportViewHolder.quizName.setText( title );
                    }
                } );

                homeworkReportViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hid = getSnapshots().getSnapshot(position).getId();
                        String qid = getSnapshots().getSnapshot(position).getString( "quizId" ) ;
                        reportDetails(qid, hid);
                    }
                } );

            }

            @NonNull
            @Override
            public HomeworkReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_homework_report, parent, false );
                return new HomeworkReportViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class HomeworkReportViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView homeworkTitle;
        TextView quizName;
        TextView date;

        public HomeworkReportViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            homeworkTitle = itemView.findViewById( R.id.homework_title );
            quizName = itemView.findViewById( R.id.quizName );
            date = itemView.findViewById( R.id.date );
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
        HomeworkReport.this.finish();
    }

    public void reportDetails(String quizId, String homeworkId){
        Intent intent = new Intent(this, HomeworkReportDetail.class);
        intent.putExtra( "quizId", quizId );
        intent.putExtra( "homeworkId", homeworkId );
        startActivity( intent );
    }

    public void openAnnouncement(){
        Intent intent = new Intent (this, Announcement.class);
        intent.putExtra("classId", cid);
        intent.putExtra("className", cname);
        intent.putExtra("classHost", chost);
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
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
