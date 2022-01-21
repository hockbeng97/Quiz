package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class editQuiz extends AppCompatActivity {

    String txttitle;
    String txtDocId, txtDesc, txtImg;
    CollectionReference userRef;
    RelativeLayout relativelayout;
    String dbimg, dbquestion, dbtime, dbQuestionId;
    String KEY_IMG = "imageUri", KEY_QUESTION = "txt_question", KEY_TIME = "time";
    TextView txtview, txtview1;
    int topMargin, a;
    ImageView imgview, imgview1;

    Map<String, Object> objectMap;
    String txttitle1;
    String txtdesc;
    String txtvisible;
    String uid;
    EditText title;
    EditText description;
    Spinner visibility;
    FirebaseFirestore objectFirebaseFirestore;;
    String imagestore;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image;
    private StorageReference storageRef;
    ImageView loading;
    Button addBtn;
    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<QuestionModel, ClassroomViewHolder> adapter;
    TextView getTitle, getDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        loading = findViewById( R.id.loading );

        getTitle = (TextView) findViewById(R.id.title);
        getDesc = (TextView) findViewById(R.id.description);
        imgview = (ImageView) findViewById(R.id.show_img);
        addBtn = findViewById( R.id.addBtn );

        Intent title = getIntent();
        //txttitle = title.getStringExtra("quiz_title");
        txtDocId = title.getStringExtra("docId");
        //txtDesc = title.getStringExtra("description");
        txtImg = title.getStringExtra("imageUri");

        Picasso.with(editQuiz.this).load(txtImg).into(imgview);

        userRef = objectFirebaseFirestore.collection( "quiz" );

        userRef.document(txtDocId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                txttitle = documentSnapshot.getString( "title" );
                txtDesc = documentSnapshot.getString("description");
                txtImg = documentSnapshot.getString( "imageUri" );

                getTitle.setText(txttitle);
                getDesc.setText( txtDesc );
                Picasso.with(editQuiz.this).load(txtImg).into(imgview);
            }
        } ) ;

        Button doneBtn = findViewById( R.id.doneBtn );
        doneBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDatabase();
                refresh();
            }
        } );

        ImageView editImg = findViewById( R.id.show_img );
        editImg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        } );

        Button cancelBtn = findViewById( R.id.cancelBtn );
        cancelBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        } );

        addBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        } );

        // -------------- RECYCLER VIEW -----------------
        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );


        userRef = objectFirebaseFirestore.collection( "question" );

        Query query1 = userRef.whereEqualTo( "quizId", txtDocId );

        FirestoreRecyclerOptions options1 = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery( query1, QuestionModel.class ).build();

        adapter = new FirestoreRecyclerAdapter<QuestionModel, ClassroomViewHolder>( options1 ) {

            @Override
            protected void onBindViewHolder(@NonNull final ClassroomViewHolder classroomViewHolder, final int position, @NonNull QuestionModel classroomModel) {

                String image = classroomModel.getImageUri();
                classroomViewHolder.quizTitle.setText( classroomModel.getTxt_question() );

                if(image == null){
                    classroomViewHolder.image.setImageResource( R.drawable.blank_img );
                }else{
                    Picasso.with( editQuiz.this ).load( image ).into( classroomViewHolder.image );
                }

                classroomViewHolder.view.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String image = getSnapshots().getSnapshot( position ).getString( "imageUri" );
                        String question = getSnapshots().getSnapshot( position ).getString( "txt_question" );
                        String questionId = getSnapshots().getSnapshot( position ).getId();
                        String time = getSnapshots().getSnapshot( position ).getString( "time" );

                        openEditQuestion(image, question, questionId, time);
                    }
                } );

            }

            @NonNull
            @Override
            public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_view_quiz, parent, false );
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


    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = findViewById( R.id.show_img );


        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            loading.setVisibility( View.VISIBLE );
            uploadImage();
        }


    }

    // Get the file extension like .jpg or .png

    public String getFileExtension(){

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }


    public void uploadImage() {

        // CHECK whether file path is empty or not
        if (imageUri != null) {

            final StorageReference storageReference = storageRef.child( "quiz/" + "/" + UUID.randomUUID().toString() + getFileExtension() );
            storageReference.putFile( imageUri )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagestore = uri.toString();
                                }
                            } );

                        }
                    } ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    image.setImageURI( imageUri );
                    loading.setVisibility( View.INVISIBLE );
                }
            } );
    }else{
            Picasso.with( this ).load( txtImg ).into( image );
        }
    }


        public void insertDatabase(){

        title = findViewById(R.id.title);
        txttitle1 = title.getText().toString();

        description = findViewById(R.id.description);
        txtdesc = description.getText().toString();

        visibility = findViewById(R.id.visibility);

        // get the data from the selected spinner
        if( visibility.getSelectedItem().toString().equals("Public") ){
            txtvisible = "Public";
        }
        else
            txtvisible = "Private";


        //try{
            //-------------------------- Insert ----------------------------------------

            objectMap = new HashMap<>();

            objectMap.put("title", txttitle1);
            objectMap.put("description", txtdesc);
            objectMap.put("visibility", txtvisible);
            if(imagestore == null){
                //objectMap.put("imageUri", txtImg);
            }else{
                objectMap.put("imageUri", imagestore);
            }

             userRef = objectFirebaseFirestore.collection("quiz");

                    userRef.document(txtDocId)
                    .update(objectMap)
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText( editQuiz.this, "Quiz Updated", Toast.LENGTH_SHORT ).show();
                        }
                    } );


        //}
        //catch(Exception e){ Toast.makeText(editQuiz.this, "In the catch " + e, Toast.LENGTH_SHORT).show(); }
    }


    public void refresh(){
        Toast.makeText(this, "Refreshing" , Toast.LENGTH_SHORT).show();
        Intent refresh = new Intent(this, ViewQuiz.class);
        refresh.putExtra( "docId", txtDocId );
        startActivity(refresh);
        this.finish();
    }

    public void closeActivity(){
        this.finish();
    }

    public void addQuestion(){
        Intent intent = new Intent (this, CreateQuestion.class);
        intent.putExtra( "quizId", txtDocId );
        intent.putExtra( "imageUri", txtImg);
        startActivity(intent);
    }

    public void openEditQuestion(String image, String question, String id, String time){

        Intent view_quiz = new Intent(editQuiz.this, edit_Question.class);
        view_quiz.putExtra("question_img", image);
        view_quiz.putExtra("question_text", question);
        view_quiz.putExtra("time",  time );
        view_quiz.putExtra("questionId", id);
        view_quiz.putExtra("quizId", txtDocId);
        view_quiz.putExtra("quizImg", txtImg);
        startActivity( view_quiz );
        //this.finish();
    }

}
