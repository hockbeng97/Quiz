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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

public class HomeworkReportDetail extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuestionModel, HomeworkQuestionViewHolder> adapter;
    int count_true, total_count;
    double accuracy;
    String quizId, homeworkId;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_homework_report_detail );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        quizId = intent.getStringExtra( "quizId" );
        homeworkId = intent.getStringExtra( "homeworkId" );

        nextBtn = findViewById( R.id.nextBtn );
        nextBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIndividualHomeworkReport();
            }
        } );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "homeworkQuestion" );

        Query query1 = userRef.whereEqualTo( "quizId", quizId ).orderBy( "txt_question", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query1, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, HomeworkQuestionViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final HomeworkQuestionViewHolder questionViewHolder, final int position, @NonNull QuestionModel questionModel) {

                questionViewHolder.quizTitle.setText( questionModel.getTxt_question() );

                if(questionModel.getImageUri() == null) {
                    questionViewHolder.image.setImageResource( R.drawable.blank_img );
                }else{
                    Picasso.with(HomeworkReportDetail.this).load(questionModel.getImageUri()).into(questionViewHolder.image);
                }

                String qid = getSnapshots().getSnapshot(position).getId();

                //-- Calculate Average
                userRef = objectFirebaseFirestore.collection( "homework_score" );

                userRef.whereEqualTo( "homeworkId", homeworkId ).whereEqualTo( "questionId", qid ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

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

                        if(total_count != 0 ){
                            accuracy = ((double)count_true/total_count)*100;
                            questionViewHolder.accuracy.setText("Accuracy: " + String.format("%.1f", accuracy) + " %" );
                        }else{
                            questionViewHolder.accuracy.setText("Accuracy: " + "0 %" );
                        }

                    }
                } );

            }

            @NonNull
            @Override
            public HomeworkQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_question, parent, false );
                return new HomeworkQuestionViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class HomeworkQuestionViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView quizTitle;
        ImageView image;
        TextView accuracy;

        public HomeworkQuestionViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            quizTitle = itemView.findViewById( R.id.quizTitle );
            image = itemView.findViewById( R.id.image );
            accuracy = itemView.findViewById( R.id.accuracy );
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
        HomeworkReportDetail.this.finish();
    }

    public void openIndividualHomeworkReport(){
        Intent intent = new Intent ( this, IndividualHomeworkReport.class );
        intent.putExtra( "quizId", quizId );
        intent.putExtra( "homeworkId", homeworkId );
        startActivity( intent );
    }

}
