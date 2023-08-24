package com.turksat46.freakslabor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.turksat46.freakslabor.FreaksAR.OverlayDebuggingTool;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class newMainActivity extends AppCompatActivity implements SensorEventListener{
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public PreviewView previewView;

    StatusBar statusBar;

    Boolean isProfileOpen = false;

    CircleImageView userimage;

    ConstraintLayout homeview;

    ConstraintLayout mainview;

    //ProfileView
    ConstraintLayout profileview;
    ImageView largeProfilePic;
    TextView usernametextview;
    TextView biotextview;
    //END ProfileView

    SharedPreferences sharedPreferences;
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Systems
    FreaksNearby freaksNearby;

    private SensorManager sensorManager;
    private Sensor magnetfeldSensor;

    OverlayDebuggingTool overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        overridePendingTransition(R.anim.activityfadein, R.anim.activityfaedout);
        setContentView(R.layout.activity_new_main);

        overlay = (OverlayDebuggingTool)findViewById(R.id.testOverlay);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetfeldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sensorManager.registerListener((SensorEventListener) this, magnetfeldSensor, SensorManager.SENSOR_DELAY_GAME);

        db.setFirestoreSettings(settings);

        freaksNearby = new FreaksNearby(getApplicationContext(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        mainview = (ConstraintLayout)findViewById(R.id.mainview);

        TextView statusTextView = (TextView)findViewById(R.id.statustextView);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar3);
        CardView statusCard = (CardView)findViewById(R.id.statuscard);
        statusBar = new StatusBar(statusCard, statusTextView, progressBar);

        checkForInternetConnection();

        homeview = (ConstraintLayout)findViewById(R.id.homeview);
        profileview = (ConstraintLayout)findViewById(R.id.profileview);
        usernametextview = (TextView)findViewById(R.id.usernametextview);
        biotextview = (TextView)findViewById(R.id.biotextview);
        populateuserinfo();
        profileview.setVisibility(View.GONE);
        largeProfilePic = (ImageView)findViewById(R.id.largeprofpic);
        FirebaseImageLoader.loadImage(getApplicationContext(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString(), largeProfilePic);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        //Set Userimage
        userimage = (CircleImageView)findViewById(R.id.personalimg);
        FirebaseImageLoader.loadImage(this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString(), userimage);
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateuserinfo();
                if(!isProfileOpen){
                    homeview.setVisibility(View.GONE);
                    profileview.setVisibility(View.VISIBLE);
                    userimage.setImageResource(R.drawable.baseline_close_24);
                    isProfileOpen = true;
                }else{
                    profileview.setVisibility(View.GONE);
                    homeview.setVisibility(View.VISIBLE);
                    FirebaseImageLoader.loadImage(getApplicationContext(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString(), userimage);
                    isProfileOpen = false;
                }
            }
        });

        //Camera
        previewView = (PreviewView) findViewById(R.id.previewView);

        //Camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);



        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        //FOR DEBUGGING PURPOSES
        debugFunction();
    }

    private void checkForInternetConnection() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()){
            //Cache data
            cacheData();
        }else{
            statusBar.showWarning("You are offline, cached data will be used!");
            //FreaksMesh
        }
    }

    private void cacheData(){
        statusBar.showSimpleLoadingProgress("Caching data...");
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.w("Caching data...", task.getResult().getDocuments().toString());

                    }
                });
    }

    private void populateuserinfo() {
        db.collection("users").document(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        usernametextview.setText(documentSnapshot.get("name").toString());
                        biotextview.setText(documentSnapshot.get("bio").toString());
                    }
                });
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
        
    }
    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void debugFunction(){
        newPerson personData[] = new newPerson[2];
        personData[0] = new newPerson(R.drawable.img, "Test", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        personData[1] = new newPerson(R.drawable.img, "Test", "575bb088408988be");
        initPersonRecyclerView(personData);
    }

    private void initPersonRecyclerView(newPerson[] personData) {
        Log.d("PersonRecyclerView", "Start and buildup!");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView personrecyclerView = findViewById(R.id.personrecyclerview);
        personrecyclerView.setLayoutManager(layoutManager);
        PersonRecyclerViewAdapter personadapter = new PersonRecyclerViewAdapter(this, personData);
        personrecyclerView.setAdapter(personadapter);
        Log.d("PersonRecyclerView", "Adapter set and finished!");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimutKamera = event.values[0];
        float polarKamera = 90 - event.values[1];
        overlay.setParameters(azimutKamera, polarKamera);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}