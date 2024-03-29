//This is the main program of FreaksLabor, an app, in which you can discover new places, meet people
//and more...
//Written by: Turksat46
//This app shouldn't be used to harm other people or else!!!
//This app should work with many APIs like Spotify or more
//I don't want, collect or sell any data, that's why I want to make it open-source
//

package com.turksat46.freakslabor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.ederdoski.simpleble.utils.BluetoothLEHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.face.FaceDetectorOptions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import Connectors.SongService;
//import de.markusfisch.android.cameraview.widget.CameraView;
public class MainActivity extends AppCompatActivity implements ItemClickListener, Camera.PreviewCallback, SensorEventListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;
    String name, email;
    String idToken;
    private FirebaseAuth.AuthStateListener authStateListener;
    GoogleSignInAccount account;

    // Main shittt
    CameraView cameraView;
    CameraManager cameraManager;
    ImageView coverView;
    ImageView personalimg;
    TextView personalnameText;

    CardView statusBar;
    TextView statusTextView;
    CardView titlecard;
    CardView changeLayout;
    CardView peopleNearCard;
    CardView friendsCard;
    CardView buyCoffeeCard;
    CardView placesNearbyCard;

    TextView cameraViewText;
    ImageView logoImageView;

    ImageView camerashutter;

    ConstraintLayout mainView;
    ConstraintLayout controlView;

    //Debug
    LinearLayout debugView;
    TextView appVersion;
    TextView cameraHeightText;
    TextView cameraWidthText;
    //END DEBUG

    Button activateButton;
    Button shareButton;
    Button supportButton;
    Button reloadButton;

    Handler handler = new Handler();

    BluetoothLEHelper ble;
    AdvertisingSet currentAdvertisingSet;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;


    ListView newPersonList;

    CardView profilecard;

    Song song;

    View view;

    private SongService songService;
    ArrayList<Song> recentlyPlayedTracks;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    boolean mScanning;
    Handler mHandler;

    FrameLayout arlayout;
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final int camerawidth_azimut = 10;
    private static final int camerawidth_polar = 15;

    //CompassView
    private CompassView compassview;

    //SharedPreferences
    //SharedPreferences usersp = getSharedPreferences("user", MODE_PRIVATE);
    //SharedPreferences settingssp = getSharedPreferences("settings", MODE_PRIVATE);


    //DEBUGGING
    //Check if screen has ben touched or sth

    Strategy strategy = Strategy.P2P_CLUSTER;
    String service_id = "com.turksat46.freakslabor.SERVICE_ID";
    String mName;

    //Firebase
    FirebaseFirestoreSettings fbsettings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //ML Kit face recognition
    // High-accuracy landmark detection and face classification
    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .enableTracking()
                    .build();



    //Nearby variables
    /** Our handler to Nearby Connections. */
    private ConnectionsClient mConnectionsClient;

    /** The devices we've discovered near us. */
    private final Map<String, Endpoint> mDiscoveredEndpoints = new HashMap<>();

    /**
     * The devices we have pending connections to. They will stay pending until we call {@link
     * #acceptConnection(Endpoint)} or {@link #rejectConnection(Endpoint)}.
     */
    private final Map<String, Endpoint> mPendingConnections = new HashMap<>();

    /**
     * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
     * there will only be one entry in this map.
     */
    private final Map<String, Endpoint> mEstablishedConnections = new HashMap<>();

    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    private boolean mIsConnecting = false;

    /** True if we are discovering. */
    private boolean mIsDiscovering = false;

    /** True if we are advertising. */
    private boolean mIsAdvertising = false;

    private final Map<Integer, Endpoint> personList = new HashMap<>();


    //Main Program
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        compassview = new CompassView(this);

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
        
        //checkPermissions();
        setContentView(R.layout.activity_main);
        songService = new SongService(getApplicationContext());
        cameraView = (CameraView) findViewById(R.id.camera_view);
        //cameraView.setUseOrientationListener(true);
        coverView = (ImageView) findViewById(R.id.songCoverView);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        statusBar = (CardView)findViewById(R.id.statuscard);
        statusBar.setVisibility(View.GONE);
        statusTextView = (TextView)findViewById(R.id.statusTextView);
        mainView = (ConstraintLayout)findViewById(R.id.mainLayout);

        camerashutter = (ImageView) findViewById(R.id.mainbackground);

        peopleNearCard = (CardView)findViewById(R.id.peoplenearCard);
        friendsCard = (CardView)findViewById(R.id.friendsCard);
        buyCoffeeCard = (CardView)findViewById(R.id.buycoffeecard);
        cameraViewText = (TextView)findViewById(R.id.cameraViewText);

        controlView = (ConstraintLayout)findViewById(R.id.controlLayout);

        placesNearbyCard = (CardView)findViewById(R.id.placesnearbycard);

        changeLayout = (CardView)findViewById(R.id.changeViewButton);
        changeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraView();
            }
        });


        //DEBUG
        debugView = (LinearLayout)findViewById(R.id.debugView);
        appVersion = (TextView)findViewById(R.id.appVersionText);
        cameraHeightText = (TextView)findViewById(R.id.cameraHeightText);
        cameraWidthText = (TextView)findViewById(R.id.cameraWidthText);
        //END DEBUG

        logoImageView=(ImageView)findViewById(R.id.logoImageView);

        view = getWindow().getDecorView();

        db.setFirestoreSettings(fbsettings);

        //Bluetooth
        ble = new BluetoothLEHelper(this);
        mConnectionsClient = Nearby.getConnectionsClient(this);

        //ble.setFilterService(getString(R.string.ble_uuid));

        activateButton = (Button) findViewById(R.id.activatebutton);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), presentation.class));
            }
        });

        titlecard = (CardView)findViewById(R.id.titlecard);
        titlecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cameraView.openAsync(CameraView.findCameraId(
                //        Camera.CameraInfo.CAMERA_FACING_BACK));
                //mainlayout.setVisibility(View.INVISIBLE);
                //start Camera Activity

                showControlCenter();
            }
        });

        shareButton = (Button) findViewById(R.id.friendsbutton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Send this Invitation to anyone!");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Let's be friends on FreaksLabor! https://google.de");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        supportButton = (Button)findViewById(R.id.supportbutton);
        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.buymeacoffee.com/turksat46";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        personalimg = (ImageView)findViewById(R.id.personalimg);
        personalnameText=(TextView)findViewById(R.id.personalnameText);
        personalnameText.setSelected(true);

        profilecard = (CardView)findViewById(R.id.profilecard);
        profilecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ownprofile.class);
                // create the transition animation - the images in te layouts
                // of both activities are defined with android:transitionName="robot"
                //ActivityOptions options = ActivityOptions
                //        .makeSceneTransitionAnimation(MainActivity.this, personalimg, "profileimgtrans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                        Pair.create(personalimg, "profileimgtrans"),
                        Pair.create(personalnameText, "textusernametrans"));
                // start the new activity , options.toBundle()
                //startActivity(intent);


                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());


            }
        });

        reloadButton = (Button)findViewById(R.id.reloadbutton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scanCollars();
            }
        });

        newPersonList = (ListView)findViewById(R.id.listView1);



        /*For testing purposes!!
        newPerson data[] = new newPerson[]
                {
                    new newPerson(R.drawable.ic_launcher_background, "Test"),
                    new newPerson(R.drawable.ic_launcher_foreground, "Kerem"),
                    new newPerson(R.drawable.common_google_signin_btn_icon_dark, "Weyo"),
                    new newPerson(R.drawable.common_google_signin_btn_icon_dark_normal, "Ahaa")
                };

         */
        //reloadNewPersonList(data);
        //initRecyclerView(data);
        //startActivity(new Intent(this, SpotifyLogin.class));
        //getTracks();
        //
        //
        /*cameraView.setOnCameraListener(new CameraView.OnCameraListener() {
            @Override
            public void onConfigureParameters(Camera.Parameters parameters) {
                // set additional camera parameters here
                CameraView.setAutoFocus(parameters);
                cameraHeightText.setText(String.valueOf(parameters.getPreviewSize().height));
                cameraWidthText.setText(String.valueOf(parameters.getPreviewSize().width));
            }

            @Override
            public void onCameraError() {
                // handle camera errors
            }

            @Override
            public void onCameraReady(Camera camera) {
                // set a preview listener

            }

            @Override
            public void onPreviewStarted(Camera camera) {
                // start processing camera data
                camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                    @Override
                    public void onFaceDetection(Camera.Face[] faces, Camera camera) {

                    }
                });


            }

            @Override
            public void onCameraStopping(Camera camera) {
                // clean up
            }
        });

         */

        mName = generateRandomName();

        //Check permissions
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                personalnameText.setText(user.getDisplayName());
                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    // you could place other firebase code
                    //logic to save the user details to Firebase
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)

                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signin();
        lookfordata();
        //Start bluetooth

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

    public void startMain(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(()->{
            logoImageView.setVisibility(View.GONE);
            personalnameText.setVisibility(View.VISIBLE);
        }, 2000);
        mHandler.postDelayed(()->{
            cameraView.setVisibility(View.VISIBLE);
        }, 5000);
        mHandler.postDelayed(()->{
            //Start scanning
            //scanCollars();
            startAdvertising();
            startDiscovery();
            setTestList();
            mainView.setAlpha(0.8f);
        }, 4000);
        mHandler.postDelayed(()->{
            showCameraView(true);
        }, 20000);
        //scanCollars();
    }

    //Google Nearby API

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    public void startAdvertising(){
        mIsAdvertising = true;
        AdvertisingOptions.Builder adOptions = new AdvertisingOptions.Builder();
        adOptions.setStrategy(strategy);

        mConnectionsClient
                .startAdvertising(mName,
                        service_id,
                        mConnectionLifecycleCallback,
                        adOptions.build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.v("Nearby Service", "Now advertising endpoint " + mName);
                        onAdvertisingStarted();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mIsAdvertising = false;
                        Log.w("startAdvertising() failed.",e);
                        onAdvertisingFailed();
                    }
                });
    }

    /** Called when advertising successfully starts. Override this method to act on the event. */
    protected void onAdvertisingStarted() {}

    /** Called when advertising fails to start. Override this method to act on the event. */
    protected void onAdvertisingFailed() {}

    public void startDiscovery(){
        mIsDiscovering = true;
        mDiscoveredEndpoints.clear();
        DiscoveryOptions.Builder dOptions = new DiscoveryOptions.Builder();
        dOptions.setStrategy(strategy);
        mConnectionsClient
                .startDiscovery(service_id,
                        new EndpointDiscoveryCallback() {
                            @Override
                            public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                                Log.d("Nearby Service", String.format("onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",s, discoveredEndpointInfo.getServiceId(), discoveredEndpointInfo.getEndpointName()));
                                if(service_id.equals(discoveredEndpointInfo.getServiceId())){
                                    Endpoint endpoint = new Endpoint(s, discoveredEndpointInfo.getEndpointName());
                                    mDiscoveredEndpoints.put(s, endpoint);
                                    onEndpointDiscovered(endpoint);
                                    connectToEndpoint(endpoint);
                                }
                            }

                            @Override
                            public void onEndpointLost(@NonNull String s) {
                                Log.d("Nearby Service", String.format("onEndpointLost(endpointId=%s)",s));
                            }
                        }, dOptions.build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onDiscoveryStarted();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mIsDiscovering = false;
                        Log.w("startDiscovering() failed!", e);
                        onDiscoveryFailed();
                    }
                });
    }

    /** Called when discovery successfully starts. Override this method to act on the event. */
    public void onDiscoveryStarted() {}

    /** Called when discovery fails to start. Override this method to act on the event. */
    public void onDiscoveryFailed() {}

    /**
     * Called when a remote endpoint is discovered. To connect to the device, call {@link
     * #connectToEndpoint(Endpoint)}.
     */
    public void onEndpointDiscovered(Endpoint endpoint) {}


    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            //An endpoint as found! Requesting connection!
            Nearby.getConnectionsClient(getApplicationContext())
                    .requestConnection(mName, s, connectionLifecycleCallback)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Successfully requested a connection.

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed
                        }
                    });
        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            //A previously discovered endpoint has gone away.
        }
    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            Log.d("Nearby Service", String.format("onConnectionInitiated(endpointId=%s, endpointName=%s)", s, connectionInfo.getEndpointName()));
            Endpoint endpoint = new Endpoint(s, connectionInfo.getEndpointName());
        mPendingConnections.put(s, endpoint);
        MainActivity.this.onConnectionInitiated(endpoint, connectionInfo);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            Log.d("Nearby Service", String.format("onConnectionResult(endpointId=%s, result=%s)", s, result));

            mIsConnecting = false;

            if(!result.getStatus().isSuccess()){
                Log.e("Nearby Service", String.format("Connection failed. Received status &s.",
                        String.valueOf(result.getStatus())));
                onConnectionFailed(mPendingConnections.remove(s));
                return;
            }
            connectToEndpoint(mPendingConnections.remove(s));
        };

        @Override
        public void onDisconnected(@NonNull String s) {
            if(!mEstablishedConnections.containsKey(s)){
                Log.w("Nearby Service", String.format("Unexpected disconnection from endpoint %s", s));
                return;
            }
            disconnectedFromEndpoint(mEstablishedConnections.get(s));
        }
    };

    public void connectToEndpoint(final Endpoint endpoint){
        Log.v("Nearby Service", "Sending a connection request to endpoint " + endpoint);
        mIsConnecting = true;

        //Ask to connect
        mConnectionsClient
                .requestConnection(mName, endpoint.getId(), mConnectionLifecycleCallback)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Nearby Service", "requestConnection() failed.", e);
                        mIsConnecting = false;
                        onConnectionFailed(endpoint);
                    }
                });
    }

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                    Log.d("Nearby Service", String.format("onConnecionInitiated(endpoint=%s, endpointName=%s)", s, connectionInfo.getEndpointName()));
                    Endpoint endpoint = new Endpoint(s, connectionInfo.getEndpointName());
                    mPendingConnections.put(s, endpoint);
                    MainActivity.this.onConnectionInitiated(endpoint, connectionInfo);
                }

                @Override
                public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
                    Log.d("Nearby Service", String.format("onConnectionResult(endpointId=%s, result=%s)", s, result));
                    mIsConnecting = false;
                    if(!result.getStatus().isSuccess()){
                        Log.e("Nearby Service", String.format("Connection failed! Received status %s.",String.valueOf(result.getStatus())));
                        onConnectionFailed(mPendingConnections.remove(s));
                        return;
                    }
                    connectedToEndpoint(mPendingConnections.remove(s));

                    //Send data

                    Log.d("Nearby Service", "idToken:" + idToken);
                    String uid = mAuth.getUid();
                    byte[] uidBytes = uid.getBytes();
                    Log.d("Nearby Service", "Sending "+new String(uidBytes));
                    Payload payload = Payload.fromBytes(uidBytes);
                    send(payload, s);
                }

                @Override
                public void onDisconnected(@NonNull String s) {
                    if(!mEstablishedConnections.containsKey(s)){
                        Log.w("Nearby Service", "Unexpected disconnection from endpoint "+s);
                        return;
                    }
                    disconnectedFromEndpoint(mEstablishedConnections.get(s));
                }
            };

    public void connectedToEndpoint(Endpoint endpoint){
        Log.d("Nearby Service", String.format("connectedToEndpoint(endpoint=%s",endpoint));
        mEstablishedConnections.put(endpoint.getId(), endpoint);
        onEndpointConnected(endpoint);
    }

    public void disconnectedFromEndpoint(Endpoint endpoint){
        Log.d("Nearby Service", String.format("disconnectedFromEndpoint(endpoint=%s)",endpoint));
        mEstablishedConnections.remove(endpoint.getId());
        onEndpointDisconnected(endpoint);
    }

    /** Called when someone has connected to us. Override this method to act on the event. */
    public void onEndpointConnected(Endpoint endpoint) {}

    /** Called when someone has disconnected. Override this method to act on the event. */
    public void onEndpointDisconnected(Endpoint endpoint) {}

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            Log.d("Nearby Service", String.format("onPayloadReceived(endpointId=%s, payload=%s)", s, payload));
            onReceive(mEstablishedConnections.get(s), payload);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            Log.d("Nearby Service", String.format("onPayloadTransferUpdate(endpointId=%s, update=%s)", s, payloadTransferUpdate));
        }
    };

    public void send(Payload payload) {
        send(payload, mEstablishedConnections.keySet());
    }

    public void send(Payload payload, Set<String> endpoints) {
        mConnectionsClient
                .sendPayload(new ArrayList<>(endpoints), payload)

                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Nearby Service","sendPayload() failed.", e);
                            }
                        });
    }

    public void send(Payload payload, String endpointId){
        mConnectionsClient
                .sendPayload(endpointId, payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.v("Nearby Service", String.format("sendPayload(): Successfully sent %s",payload.toString()));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Nearby Service", "sendPayload() failed.",e);
                    }
                });
    }

    /**
     * Someone connected to us has sent us data. Override this method to act on the event.
     *
     * @param endpoint The sender.
     * @param payload The data.
     */
    public void onReceive(Endpoint endpoint, Payload payload) {
        String payloadstring;

        if(payload.getType() == Payload.Type.BYTES){
            byte[] received = payload.asBytes();
            Log.w("Nearby Service", "Got with toString() "+received.toString());

            payloadstring = new String(received);
            Toast.makeText(getApplicationContext(), payloadstring, Toast.LENGTH_LONG).show();
            Log.d("Nearby Service", "Got: "+payloadstring);
            addToPersonList(payloadstring);
        }
    }

    /**
     * Called when a connection with this endpoint has failed. Override this method to act on the
     * event.
     */
    public void onConnectionFailed(Endpoint endpoint) {}



    public void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        // A connection to another device has been initiated! We'll use the auth token, which is the
        // same on both devices, to pick a color to use when we're connected. This way, users can
        // visually see which device they connected with.
        //mConnectedColor = COLORS[connectionInfo.getAuthenticationToken().hashCode() % COLORS.length];

        // We accept the connection immediately.
        acceptConnection(endpoint);
    }

    public void acceptConnection(Endpoint endpoint){
        mConnectionsClient
                .acceptConnection(endpoint.getId(), mPayloadCallback)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Nearby Service", "acceptConnection(), failed", e);
                    }
                });
    }

    /** Rejects a connection request. */
    protected void rejectConnection(Endpoint endpoint) {
        mConnectionsClient
                .rejectConnection(endpoint.getId())
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Nearby Service","rejectConnection() failed.", e);
                            }
                        });
    }


    //END

    /*private void editUserSettings(String id, String value){
        SharedPreferences.Editor editor = usersp.edit();
        editor.putString(id, value);
        editor.commit();
    }

     */

    /*private String getUserSettings(String id){
        if(usersp.contains(id)){
            return usersp.getString(id, "null");
        }
        Log.e("getUserSettings", "Couldn't get value from id" + id);
        return null;
    }

     */

    private void showControlCenter(){
        if(controlView.getVisibility() == View.GONE) {
            //Show ControlCenter
            //cameraView.setVisibility(View.GONE);
            mainView.setVisibility(View.GONE);
            controlView.setVisibility(View.VISIBLE);
        }else{
            //Hide ControlCenter
            //cameraView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.VISIBLE);
            controlView.setVisibility(View.GONE);
        }
    }

    private void showCameraView() {
        if(cameraViewText.getVisibility() == View.VISIBLE) {
            //profilecard.setVisibility(View.GONE);
            //cameraView.setVisibility(View.VISIBLE);

            personalnameText.setVisibility(View.GONE);
            peopleNearCard.setVisibility(View.GONE);
            placesNearbyCard.setVisibility(View.GONE);
            friendsCard.setVisibility(View.GONE);
            buyCoffeeCard.setVisibility(View.GONE);
            cameraViewText.setVisibility(View.GONE);
            debugView.setVisibility(View.VISIBLE);
            camerashutter.setVisibility(View.GONE);

        }else if(cameraViewText.getVisibility() == View.GONE){
            //profilecard.setVisibility(View.VISIBLE);
            //cameraView.setVisibility(View.INVISIBLE);
            camerashutter.setVisibility(View.VISIBLE);
            personalnameText.setVisibility(View.VISIBLE);
            peopleNearCard.setVisibility(View.VISIBLE);
            placesNearbyCard.setVisibility(View.VISIBLE);
            friendsCard.setVisibility(View.VISIBLE);
            buyCoffeeCard.setVisibility(View.VISIBLE);
            cameraViewText.setVisibility(View.VISIBLE);
            debugView.setVisibility(View.GONE);

        }
    }

    private void showCameraView(boolean state) {
        if(cameraViewText.getVisibility() == View.VISIBLE || state == true) {
            //profilecard.setVisibility(View.GONE);

            personalnameText.setVisibility(View.GONE);
            peopleNearCard.setVisibility(View.GONE);
            placesNearbyCard.setVisibility(View.GONE);
            friendsCard.setVisibility(View.GONE);
            buyCoffeeCard.setVisibility(View.GONE);
            cameraViewText.setVisibility(View.GONE);
            debugView.setVisibility(View.VISIBLE);
            camerashutter.setVisibility(View.GONE);
            cameraView.setVisibility(View.VISIBLE);

        }else if(cameraViewText.getVisibility() == View.GONE || state == false){
            //profilecard.setVisibility(View.VISIBLE);

            personalnameText.setVisibility(View.VISIBLE);
            peopleNearCard.setVisibility(View.VISIBLE);
            placesNearbyCard.setVisibility(View.VISIBLE);
            friendsCard.setVisibility(View.VISIBLE);
            buyCoffeeCard.setVisibility(View.VISIBLE);
            cameraViewText.setVisibility(View.VISIBLE);
            debugView.setVisibility(View.GONE);
            camerashutter.setVisibility(View.VISIBLE);
            cameraView.setVisibility(View.INVISIBLE);
        }
    }

    private void reloadNearby(){

    }

    private void lookfordata() {
        showStatusBar("Downloading data", 3000);
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Firebase User Database", document.getId() + "=>" + document.getData());
                            }
                        }else{
                            //Error
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        /*cameraView.openAsync(CameraView.findCameraId(
                Camera.CameraInfo.CAMERA_FACING_BACK));

         */


        /*if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 105);
        }*/

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onPause() {
        super.onPause();
        //cameraView.close();
    }

    public void signin(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            personalnameText.setText(name);
            email = account.getEmail();

            //Check, if user is in database
            checkUserInDatabase(idToken);

            //Start advertisment
            //startAdvertisment();

            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
            startMain();
            //
        }else{
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. "+result);
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();

        }
    }

    private void checkUserInDatabase(String idToken) {
        db.collection("users").document(idToken).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                //exists
                            }else{
                                //Send to add new Userdetails
                                Bundle bundle = new Bundle();
                                bundle.putString("id", idToken);
                            }
                        }else{
                            Log.d("Firebase", "Failed with: ", task.getException());
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(AuthCredential credential){

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            //Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            //Eingeloggt
                            Toast.makeText(getApplicationContext(), "Logged in!",
                                    Toast.LENGTH_SHORT).show();
                            //Glide.with(getApplicationContext()).load(account.getPhotoUrl()).into(profileImage);
                        }else{
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void getTracks() {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            updateSong();
        });
    }

    private void updateSong() {
        if (recentlyPlayedTracks.size() > 0) {
            //songView.setText(recentlyPlayedTracks.get(0).getName());
            song = recentlyPlayedTracks.get(0);


        }

    }


    //
    // Google things
    //


    //
    //Bluetooth
    //

    public void initRecyclerView(newPerson[] data){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.personrecyclerview);
        recyclerView.setLayoutManager(layoutManager);
        PersonRecyclerViewAdapter adapter = new PersonRecyclerViewAdapter(this,data);
        recyclerView.setAdapter(adapter);

    }

    public void initPlaceRecyclerView(newPlace[] data){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.placesrecyclerview);
        recyclerView.setLayoutManager(layoutManager);
        PlacesRecyclerAdapter adapter = new PlacesRecyclerAdapter(this,data);
        recyclerView.setAdapter(adapter);
    }

    public boolean checkPermission(String permission){
        if (ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }

    String userName;
    String userBio;
    String userImg;
    private void addToPersonList(String useruid){

        db.collection("users").document(useruid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userName = (String) documentSnapshot.get("name");
                        userBio = (String) documentSnapshot.get("bio");
                        userImg = (String) documentSnapshot.get("image");
                    }
                });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        StorageReference ref = storageReference.child("/images/turksat46");


        newPerson data[] = new newPerson[1];
        data[0] = new newPerson(R.drawable.logo, userName, useruid);
        initRecyclerView(data);
    }

    private void setTestList(){
        //Firebase


        ArrayList<BluetoothLE> aBleAvailable  = new ArrayList<>();
        //newPerson data[] = new newPerson[ble.getListDevices().size()+1];
        newPerson data[] = new newPerson[1];
        newPlace dataplace[] = new newPlace[1];

        data[0] = new newPerson(R.drawable.img, "Test", account.getIdToken());
        //dataplace[0] = new newPlace(R.drawable.logo, "McDonalds");
        initRecyclerView(data);
        //initPlaceRecyclerView(dataplace);
        if(mDiscoveredEndpoints.size() >0){
            for(int i = 0; i < mDiscoveredEndpoints.size(); i++){
                //data[i] = new newPerson(R.drawable.logo, )
            }
        }

        /*if(ble.getListDevices().size() > 0) {
            for (int i = 0; i < ble.getListDevices().size(); i++) {
                if(ble.getListDevices().get(i).getName() != null) {
                    data[i] = new newPerson(R.drawable.logo, ble.getListDevices().get(i).getName());
                }

            }
            data[ble.getListDevices().size()] = new newPerson(R.drawable.logo, "Turksat46");
            initRecyclerView(data);


            /*listBle.setAdapter(mAdapter);
            listBle.setOnItemClickListener((parent, view, position, id) -> {
                BluetoothLE  itemValue = (BluetoothLE) listBle.getItemAtPosition(position);
                ble.connect(itemValue.getDevice(), bleCallbacks());
            });
        }else{
            dAlert = setDialogInfo("Ups", "We do not find active devices", true);
            dAlert.show();
        }


        }else{
            Toast.makeText(this, "Couldn't find anyone near you :((", Toast.LENGTH_LONG);
        }
        */
    }

    private void showStatusBar(String Text, long duration){
        Handler mHandler = new Handler();
        statusTextView.setText(Text);
        statusBar.setVisibility(View.VISIBLE);

        mHandler.postDelayed(()->{

            statusBar.setVisibility(View.GONE);
        }, duration);

    }

    @Override
    public void onClick(View view, int position) {
        //Connect to device and get details
        Toast.makeText(getApplicationContext(), "Clicked on item nr." + Integer.toString(position), Toast.LENGTH_LONG);
    }


    //AR
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        cameraView.setOneShotPreviewCallback(this);
        if(camera.getParameters().getPreviewFormat() == ImageFormat.NV21){
            //Do the checks
            int height = camera.getParameters().getPreviewSize().height;
            int width = camera.getParameters().getPreviewSize().width;

            checkIfScreenIsBlack(new NV21Image(bytes, width, height));
        }
    }

    private void checkIfScreenIsBlack(NV21Image nv21Image) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Testing
        //Log.e("Sensor Data", Float.toString(sensorEvent.values[0]));
        float azimutCamera = sensorEvent.values[0];
        float polarCamera = 90 - sensorEvent.values[1];
        //setTestPoint(azimutCamera, polarCamera);

        if(compassview != null){
            compassview.setWinkel(-sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Endpoint class
    /** Represents a device we can talk to. */
    public class Endpoint {
        @NonNull private final String id;
        @NonNull private final String name;

        private Endpoint(@NonNull String id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Endpoint) {
                Endpoint other = (Endpoint) obj;
                return id.equals(other.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Endpoint{id=%s, name=%s}", id, name);
        }
    }
}


