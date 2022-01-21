package application.example.clicker;


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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class MyQuiz extends AppCompatActivity {


    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    String uid;
    int check;
    int a = 0;
    int topMargin;
    TextView txtview;
    String KEY_TITLE = "title";
    String KEY_DESCRIPTION = "description";
    String KEY_IMG = "imageUri";
    String dbtitle;
    String db_docId;
    String dbdesc;
    String imgUrl;
    ImageView[] imgview;
    TextView layout_title;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuizModel, ClassroomViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_quiz );

        layout_title = findViewById( R.id.quizTitle );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        // get user id from home.java
        Intent userId = getIntent();
        uid = userId.getStringExtra( "userId" );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );


        userRef = objectFirebaseFirestore.collection( "quiz" );

        Query query1 = userRef.whereEqualTo( "userId", uid );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuizModel>().setQuery( query1, QuizModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuizModel, ClassroomViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ClassroomViewHolder classroomViewHolder, final int position, @NonNull QuizModel classroomModel) {

                String image = classroomModel.getImageUri();
                classroomViewHolder.quizTitle.setText( classroomModel.getTitle() );

                if (image == null) {
                    classroomViewHolder.image.setImageResource( R.drawable.blank_img );
                } else {
                    Picasso.with( MyQuiz.this ).load( image ).into( classroomViewHolder.image );
                }

                layout_title.setText( "My Quiz (" + getItemCount() + ")" );

                classroomViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = getSnapshots().getSnapshot( position ).getString( "title" );
                        String description = getSnapshots().getSnapshot( position ).getString( "description" );
                        String image = getSnapshots().getSnapshot( position ).getString( "imageUri" );
                        String id = getSnapshots().getSnapshot( position ).getId();
                        openViewQuiz( title, description, id, image );
                    }
                } );

            }

            @NonNull
            @Override
            public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_quiz, parent, false );
                return new ClassroomViewHolder( v );
            }

        };

        recyclerView.setAdapter( adapter );
    }


    private class ClassroomViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView image;
        TextView quizTitle;

        public ClassroomViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            image = itemView.findViewById( R.id.image );
            quizTitle = itemView.findViewById( R.id.quizTitle );
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

    public void openViewQuiz(String title, String description, String quizId, String image){
        Intent intent = new Intent (this, ViewQuiz.class);
        intent.putExtra( "quiz_title", title );
        intent.putExtra( "description", description );
        intent.putExtra( "docId", quizId );
        intent.putExtra( "imageUri", image );
        intent.putExtra( "userId", uid );
        startActivity( intent );
    }



}