package application.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SearchQuiz extends AppCompatActivity{

    SearchView searchText;
    RecyclerView recyclerView;
    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    List<String> quizList;
    List<String> description;
    List<String> quizId;
    SearchFilterRecycler adapter;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search_quiz );

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        userId = intent.getStringExtra( "userId" );


        searchText = findViewById( R.id.search_text );
        recyclerView = findViewById( R.id.recycler_view1 );

        recyclerView = findViewById( R.id.recycler_view1 );
        recyclerView.setHasFixedSize( false );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        quizList = new ArrayList<>();
        description = new ArrayList<>();
        quizId = new ArrayList<>();

        userRef = objectFirebaseFirestore.collection( "quiz" );

        userRef.whereEqualTo( "visibility", "Public" ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String title = documentSnapshot.getString( "title" );
                    String desc = documentSnapshot.getString( "description" );
                    String id = documentSnapshot.getId();
                    quizList.add( title );
                    description.add( desc );
                    quizId.add( id );
                }
            }
        } ).addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                adapter = new SearchFilterRecycler( quizList, description, quizId, userId );
                recyclerView.setAdapter( adapter );
            }
        } );


        searchText.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter( newText );
                return false;
            }
        } );

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}