package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateQuestion extends AppCompatActivity {

    ImageView image;
    TextView txthint;
    Uri imageUri;
    EditText question, option1, option2, option3, option4;
    ImageView tick1, tick2, tick3, tick4;
    private static final int PICK_IMAGE = 100;
    private StorageReference storageRef;
    TextView txtsave, cancelBtn;
    String questionId;

    Map <String, String> objectMap;
    Map <String, Question> objMap;
    FirebaseFirestore objectFirebaseFirestore;
    String imagestore, txtquestion;
    int count1, count2, count3, count4 = 0;
    String txtoption1, txtoption2, txtoption3, txtoption4;
    boolean is_true1, is_true2, is_true3, is_true4 = false;
    Intent quizId;
    String qid;
    Question question_class;
    Spinner timer;
    String txtTime, txtImg;

    int count_insert = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        question_class = new Question() ;

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        tick1 = findViewById(R.id.tick1);
        tick2 = findViewById(R.id.tick2);
        tick3 = findViewById(R.id.tick3);
        tick4 = findViewById(R.id.tick4);

        image = findViewById(R.id.imageInput);
        txthint = findViewById(R.id.textView3);
        question = findViewById(R.id.question);
        txtsave = findViewById(R.id.textView8);

        cancelBtn = findViewById( R.id.cancelBtn );

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        tick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption1();
                count1++;
            }
        });

        tick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption2();
                count2++;
            }
        });

        tick3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption3();
                count3++;
            }
        });

        tick4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption4();
                count4++;
            }
        });

        question.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                question.setError( null );
                return false;
            }
        } );

        option1.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                option1.setError( null );
                return false;
            }
        } );

        option2.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                option2.setError( null );
                return false;
            }
        } );


        txtsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtoption1 = option1.getText().toString();
                txtoption2 = option2.getText().toString();
                txtoption3 = option3.getText().toString();
                txtoption4 = option4.getText().toString();

                question_class.setQuestionId(questionId);
                String txt_question = question.getText().toString();

                if(TextUtils.isEmpty( txt_question ) ){
                    question.setFocusable( true );
                    //question.setFocusableInTouchMode( true );
                    question.requestFocus();
                    question.setError( "Field Cannot be Empty" );
                }

                // Check option is fill or not
                    if( !TextUtils.isEmpty( txtoption1 ) && !TextUtils.isEmpty( txtoption2 )) {

                        //check if the one of option selected true
                        if(is_true1 != true && is_true2 != true && is_true3 != true && is_true4 != true ){
                            View view = findViewById(android.R.id.content).getRootView();
                            Snackbar snackbar = Snackbar.make(view, "At Least One Option Must Be True", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }else{
                            String op3 = option3.getText().toString();
                            String op4 = option4.getText().toString();

                            View view = findViewById(android.R.id.content).getRootView();

                            if(TextUtils.isEmpty( op3 ) && is_true3 == true){
                                Snackbar snackbar = Snackbar.make(view, "Please fill the Option 3", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }else if(TextUtils.isEmpty( op4 ) && is_true4 == true){
                                Snackbar snackbar = Snackbar.make(view, "Please fill the Option 4", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }else{
                                insertQuestion();
                                openQuiz();
                            }

                        }

                    }//end if
                     else{
                        option1.setFocusable(true);
                        //option1.setFocusableInTouchMode( true );
                        option1.requestFocus();
                        option1.setError( "Field cannot be empty" );
                        option2.setFocusable(true);
                       // option2.setFocusableInTouchMode( true );
                        option2.requestFocus();
                        option2.setError( "Field cannot be empty" );
                    }

                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        cancelBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateQuestion.this.finish();
            }
        } );

        quizId = getIntent();
        qid = quizId.getStringExtra("quizId");
        txtImg = quizId.getStringExtra( "imageUri" );

    }

    // ---------------- Option -----------------------------------------------
    public void checkOption1(){

        if(count1 % 2 == 0){
            tick1.setImageResource(R.drawable.green_tick);
            is_true1 = true;
        }
        else{
            tick1.setImageResource(R.drawable.grey_tick1);
            is_true1 = false;
        }
    }

    public void checkOption2(){
        if(count2 % 2 == 0){
            tick2.setImageResource(R.drawable.green_tick);
            is_true2 = true;}
        else{
            tick2.setImageResource(R.drawable.grey_tick1);
            is_true2 = false;}
    }

    public void checkOption3(){
        if(count3 % 2 == 0){
            tick3.setImageResource(R.drawable.green_tick);
            is_true3 = true;}
        else{
            tick3.setImageResource(R.drawable.grey_tick1);
            is_true3 = false;}
    }

    public void checkOption4(){
        if(count4 % 2 == 0){
            tick4.setImageResource(R.drawable.green_tick);
            is_true4 = true;}
        else{
            tick4.setImageResource(R.drawable.grey_tick1);
            is_true4 = false;}
    }



    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int count = 0;

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            txthint.setVisibility(View.INVISIBLE);
            imageUri = data.getData();
            image.setImageURI(imageUri);
            count++;
        }

        if(count == 1)
            uploadImage();
    }

    // Get the file extension like .jpg or .png

    public String getFileExtension(){

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }


    public void uploadImage(){

        txtquestion = question.getText().toString();

        // CHECK whether file path is empty or not
        if(imageUri != null) {

            final StorageReference storageReference = storageRef.child("question/" + UUID.randomUUID().toString() + getFileExtension());
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagestore = uri.toString();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }


    public void insertQuestion(){

        txtoption1 = option1.getText().toString();
        txtoption2 = option2.getText().toString();
        txtoption3 = option3.getText().toString();
        txtoption4 = option4.getText().toString();

        txtquestion = question.getText().toString();

        timer = findViewById(R.id.timer);

        if( timer.getSelectedItem().toString().equals("10 sec") )
            txtTime = "10";

        else if ( timer.getSelectedItem().toString().equals("30 sec") )
            txtTime = "30";

        else if ( timer.getSelectedItem().toString().equals("60 sec") )
            txtTime = "60";

        else if ( timer.getSelectedItem().toString().equals("120 sec") )
            txtTime = "120";

        try {
            //-------------------------- Insert ----------------------------------------

            objectMap = new HashMap<>();
            objectFirebaseFirestore = FirebaseFirestore.getInstance();

            objectMap.put( "txt_question", txtquestion );
            objectMap.put( "imageUri", imagestore );
            objectMap.put( "quizId", qid );
            objectMap.put( "time", txtTime );

                // Add data to question
                objectFirebaseFirestore
                        .collection( "question" )
                        .add( objectMap )
                        .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText( CreateQuestion.this, "Question is added", Toast.LENGTH_SHORT ).show();
                                questionId = documentReference.getId();
                                question_class.setQuestionId( questionId );
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( CreateQuestion.this, "Question is failed to add", Toast.LENGTH_SHORT ).show();
                    }
                } ).addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        insertOption( txtoption1, is_true1 );
                        insertOption( txtoption2, is_true2 );
                        insertOption( txtoption3, is_true3 );
                        insertOption( txtoption4, is_true4 );
                    }
                } );




        }// try bracket
        catch(Exception e){ Toast.makeText(CreateQuestion.this, "In the catch " + e , Toast.LENGTH_SHORT).show(); }
    }

    // ------------------------- Insert Option -------------------------------------

    public void insertOption(String option, boolean is_true){

        // Add data to option

       question_class.setOption1(option);
       question_class.setIs_true1(is_true);
       question_class.setQuestionId(question_class.getQuestionId());

        objectFirebaseFirestore
                .collection("option")
                .add(question_class)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateQuestion.this, "Option added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openQuiz(){
        Intent intent = new Intent (CreateQuestion.this, editQuiz.class);
        intent.putExtra( "docId", qid );
        intent.putExtra( "imageUri", txtImg );
        startActivity( intent );
        this.finish();
    }


}
