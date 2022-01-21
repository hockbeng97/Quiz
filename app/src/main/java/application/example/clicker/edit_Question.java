package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class edit_Question extends AppCompatActivity {

    ImageView image;
    TextView txthint, cancelBtn;
    Uri imageUri;
    EditText question, option1, option2, option3, option4;
    ImageView tick1, tick2, tick3, tick4;
    private static final int PICK_IMAGE = 100;
    private StorageReference storageRef;
    TextView txtsave;
    String questionId;

    Map<String, Object> objectMap;
    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    String imagestore, txtquestion;
    int count1, count2, count3, count4;
    String txtoption1, txtoption2, txtoption3, txtoption4;
    boolean is_true1, is_true2, is_true3, is_true4 = false;
    Question question_class;
    Spinner timer;
    String txtTime;
    String dbimg, dbquestion, dbtime, dbQuestionId;
    String[] id; int a = 0;
    int value1 = 0;
    int value2 = 1;
    int value3 = 2;
    int value4 = 3;
    ImageView loading;
    TextView deleteBtn;
    String quizId, quizImg;
    static int countNull, countFalse, countAllFalse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent getQ = getIntent();
        dbimg = getQ.getStringExtra("question_img");
        dbquestion = getQ.getStringExtra("question_text");
        dbQuestionId = getQ.getStringExtra("questionId");
        dbtime = getQ.getStringExtra("time");
        quizId = getQ.getStringExtra( "quizId" );
        quizImg = getQ.getStringExtra( "quizImg" );

        loading = findViewById( R.id.loading );
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

        deleteBtn = findViewById( R.id.deleteBtn );
        cancelBtn = findViewById( R.id.cancelBtn );

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( edit_Question.this );
                builder.setTitle( "Alert" );
                builder.setMessage( "Are you sure to delete ?" );
                builder.setCancelable( true );

                builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );

                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteQuestion();
                        edit_Question.this.finish();
                    }
                } );
                builder.show();
            }
        } );

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        tick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1++;
                checkOption1();

            }
        });

        tick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2++;
                checkOption2();

            }
        });

        tick3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count3++;
                checkOption3();

            }
        });

        tick4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count4++;
                checkOption4();

            }
        });

        retrieve_Question();

        //---------------------------------------  OPTION ID Start -------------------------------------

        userRef = objectFirebaseFirestore.collection( "option" );


        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                id = new String[4];

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    id[a] = documentSnapshot.getId();
                    a++;
                }
            }
        });

        //---------------------------------------  OPTION ID End ------------------------------------

        txtsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countNull = 0;
                countFalse = 0;
                countAllFalse = 0;

                String op1 = option1.getText().toString();
                String op2 = option2.getText().toString();
                String op3 = option3.getText().toString();
                String op4 = option4.getText().toString();

                if (TextUtils.isEmpty( op1 )) {
                    countNull++;
                }
                if (TextUtils.isEmpty( op2 )) {
                    countNull++;
                }
                if (TextUtils.isEmpty( op3 )) {
                    countNull++;
                }
                if (TextUtils.isEmpty( op4 )) {
                    countNull++;
                }

                if (countNull > 2) {
                    Toast.makeText( edit_Question.this, "Must have at least 2 Options filled", Toast.LENGTH_SHORT ).show();
                }

                if (is_true1 == false && is_true2 == false && is_true3 == false && is_true4 == false) {
                    Toast.makeText( edit_Question.this, "Must have at least 1 Option is True", Toast.LENGTH_SHORT ).show();
                    countAllFalse++;
                }

                if (is_true1 == true && TextUtils.isEmpty( op1 )) {
                    countFalse++;
                }
                if (is_true2 == true && TextUtils.isEmpty( op2 )) {
                    countFalse++;
                }
                if (is_true3 == true && TextUtils.isEmpty( op3 )) {
                    countFalse++;
                }
                if (is_true4 == true && TextUtils.isEmpty( op4 )) {
                    countFalse++;
                }

                if (countFalse > 0) {
                    Toast.makeText( edit_Question.this, "The Option Selected True Cannot Be Empty", Toast.LENGTH_SHORT ).show();
                }

                if (countNull <= 2 && countAllFalse == 0 && countFalse == 0){
                    insertQuestion();
                question_class.setQuestionId( questionId );
                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        insertOption( txtoption1, is_true1, value1 );
                        insertOption( txtoption2, is_true2, value2 );
                        insertOption( txtoption3, is_true3, value3 );
                        insertOption( txtoption4, is_true4, value4 );
                        Toast.makeText( edit_Question.this, " Updated ", Toast.LENGTH_SHORT ).show();
                        openEditQuiz();
                    }
                }, 3000 );
            }// end if
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        cancelBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_Question.this.finish();
            }
        } );

    }

    // ---------------- Option -----------------------------------------------
    public void checkOption1(){

        //Toast.makeText( this, "Count 1 : " + count1, Toast.LENGTH_SHORT ).show();

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

        //Toast.makeText( this, "Count 2 : " + count2, Toast.LENGTH_SHORT ).show();

        if(count2 % 2 == 0){
            tick2.setImageResource(R.drawable.green_tick);
            is_true2 = true;}
        else{
            tick2.setImageResource(R.drawable.grey_tick1);
            is_true2 = false;}
    }

    public void checkOption3(){

        //Toast.makeText( this, "Count 3 : " + count3, Toast.LENGTH_SHORT ).show();

        if(count3 % 2 == 0){
            tick3.setImageResource(R.drawable.green_tick);
            is_true3 = true;}
        else{
            tick3.setImageResource(R.drawable.grey_tick1);
            is_true3 = false;}
    }

    public void checkOption4(){

        //Toast.makeText( this, "Count 4 : " + count4, Toast.LENGTH_SHORT ).show();

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

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            uploadImage();
            loading.setVisibility( View.VISIBLE );
        }
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
                    }).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    loading.setVisibility( View.INVISIBLE );
                }
            } );
        }

    }


    public void insertQuestion(){

        txtquestion = question.getText().toString();

        txtoption1 = option1.getText().toString();
        txtoption2 = option2.getText().toString();
        txtoption3 = option3.getText().toString();
        txtoption4 = option4.getText().toString();

        timer = findViewById(R.id.timer);

        if( timer.getSelectedItem().toString().equals("10 sec") )
            txtTime = "10";

        else if ( timer.getSelectedItem().toString().equals("30 sec") )
            txtTime = "30";

        else if ( timer.getSelectedItem().toString().equals("60 sec") )
            txtTime = "60";

        else if ( timer.getSelectedItem().toString().equals("120 sec") )
            txtTime = "120";

        try{
            //-------------------------- Insert ----------------------------------------
            Intent getQ = getIntent();
            dbQuestionId = getQ.getStringExtra("questionId");

            objectMap = new HashMap<>();
            userRef = objectFirebaseFirestore.collection( "question" );

            objectMap.put("txt_question", txtquestion);
            if(imagestore!=null) {
                objectMap.put( "imageUri", imagestore );
            }
            objectMap.put( "time", txtTime );

                // Add data to question
            userRef.document( dbQuestionId ).update(objectMap);
            Toast.makeText( edit_Question.this, " Updating ... ", Toast.LENGTH_SHORT ).show();


        }// try bracket


        catch(Exception e){ Toast.makeText(edit_Question.this, "In the catch " + e , Toast.LENGTH_SHORT).show(); }
    }


    // ------------------------- Insert Option -------------------------------------

    public void insertOption(final String option, final boolean is_true, final int value) {

        Intent getQ = getIntent();
        dbQuestionId = getQ.getStringExtra( "questionId" );

        // Add data to option

        final Map<String, Object> map = new HashMap<>();

        userRef = objectFirebaseFirestore.collection( "option" );

        question_class.setOption1( option );
        question_class.setIs_true1( is_true );

        map.put( "option1", question_class.getOption1() );
        map.put( "is_true1", question_class.getIs_true1() );


        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(int a=0; a<4; a++) {
                    userRef.document( id[value] ).update( map );

                }
            }
        } );
    }



    // Retrieve Question from firebase

    public void retrieve_Question(){

        EditText enterQ = (EditText) findViewById( R.id.question );
        Spinner mySpinner = (Spinner) findViewById( R.id.timer );

        enterQ.setText( dbquestion);
        Picasso.with( this ).load( dbimg ).into( image );

        if(dbtime.equals("10"))
            mySpinner.setSelection(0);

        else if(dbtime.equals("30"))
            mySpinner.setSelection(1);

        else if(dbtime.equals( "60" ))
            mySpinner.setSelection(2);

        else if(dbtime.equals( "120" ))
            mySpinner.setSelection(3);

        FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference userRef = objectFirebaseFirestore.collection( "option" );

        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {

            EditText option1 = findViewById( R.id.option1 );
            EditText option2 = findViewById( R.id.option2 );
            EditText option3 = findViewById( R.id.option3 );
            EditText option4 = findViewById( R.id.option4 );

            int a = 1;
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String dboption = documentSnapshot.getString( "option1" );
                    boolean dbtrue = documentSnapshot.getBoolean( "is_true1" );

                    if(a == 1) {
                        option1.setText( dboption );

                        if ( dbtrue == true ) {
                            count1 = 0;
                            checkOption1();
                        }else if ( dbtrue == false ){
                            count1 = 1;
                            checkOption1();
                        }else if ( dboption == null ){
                            count1 = 1;
                            checkOption1();
                        }
                    }

                    if(a == 2){
                        option2.setText( dboption );

                        if (dbtrue == true ) {
                            count2 = 0;
                            checkOption2();
                        }else {
                            count2 = 1;
                            checkOption2();
                        }
                    }

                    if(a == 3){
                        option3.setText( dboption );

                        if (dbtrue == true ){
                            count3 = 0;
                            checkOption3();
                        } else {
                            count3 = 1;
                            checkOption3();
                        }
                    }

                    if(a == 4){
                        option4.setText( dboption );

                        if (dbtrue == true ) {
                            count4 = 0;
                            checkOption4();
                        } else {
                            count4 = 1;
                            checkOption4();
                        }
                    }
                    a++;
                }



            }
        } );
    }

    public void deleteQuestion(){

        userRef = objectFirebaseFirestore.collection( "question" );

        userRef.document(dbQuestionId).delete();

        deleteOption();
    }

    public void deleteOption(){
        userRef = objectFirebaseFirestore.collection( "option" );
        userRef.whereEqualTo( "questionId", dbQuestionId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot: snapshotList) {
                    batch.delete( documentSnapshot.getReference() );
                }
                batch.commit();
            }
        } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Toast.makeText( edit_Question.this, "Question Deleted", Toast.LENGTH_SHORT).show();
            }
        } );
    }

    public void openEditQuiz(){
        /*Intent intent = new Intent (edit_Question.this, editQuiz.class);
        intent.putExtra( "docId", quizId );
        intent.putExtra( "imageUri", quizImg );
        startActivity(intent);*/
        edit_Question.this.finish();
    }

}