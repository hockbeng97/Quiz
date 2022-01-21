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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StudentHomework extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    private FirestoreRecyclerAdapter<HomeworkModel, HomeworkViewHolder> adapter;
    String cid, uid, chost;
    long start, end, left;
    static int check_attempt;
    TextView className, hosted, homework, result, announcement;
    Map <String, Object> objectMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_student_homework );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        result = findViewById( R.id.result );
        announcement = findViewById( R.id.announcement );

        Intent intent = getIntent();
        cid = intent.getStringExtra( "classId" );
        uid = intent.getStringExtra( "userId" );

        className = findViewById( R.id.className );
        hosted = findViewById( R.id.txt_hosted );
        homework = findViewById( R.id.homework );

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

        result.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeworkResult();
            }
        } );

        announcement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice();
            }
        } );


        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "homework" );

        Query query = userRef.whereEqualTo( "classId", cid ).whereEqualTo( "state", "progress" ).orderBy( "due_date_time", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<HomeworkModel>().setQuery( query, HomeworkModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<HomeworkModel, HomeworkViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final HomeworkViewHolder homeworkViewHolder, final int position, @NonNull final HomeworkModel homeworkModel) {

                homeworkViewHolder.title.setText( homeworkModel.getTitle() );

                //--- Current time -----
                Calendar c = Calendar.getInstance();
                Timestamp timestamp1 = new Timestamp( c.getTimeInMillis() );
                Date date = new Date( timestamp1.getTime() );
                start = date.getTime();


                //--- From Firestore time  -----
                Date date1 = new Date( homeworkModel.getDue_date_time().getTime() );
                end = date1.getTime();

                left = (end - start);

                Toast.makeText( StudentHomework.this, "left " + left, Toast.LENGTH_SHORT ).show();


                if (left > 0) {

                    new CountDownTimer( left, 1000 ) {

                        public void onTick(long millisUntilFinished) {

                            Long serverUptimeSeconds = (millisUntilFinished - 1) / 1000;

                            String daysLeft = String.format( "%d", serverUptimeSeconds / 86400 );
                            String hoursLeft = String.format( "%d", (serverUptimeSeconds % 86400) / 3600 );
                            String minutesLeft = String.format( "%d", ((serverUptimeSeconds % 86400) % 3600) / 60 );
                            String secondsLeft = String.format( "%d", ((serverUptimeSeconds % 86400) % 3600) % 60 );

                            homeworkViewHolder.due_date.setText( daysLeft + " d " + hoursLeft + " h " + minutesLeft + " m " + secondsLeft + " s" );
                        }

                        @Override
                        public void onFinish() {

                            try {
                                userRef = objectFirebaseFirestore.collection( "homework" );

                                String id = getSnapshots().getSnapshot( position ).getId();
                                Toast.makeText( StudentHomework.this, "Due DUE DUE", Toast.LENGTH_SHORT ).show();
                                userRef.document( id ).update( "state", "due" );

                            }catch(Exception ex){
                                StudentHomework.this.finish();
                            }
                        }

                    }.start();
                } else
                    {

                    userRef = objectFirebaseFirestore.collection( "homework" );
                    String id = getSnapshots().getSnapshot( position ).getId();
                    Toast.makeText( StudentHomework.this, "Due DUE DUE", Toast.LENGTH_SHORT ).show();
                    userRef.document( id ).update( "state", "due" );
                }


                // Check whether user has attempt the quiz

                homeworkViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef = objectFirebaseFirestore.collection( "joinHomework" );

                        check_attempt = 0;

                        userRef.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    String dbuserId = documentSnapshot.getString( "userId" );
                                    String dbhomeworkId = documentSnapshot.getString( "homeworkId" );

                                    String userId = uid;
                                    String homeworkId = getSnapshots().getSnapshot( position ).getId();

                                    if(userId.equals( dbuserId ) && homeworkId.equals( dbhomeworkId )) {
                                        check_attempt++;
                                    }
                                }
                            }
                        } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(check_attempt > 0 ){
                                    Toast.makeText( StudentHomework.this, "No more attempt allowed", Toast.LENGTH_SHORT ).show();
                                }else{
                                    String homeworkId = getSnapshots().getSnapshot( position ).getId();
                                    String quizId = getSnapshots().getSnapshot( position ).getString( "quizId" );
                                    getUsername(homeworkId, quizId);
                                }
                            }
                        } );

                    }
                } );
            }
            @NonNull
            @Override
            public HomeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_homework, parent, false );
                return new HomeworkViewHolder( v );
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter( adapter );
    }


    private class HomeworkViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView title;
        TextView due_date;

        public HomeworkViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            title = itemView.findViewById( R.id.homerwork_title );
            due_date = itemView.findViewById( R.id.due_date1 );
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
        StudentHomework.this.finish();
    }

    public void openHomeworkData(String hid, String qid, String joinHomeworkId){
        Intent intent = new Intent (this, HomeworkData.class);
        intent.putExtra( "quizId", qid );
        intent.putExtra( "homeworkId", hid );
        intent.putExtra( "userId", uid );
        intent.putExtra( "classId", cid );
        intent.putExtra( "joinHomeworkId", joinHomeworkId );
        startActivity( intent );
        StudentHomework.this.finish();
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

    public void openHomeworkResult(){

        Intent intent = new Intent (this, HomeworkResult.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
        this.finish();
    }

    public void openNotice(){

        Intent intent = new Intent (this, JoinClassAnnouncement.class);
        intent.putExtra( "classId", cid );
        intent.putExtra( "userId", uid );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
        this.finish();
    }

    public void getUsername(final String homeworkId, final String quizId){

        userRef = objectFirebaseFirestore.collection( "user" );

        userRef.document(uid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString( "username" );
                joinHomework( homeworkId, quizId, username );
            }
        } );

    }


    public void joinHomework(String homeworkId, String quizId, String username){

        objectMap = new HashMap<>();


        DocumentReference ref = objectFirebaseFirestore.collection( "joinHomework" ).document();
        String joinHomeworkId = ref.getId();
        objectMap.put("username", username);
        objectMap.put( "homeworkId", homeworkId );
        objectMap.put("classId", cid);
        objectMap.put("userId", uid);
        ref.set( objectMap );

        openHomeworkData( homeworkId , quizId, joinHomeworkId );
    }





    }
