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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;

public class Report extends AppCompatActivity {

    RecyclerView recyclerView, recyclerView2;
    FirebaseFirestore objectFirebaseFirestore;
    private FirestoreRecyclerAdapter<ReportModel, ReportViewHolder> adapter;
    CollectionReference userRef;
    String uid;
    static String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra( "userId" );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "game" );

        Query query1 = userRef.whereEqualTo( "userId", uid ).orderBy( "hosted_date", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<ReportModel>().setQuery( query1, ReportModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<ReportModel, ReportViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ReportViewHolder reportViewHolder, final int position, @NonNull final ReportModel reportModel) {

                try {
                    reportViewHolder.input_date.setText( reportModel.getHosted_date() );
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String qid = getSnapshots().getSnapshot(position).getString( "quizId" ) ;

                userRef = objectFirebaseFirestore.collection( "gameQuiz" );

                userRef.document( qid ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        title = documentSnapshot.getString("title");
                        reportViewHolder.quizTitle.setText( title );
                    }
                } );

                reportViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String gid = getSnapshots().getSnapshot(position).getId();
                        String qid = getSnapshots().getSnapshot(position).getString( "quizId" ) ;
                        reportDetails(gid, qid);
                    }
                } );

            }

            @NonNull
            @Override
            public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_report, parent, false );
                return new ReportViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );

    }

    private class ReportViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView quizTitle;
        TextView input_date;

        public ReportViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            quizTitle = itemView.findViewById( R.id.quizTitle );
            input_date = itemView.findViewById( R.id.input_date );
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
        Report.this.finish();
    }


    public void retrieveQuiz(String qid){


    }

    public void reportDetails(String gid, String qid){
        Intent intent = new Intent (this, ReportDetail.class);
        intent.putExtra("gameId", gid);
        intent.putExtra( "quizId", qid );
        startActivity(intent);
    }

}