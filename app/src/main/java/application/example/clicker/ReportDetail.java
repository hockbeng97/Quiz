package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

public class ReportDetail extends AppCompatActivity {

    String gid, qid;
    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder> adapter;
    int count_true;
    int total_count;
    double accuracy;
    ImageView imgView, imgView1;
    Dialog dialog;
    Button nextBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_detail );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        gid = intent.getStringExtra( "gameId" );
        qid = intent.getStringExtra( "quizId" );

        nextBtn = findViewById( R.id.nextBtn );

        nextBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIndividualReport();
            }
        } );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "gameQuestion" );

        Query query1 = userRef.whereEqualTo( "quizId", qid ).orderBy( "txt_question", Query.Direction.ASCENDING );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query1, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final QuestionViewHolder questionViewHolder, final int position, @NonNull QuestionModel questionModel) {

                questionViewHolder.quizTitle.setText( questionModel.getTxt_question() );

                // --- Image matter ---
                String imageUri = getSnapshots().getSnapshot( position ).getString( "imageUri" );


                dialog = new Dialog(ReportDetail.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.floating_image);

                imgView = questionViewHolder.image;
                imgView1 = dialog.findViewById( R.id.fullImage );

                if(imageUri == null) {
                    imgView.setImageResource( R.drawable.blank_img );
                    imgView1.setImageResource( R.drawable.blank_img );
                }else{
                    Picasso.with( ReportDetail.this ).load( imageUri ).into( imgView );
                    Picasso.with( ReportDetail.this ).load( imageUri ).into( imgView1 );
                }


                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String imageUri = getSnapshots().getSnapshot( position ).getString( "imageUri" );
                        if(imageUri == null) {
                            imgView1.setImageResource( R.drawable.blank_img );
                        }else{
                            Picasso.with( ReportDetail.this ).load( imageUri ).into( imgView1 );
                        }
                        //isImageFitToScreen=false;
                        imgView1.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        imgView1.setAdjustViewBounds(true);
                        dialog.show();

                    }
                });

                String qid = getSnapshots().getSnapshot(position).getId();

                //-- Calculate Average
                userRef = objectFirebaseFirestore.collection( "user_score" );

                userRef.whereEqualTo( "gameId", gid ).whereEqualTo( "questionId", qid ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

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
            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_question, parent, false );
                return new QuestionViewHolder( v );
            }
        };

        recyclerView.setAdapter( adapter );
    }

    private class QuestionViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView quizTitle;
        ImageView image;
        TextView accuracy;

        public QuestionViewHolder(@NonNull View itemView) {
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
        ReportDetail.this.finish();
    }

    public void openIndividualReport(){
        Intent intent = new Intent (this, IndividualReport.class);
        intent.putExtra( "gameId", gid );
        startActivity(intent);
    }



}