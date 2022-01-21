package application.example.clicker;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    Intent userId;
    String uid;
    CollectionReference userRef;
    FirebaseFirestore objectFirebaseFirestore;
    static int notifyNumber = 0;
    CheckInternet checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkInternet = new CheckInternet(getApplicationContext());
        boolean connected = checkInternet.isNetworkConnected();

        if(connected == false){
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
            dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_check_internet);
            dialog.show();
        }

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
//Display as default
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new CreateFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }


        // Get doc id from main activity
        userId = getIntent();
        uid = userId.getStringExtra("userId");
        //Toast.makeText(Home.this, "Home : " + uid, Toast.LENGTH_SHORT).show();


        getJoinClass();

    }

    //select item in navigation to display
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CreateFragment()).commit();
                break;

            case R.id.create:
                Intent quiz = new Intent(this, CreateQuiz.class);
                quiz.putExtra("userId", uid);
                startActivity(quiz);
                break;

            case R.id.settings:
                Intent settings = new Intent(this, view_Profile.class);
                settings.putExtra("userId", uid);
                startActivity(settings);
                break;

            case R.id.report:
                Intent report = new Intent (this, Report.class);
                report.putExtra( "userId", uid );
                startActivity( report );
                break;

            case R.id.classroom:
                  Intent classroom = new Intent (this, Classroom.class);
                  classroom.putExtra( "userId", uid );
                  startActivity( classroom );
                  break;

            case R.id.search:
                Intent intent = new Intent (this, SearchQuiz.class);
                intent.putExtra( "userId", uid );
                startActivity( intent );
                break;

            default:
                Intent forward_uid = new Intent(this, CreateFragment.class);
                forward_uid.putExtra("userId", uid);
                startActivity(forward_uid);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void getJoinClass(){

        userRef = objectFirebaseFirestore.collection( "joinClass" );
        userRef.whereEqualTo( "userId", uid ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String classId = documentSnapshot.getString( "classId" );
                    getClassName( classId );
                }
            }
        } );
    }

    public void getClassName(final String classId){
        userRef = objectFirebaseFirestore.collection( "classrooms" );
        userRef.document(classId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString( "name" );
                checkNotice( classId, name );
            }
        } );
    }

    public void checkNotice(String classId, final String name){
        userRef = objectFirebaseFirestore.collection( "announcement" );
        userRef.whereEqualTo( "classId", classId ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String notice = documentSnapshot.getString( "announcement" );
                    notifyNumber++;
                    DisplayNotification( name, notice, notifyNumber );
                }
            }
        } );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void DisplayNotification(String className, String noticeText, int notifyNumber){

        createChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, "caller" )
                .setSmallIcon( R.drawable.notify_icon )
                .setContentTitle( className + " classroom" )
                .setContentText( "Alert: " + noticeText )
                .setPriority( NotificationCompat.PRIORITY_DEFAULT );

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from( this );
        notificationManager.notify( notifyNumber, builder.build() );
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "studentChannel";
            String description = "Hello";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel( "caller", name, importance);
            channel.setDescription( description );

            NotificationManager manager = getSystemService( NotificationManager.class );
            manager.createNotificationChannel( channel );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getJoinClass();
    }
}



