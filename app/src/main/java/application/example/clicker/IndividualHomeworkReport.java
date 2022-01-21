package application.example.clicker;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class IndividualHomeworkReport extends AppCompatActivity {

    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuestionModel, individualHomeworkReportViewHolder> adapter;
    String hid;
    int count_true, total_count;
    double accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_individual_homework_report );

        Intent intent = getIntent();
        hid = intent.getStringExtra( "homeworkId" );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "joinHomework" );

        Query query1 = userRef.whereEqualTo( "homeworkId", hid ).orderBy( "score", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query1, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, individualHomeworkReportViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final individualHomeworkReportViewHolder questionViewHolder, final int position, @NonNull QuestionModel questionModel) {

                questionViewHolder.position.setText( "" + (position + 1) );
                questionViewHolder.name.setText( getSnapshots().getSnapshot( position ).getString( "username" ) );

                //-- Calculate Average
                String jhid = getSnapshots().getSnapshot(position).getId();
                String uid = getSnapshots().getSnapshot( position ).getString( "userId" );

                userRef = objectFirebaseFirestore.collection( "homework_score" );

                userRef.whereEqualTo( "homeworkId", hid )
                        .whereEqualTo( "joinHomeworkId", jhid)
                        .whereEqualTo( "userId", uid ).get()
                        .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                count_true = 0;
                                total_count = 0;
                                accuracy = 0;

                                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                                    boolean dbtrue = document.getBoolean( "check" );
                                    if(dbtrue == true){
                                        count_true++;
                                    }
                                    total_count++;
                                }
                            }
                        } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //questionViewHolder.score.setText( "CT "+count_true + " TC " + total_count);

                        if(total_count != 0 ){
                            accuracy = ((double)count_true/total_count)*100;
                            questionViewHolder.score.setText( String.format("%.1f", accuracy) + " %" );
                        }else{
                            Toast.makeText( IndividualHomeworkReport.this, "False TC " + total_count  , Toast.LENGTH_SHORT ).show();
                            questionViewHolder.score.setText("0 %" );
                        }

                    }
                } );

            }

            @NonNull
            @Override
            public individualHomeworkReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_individual_report, parent, false );
                return new individualHomeworkReportViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class individualHomeworkReportViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView position;
        TextView name;
        TextView score;

        public individualHomeworkReportViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            position = itemView.findViewById( R.id.position );
            name = itemView.findViewById( R.id.name );
            score = itemView.findViewById( R.id.score );
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
        IndividualHomeworkReport.this.finish();
    }
}
