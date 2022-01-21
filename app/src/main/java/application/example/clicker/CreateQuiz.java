package application.example.clicker;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CreateQuiz extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image;
    TextView txtview;
    TextView save;
    TextView btnadd, cancelBtn;

    String uid;
    EditText title;
    EditText description;
    Spinner visibility;
    boolean checkSpin;

    String txttitle;
    String txtdesc;
    String txtvisible;

    FirebaseFirestore objectFirebaseFirestore;
    CollectionReference userRef;
    Map<String, String> objectMap;
    String imagestore;
    String documentId;
    ImageView loading;

    private StorageReference storageRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_quiz_fragment);

        loading = findViewById( R.id.loading );

        txtview = findViewById(R.id.textView3);
        image = findViewById(R.id.imageInput);
        save = findViewById(R.id.textView10);
        title = findViewById(R.id.editText2);

        // Select image from gallery
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // get data from user input field
        storageRef = FirebaseStorage.getInstance().getReference("uploads");


        // get user id from home.java
        Intent userId = getIntent();
        uid = userId.getStringExtra("userId");

        // add question button
        btnadd = findViewById(R.id.add);

        btnadd.setActivated(false);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnadd.isActivated()){
                    openCreateQuestion();
                    save.setError( null );
                    save.setFocusable( false );
                    save.setFocusableInTouchMode( false );
                }else{
                    save.setFocusable( true );
                    save.setFocusableInTouchMode( true );
                    save.requestFocus();
                    save.setError( "Please save quiz first !" );
                }
            }
        });


        // Save the quiz
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDatabase();
                save.setError( null );
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        cancelBtn = findViewById( R.id.cancelBtn );
        cancelBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateQuiz.this.finish();
            }
        } );

    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );


        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            txtview.setVisibility( View.INVISIBLE );
            imageUri = data.getData();
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


    public void uploadImage(){

        txttitle = title.getText().toString();

        // CHECK whether file path is empty or not
        if(imageUri != null) {

            final StorageReference storageReference = storageRef.child("quiz/" + "/" + UUID.randomUUID().toString() + getFileExtension());
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
                    image.setImageURI( imageUri );
                    loading.setVisibility( View.INVISIBLE );
                }
            } );
        }

    }


    public void insertDatabase() {

        txttitle = title.getText().toString();

        description = findViewById(R.id.editText3);
        txtdesc = description.getText().toString();

        visibility = findViewById(R.id.visibility);

        // get the data from the selected spinner
        checkSpin = true;

        if( visibility.getSelectedItem().toString().equals("Public") ){
            txtvisible = "Public";
        }
        else
            txtvisible = "Private";


        try{
            //-------------------------- Insert ----------------------------------------

            if (txttitle.isEmpty() ){
                title.requestFocus();
                title.setError( "Please fill in this field" );
            }
            if(txtdesc.isEmpty()){
                description.requestFocus();
                description.setError( "Please fill in this field" );
            }
            else if (!txttitle.isEmpty() && !txtdesc.isEmpty())
            {
                objectMap = new HashMap<>();
                objectFirebaseFirestore = FirebaseFirestore.getInstance();

                objectMap.put( "title", txttitle );
                objectMap.put( "description", txtdesc );
                objectMap.put( "visibility", txtvisible );
                objectMap.put( "imageUri", imagestore );
                objectMap.put( "userId", uid );

                objectFirebaseFirestore
                        .collection( "quiz" )
                        .add( objectMap )
                        .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentId = documentReference.getId();
                                Toast.makeText( CreateQuiz.this, "Quiz is created " + documentId, Toast.LENGTH_SHORT ).show();
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( CreateQuiz.this, "Quiz is failed to create", Toast.LENGTH_SHORT ).show();
                    }
                } );
                btnadd.setActivated( true );
            } // end of condition

        }
        catch(Exception e){ Toast.makeText(CreateQuiz.this, "In the catch " + e, Toast.LENGTH_SHORT).show(); }
    }



    public void openCreateQuestion(){
        Intent question = new Intent (this, CreateQuestion.class);
        question.putExtra("quizId", documentId);
        Toast.makeText(CreateQuiz.this, "This is quiz id bro =  " + documentId, Toast.LENGTH_SHORT).show();
        startActivity(question);
        this.finish();
    }

}
