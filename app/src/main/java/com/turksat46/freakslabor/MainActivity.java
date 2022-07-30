//This is the main program of FreaksLabor, an app, in which you can discover new places, meet people
//and more...
//Written by: Turksat46
//This app shouldn't be used to harm other people or else!!!
//This app should work with many APIs like Spotify or more
//I don't want, collect or sell any data, that's why I want to make it open-source
//

package com.turksat46.freakslabor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.Pair;
import android.view.OrientationListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.ederdoski.simpleble.interfaces.BleCallback;
import com.ederdoski.simpleble.models.BluetoothLE;
import com.ederdoski.simpleble.utils.BluetoothLEHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.uriio.beacons.Beacons;
import com.uriio.beacons.model.EddystoneURL;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Region;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import Connectors.SongService;
import de.markusfisch.android.cameraview.widget.CameraView;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    // Main shittt
    CameraView cameraView;
    CameraManager cameraManager;
    ImageView coverView;
    ImageView personalimg;
    TextView personalnameText;

    CardView statusBar;

    Button activateButton;
    Button shareButton;
    Button supportButton;
    Button reloadButton;

    Handler handler = new Handler();

    BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
    BluetoothLEHelper ble;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private BleCallback bleCallbacks(){

        return new BleCallback(){

            @Override
            public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to GATT server.", Toast.LENGTH_SHORT).show());
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Disconnected from GATT server.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
                super.onBleServiceDiscovered(gatt, status);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e("Ble ServiceDiscovered","onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onBleCharacteristicChange(gatt, characteristic);
                Log.i("BluetoothLEHelper","onCharacteristicChanged Value: " + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleRead(gatt, characteristic, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("TAG", Arrays.toString(characteristic.getValue()));
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "onCharacteristicRead : "+Arrays.toString(characteristic.getValue()), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleWrite(gatt, characteristic, status);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "onCharacteristicWrite Status : " + status, Toast.LENGTH_SHORT).show());
            }
        };
    }
    ListView newPersonList;

    CardView profilecard;

    Song song;

    private SongService songService;
    ArrayList<Song> recentlyPlayedTracks;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    boolean mScanning;
    Handler mHandler;

    ProductDetails productDetails;

    //Google Play Billing
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
        }
    };

    private BillingClient billingClient;

    QueryProductDetailsParams queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                    .setProductList(
                            ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("coffee")
                                            .setProductType(BillingClient.ProductType.INAPP)
                                            .build()))
                    .build();


    //Main Program
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        songService = new SongService(getApplicationContext());
        cameraView = (CameraView) findViewById(R.id.camera_view);
        cameraView.setUseOrientationListener(true);
        coverView = (ImageView) findViewById(R.id.songCoverView);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        statusBar = (CardView)findViewById(R.id.statuscard);
        statusBar.setVisibility(View.GONE);


        //Google Play Billing

        //Bluetooth
        ble = new BluetoothLEHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bluetoothAdapter.isMultipleAdvertisementSupported())
        {
            BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

            AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
            //Define a service UUID according to your needs
            ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );
            dataBuilder.addServiceData(pUuid, "D".getBytes());
            dataBuilder.setIncludeDeviceName(true);


            AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
            settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
            settingsBuilder.setTimeout(0);

            //Use the connectable flag if you intend on opening a Gatt Server
            //to allow remote connections to your device.
            settingsBuilder.setConnectable(true);

            AdvertiseCallback advertiseCallback=new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.i("Advertiser", "onStartSuccess: ");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    Log.e("Advertiser", "onStartFailure: "+errorCode );
                }
            };
            advertiser.startAdvertising(settingsBuilder.build(),dataBuilder.build(),advertiseCallback);
        }

        Beacons.initialize(this);
        new EddystoneURL("google.com").start();

        activateButton = (Button) findViewById(R.id.activatebutton);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), presentation.class));
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
                launchbillingprocess();
            }
        });

        personalimg = (ImageView)findViewById(R.id.personalimg);
        personalnameText=(TextView)findViewById(R.id.personalnameText);

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
                startActivity(intent);

                /*
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());

                 */
            }
        });

        reloadButton = (Button)findViewById(R.id.reloadbutton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCollars();
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
        cameraView.setOnCameraListener(new CameraView.OnCameraListener() {
            @Override
            public void onConfigureParameters(Camera.Parameters parameters) {
                // set additional camera parameters here
                CameraView.setAutoFocus(parameters);
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
            }

            @Override
            public void onCameraStopping(Camera camera) {
                // clean up
            }
        });

        /*Check permissions
        if(!checkPermission(Manifest.permission.BLUETOOTH_ADMIN)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oh no... :(");
            builder.setMessage("I didn't get the necessary permissions from you :( Don't worry, I can show you, what I need, if you want me to :D");
            builder.setNegativeButton("NO >(", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setPositiveButton("Ok, let's go!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, presentation.class);
                    startActivity(intent);
                }
            });
            builder.create().show();

        }

         */

        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oh no... :(");
            builder.setMessage("I didn't get the bluetooth permissions from you :( Don't worry, I can show you, what I need, if you want me to :D");
            builder.setNegativeButton("NO >(", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setPositiveButton("Ok, let's go!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, presentation.class);
                    startActivity(intent);
                }
            });
            builder.create().show();

        }

        if(!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oh no... :(");
            builder.setMessage("I didn't get the location permissions from you :( Don't worry, I can show you, what I need, if you want me to :D");
            builder.setPositiveButton("Ok, let's go!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, presentation.class);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();

        }

        //Start bluetooth
    }

    @Override
    public void onResume() {
        super.onResume();/*
        cameraView.openAsync(CameraView.findCameraId(
                Camera.CameraInfo.CAMERA_FACING_BACK));
                */
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.close();
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
    public void launchbillingprocess(){
         billingClient = BillingClient.newBuilder(MainActivity.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        if (!billingClient.isReady()) {
            Log.d("Billing", "BillingClient: Start connection...");
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {

                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                }
            });
        }

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        // check billingResult
                        // process returned productDetailsList
                        productDetails = productDetailsList.get(0);
                    }
                }
        );
        // An activity reference from which the billing flow will be launched.
        Activity activity = MainActivity.this;

        ImmutableList productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)
                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
                                .setOfferToken(String.valueOf(productDetails.getSubscriptionOfferDetails()))
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

// Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }


    //
    //Bluetooth
    //


    public void initializeBT() {

        if(bluetoothAdapter ==null||!bluetoothAdapter.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, 1);
        }

    }

    public void connectProfileTEST(boolean online){
        Intent intent = new Intent(this, profileActivity.class);
        intent.putExtra("online", online);
        intent.putExtra("id", 0);
        startActivity(intent);
    }

    public void reloadNewPersonList(newPerson[] data){
        newPersonAdapter adapter = new newPersonAdapter(this,
                R.layout.new_person_item, data);
        newPersonList.setAdapter(adapter);

    }

    public void initRecyclerView(newPerson[] data){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,data);
        recyclerView.setAdapter(adapter);

    }

    public boolean checkPermission(String permission){
        if (ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }

    private void setList(){

        ArrayList<BluetoothLE> aBleAvailable  = new ArrayList<>();
        newPerson data[] = new newPerson[ble.getListDevices().size()+1];
        if(ble.getListDevices().size() > 0) {
            for (int i = 0; i < ble.getListDevices().size(); i++) {
                aBleAvailable.add(new BluetoothLE(ble.getListDevices().get(i).getName(), ble.getListDevices().get(i).getMacAddress(), ble.getListDevices().get(i).getRssi(), ble.getListDevices().get(i).getDevice()));
                data[i] = new newPerson(R.drawable.ic_launcher_background, ble.getListDevices().get(i).getName());

            }
            data[ble.getListDevices().size()] = new newPerson(R.drawable.ic_launcher_background, "Turksat46");
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

             */
        }else{
            Toast.makeText(this, "Couldn't find anyone near you :((", Toast.LENGTH_LONG);
        }
    }


    private void scanCollars(){

        if(!ble.isScanning()) {


            Handler mHandler = new Handler();
            ble.scanLeDevice(true);

            statusBar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(() -> {
                statusBar.setVisibility(View.GONE);
                setList();
            },ble.getScanPeriod());

        }
    }

    //NEW WAY TO SCAN FOR PEOPLE AND USE FOR FUTURE INTENTIONS

    Beacon beacon = new Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
            .setTxPower(-59)
            //.setDataFields(Arrays.asList(new Long[] {0l})) // Remove this for beacon layouts without d: fields
            .build();

    /* Change the layout below for other beacon types
    BeaconParser beaconParser = new BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
    BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            Log.e("BEACON", "Advertisement start failed with code: "+errorCode);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i("BEACON", "Advertisement start succeeded.");
        }
    });

     */

    //Beacon start advertising!



    @Override
    public void onClick(View view, int position) {
        //Connect to device and get details
        Toast.makeText(getApplicationContext(), "Clicked on item nr." + Integer.toString(position), Toast.LENGTH_LONG);
    }
}
