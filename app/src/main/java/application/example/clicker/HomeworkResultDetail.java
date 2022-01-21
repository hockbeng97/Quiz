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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

public class HomeworkResultDetail extends AppCompatActivity {

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    String qid, homeworkTitle, homeworkId, uid;
    TextView homework_title, quizName;
    static String optionId;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter <QuestionModel, HomeworkResultDetailViewHolder> adapter;
    ImageView imgView, imgView1;
    Dialog dialog;
    static int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_homework_result_detail );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        qid = intent.getStringExtra( "quizId" );
        homeworkTitle = intent.getStringExtra( "homeworkTitle" );
        homeworkId = intent.getStringExtra( "homeworkId" );
        uid = intent.getStringExtra( "userId" );

        homework_title = findViewById( R.id.homework_title );
        quizName = findViewById( R.id.quizName );

        // set title and name
        homework_title.setText( homeworkTitle );
        displayQuizName();


        //recycler View
        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        userRef = objectFirebaseFirestore.collection( "homeworkQuestion" );

        Query query = userRef.whereEqualTo( "quizId", qid );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, HomeworkResultDetailViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final HomeworkResultDetailViewHolder homeworkResultDetailViewHolder, final int position, @NonNull final QuestionModel questionModel) {

                homeworkResultDetailViewHolder.question.setText( questionModel.getTxt_question() );

                String questionId = getSnapshots().getSnapshot( position ).getId();
                String imageUri = getSnapshots().getSnapshot( position ).getString( "imageUri" );


                dialog = new Dialog(HomeworkResultDetail.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.floating_image);

                imgView = homeworkResultDetailViewHolder.image;
                imgView1 = dialog.findViewById( R.id.fullImage );

                if(imageUri == null) {
                    imgView.setImageResource( R.drawable.blank_img );
                    imgView1.setImageResource( R.drawable.blank_img );
                }else{
                    Picasso.with( HomeworkResultDetail.this ).load( imageUri ).into( imgView );
                    Picasso.with( HomeworkResultDetail.this ).load( imageUri ).into( imgView1 );
                }


                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String imageUri = getSnapshots().getSnapshot( position ).getString( "imageUri" );
                            if(imageUri == null) {
                                imgView1.setImageResource( R.drawable.blank_img );
                            }else{
                                Picasso.with( HomeworkResultDetail.this ).load( imageUri ).into( imgView1 );
                            }
                            //isImageFitToScreen=false;
                            imgView1.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                            imgView1.setAdjustViewBounds(true);
                            dialog.show();

                    }
                });



                // get the user answer
                count = 0;

                userRef = objectFirebaseFirestore.collection( "homework_score" );

                userRef.whereEqualTo( "questionId", questionId )
                        .whereEqualTo( "homeworkId", homeworkId )
                        .whereEqualTo( "userId", uid )
                        .get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                optionId = documentSnapshot.getString( "optionId" );
                                count++;
                            }
                        }
                } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // get user option
                        if(count == 0){
                            homeworkResultDetailViewHolder.userAns.setText( "No Attempt" );
                            homeworkResultDetailViewHolder.userAns.setBackgroundColor( Color.parseColor("#FF6666") );
                        }else{
                        userRef = objectFirebaseFirestore.collection( "homeworkOption" );
                        userRef.document(optionId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String dboption = documentSnapshot.getString( "option" );
                                Boolean dbtrue = documentSnapshot.getBoolean( "is_true" );

                                if(dbtrue == true) {
                                    homeworkResultDetailViewHolder.userAns.setText( dboption );
                                    homeworkResultDetailViewHolder.userAns.setBackgroundColor( Color.parseColor("#66FF66") );
                                }else{
                                    homeworkResultDetailViewHolder.userAns.setText( dboption );
                                    homeworkResultDetailViewHolder.userAns.setBackgroundColor( Color.parseColor("#FF6666") );
                                }
                            }
                        } );}
                    }
                } );

                // Get the option
                userRef = objectFirebaseFirestore.collection( "homeworkOption" );
                userRef.whereEqualTo( "questionId", questionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int a = 1;
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String dboption = documentSnapshot.getString( "option" );
                            Boolean dbtrue = documentSnapshot.getBoolean( "is_true" );

                            if(a == 1){
                                homeworkResultDetailViewHolder.option1.setText( dboption );
                                if(dbtrue == true){
                                    homeworkResultDetailViewHolder.tick1.setVisibility( View.VISIBLE );
                                }
                            }


                            if(a == 2){
                                homeworkResultDetailViewHolder.option2.setText( dboption );
                                if(dbtrue == true){
                                    homeworkResultDetailViewHolder.tick2.setVisibility( View.VISIBLE );
                                }
                            }


                            if(a == 3){
                                homeworkResultDetailViewHolder.option3.setText( dboption );
                                if(dbtrue == true){
                                    homeworkResultDetailViewHolder.tick3.setVisibility( View.VISIBLE );
                                }
                            }

                            if(a == 4){
                                homeworkResultDetailViewHolder.option4.setText( dboption );
                                if(dbtrue == true){
                                    homeworkResultDetailViewHolder.tick4.setVisibility( View.VISIBLE );
                                    break;
                                } }

                            a++;
                        }
                    }
                } );



            }

            @NonNull
            @Override
            public HomeworkResultDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_homework_detail, parent, false );
                return new HomeworkResultDetailViewHolder( v );
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter( adapter );
    }


    private class HomeworkResultDetailViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView question;
        TextView userAns;
        TextView option1, option2, option3, option4;
        ImageView tick1, tick2, tick3, tick4, image;

        public HomeworkResultDetailViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            question = itemView.findViewById( R.id.question );
            userAns = itemView.findViewById( R.id.user_answer );
            option1 = itemView.findViewById( R.id.option1 );
            option2 = itemView.findViewById( R.id.option2 );
            option3 = itemView.findViewById( R.id.option3 );
            option4 = itemView.findViewById( R.id.option4 );
            tick1 = itemView.findViewById( R.id.tick1 );
            tick2 = itemView.findViewById( R.id.tick2 );
            tick3 = itemView.findViewById( R.id.tick3 );
            tick4 = itemView.findViewById( R.id.tick4 );
            image = itemView.findViewById( R.id.image );
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
        HomeworkResultDetail.this.finish();
    }

    public void displayQuizName(){
        userRef = objectFirebaseFirestore.collection( "homeworkQuiz" );
        userRef.document(qid).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString( "title" );
                quizName.setText( name );
            }
        } );
    }


}
