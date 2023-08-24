package com.turksat46.freakslabor;

//TODO: Change idToken to UUID or Android.Secure!

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FreaksNearby {
    /**Strategy*/
    Strategy strategy = Strategy.P2P_CLUSTER;

    /** Maps of Devices*/
    ConnectionsClient connectionsClient;
    private final Map<String, Endpoint> discoveredEndpoints = new HashMap<>();
    private final Map<String, Endpoint> pendingConnections = new HashMap<>();
    private final Map<String, Endpoint> establishedConnections = new HashMap<>();

    /** Curent States*/
    private boolean isConnecting, isDiscovering, isAdvertising = false;

    private String name;
    private String service_id = "com.turksat46.freakslabor";
    private Context context;

    public String uid;

    /**Main Engine*/

    public FreaksNearby(Context context, String uid) {
        connectionsClient = Nearby.getConnectionsClient(context);
        name = generateRandomName();
        this.context = context;
        this.uid = uid;
    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    public void startAdvertising(){
        isAdvertising = true;
        AdvertisingOptions.Builder adOptions = new AdvertisingOptions.Builder();
        adOptions.setStrategy(strategy);

        connectionsClient
                .startAdvertising(name,
                        service_id,
                        mConnectionLifecycleCallback,
                        adOptions.build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.v("Nearby Service", "Now advertising endpoint " + name);
                        onAdvertisingStarted();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isAdvertising = false;
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
        isDiscovering = true;
        discoveredEndpoints.clear();
        DiscoveryOptions.Builder dOptions = new DiscoveryOptions.Builder();
        dOptions.setStrategy(strategy);
        connectionsClient
                .startDiscovery(service_id,
                        new EndpointDiscoveryCallback() {
                            @Override
                            public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                                Log.d("Nearby Service", String.format("onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",s, discoveredEndpointInfo.getServiceId(), discoveredEndpointInfo.getEndpointName()));
                                if(service_id.equals(discoveredEndpointInfo.getServiceId())){
                                    Endpoint endpoint = new Endpoint(s, discoveredEndpointInfo.getEndpointName());
                                    discoveredEndpoints.put(s, endpoint);
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
                        isDiscovering = false;
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
            Nearby.getConnectionsClient(context)
                    .requestConnection(name, s, connectionLifecycleCallback)
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
            pendingConnections.put(s, endpoint);
            FreaksNearby.this.onConnectionInitiated(endpoint, connectionInfo);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            Log.d("Nearby Service", String.format("onConnectionResult(endpointId=%s, result=%s)", s, result));

            isConnecting = false;

            if(!result.getStatus().isSuccess()){
                Log.e("Nearby Service", String.format("Connection failed. Received status &s.",
                        String.valueOf(result.getStatus())));
                onConnectionFailed(pendingConnections.remove(s));
                return;
            }
            connectToEndpoint(pendingConnections.remove(s));
        };

        @Override
        public void onDisconnected(@NonNull String s) {
            if(!establishedConnections.containsKey(s)){
                Log.w("Nearby Service", String.format("Unexpected disconnection from endpoint %s", s));
                return;
            }
            disconnectedFromEndpoint(establishedConnections.get(s));
        }
    };

    public void connectToEndpoint(final Endpoint endpoint){
        Log.v("Nearby Service", "Sending a connection request to endpoint " + endpoint);
        isConnecting = true;

        //Ask to connect
        connectionsClient
                .requestConnection(name, endpoint.getId(), mConnectionLifecycleCallback)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Nearby Service", "requestConnection() failed.", e);
                        isConnecting = false;
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
                    pendingConnections.put(s, endpoint);
                    FreaksNearby.this.onConnectionInitiated(endpoint, connectionInfo);
                }

                @Override
                public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
                    Log.d("Nearby Service", String.format("onConnectionResult(endpointId=%s, result=%s)", s, result));
                    isConnecting = false;
                    if(!result.getStatus().isSuccess()){
                        Log.e("Nearby Service", String.format("Connection failed! Received status %s.",String.valueOf(result.getStatus())));
                        onConnectionFailed(pendingConnections.remove(s));
                        return;
                    }
                    connectedToEndpoint(pendingConnections.remove(s));

                    //Send data

                    String idToken = null;
                    Log.d("Nearby Service", "idToken:" + idToken);
                    //String uid = mAuth.getUid();
                    byte[] uidBytes = uid.getBytes();
                    Log.d("Nearby Service", "Sending "+new String(uidBytes));
                    Payload payload = Payload.fromBytes(uidBytes);
                    send(payload, s);
                }

                @Override
                public void onDisconnected(@NonNull String s) {
                    if(!establishedConnections.containsKey(s)){
                        Log.w("Nearby Service", "Unexpected disconnection from endpoint "+s);
                        return;
                    }
                    disconnectedFromEndpoint(establishedConnections.get(s));
                }
            };

    public void connectedToEndpoint(Endpoint endpoint){
        Log.d("Nearby Service", String.format("connectedToEndpoint(endpoint=%s",endpoint));
        establishedConnections.put(endpoint.getId(), endpoint);
        onEndpointConnected(endpoint);
    }

    public void disconnectedFromEndpoint(Endpoint endpoint){
        Log.d("Nearby Service", String.format("disconnectedFromEndpoint(endpoint=%s)",endpoint));
        establishedConnections.remove(endpoint.getId());
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
            onReceive(establishedConnections.get(s), payload);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            Log.d("Nearby Service", String.format("onPayloadTransferUpdate(endpointId=%s, update=%s)", s, payloadTransferUpdate));
        }
    };

    public void send(Payload payload) {
        send(payload, establishedConnections.keySet());
    }

    public void send(Payload payload, Set<String> endpoints) {
        connectionsClient
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
        connectionsClient
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
            Toast.makeText(context, payloadstring, Toast.LENGTH_LONG).show();
            Log.d("Nearby Service", "Got: "+payloadstring);
            //addToPersonList(payloadstring);
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
        connectionsClient
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
        connectionsClient
                .rejectConnection(endpoint.getId())
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Nearby Service","rejectConnection() failed.", e);
                            }
                        });
    }



}
