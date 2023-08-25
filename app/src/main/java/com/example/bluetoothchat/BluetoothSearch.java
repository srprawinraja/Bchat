package com.example.bluetoothchat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class BluetoothSearch extends AppCompatActivity {
    private ListView available_devices;
    private TextView text;
    private ProgressBar progress;
    private ImageView imageView;
    private  ImageButton searchAll;
    private final Map<String, String> dictionary = new HashMap<>();
    Integer[] don2={
            R.drawable.ic_baseline_person_24
    };
    String[] don;
    private  BluetoothDevice[] btArray;

    public String dev_address;

    public int index=0;
    private final Vector<String> friends = new Vector<>();



    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friends.clear();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_bluetooth_search);
        available_devices=findViewById(R.id.listHome);
        available_devices.setDivider(null);
        text=findViewById(R.id.textView);
        available_devices.setDividerHeight(60);
        progress=findViewById(R.id.progress);
        ImageButton back = findViewById(R.id.back);
        searchAll=findViewById(R.id.bluetooth_button);
        imageView=findViewById(R.id.img);
        progress.setVisibility(View.VISIBLE);
        searchAll.setVisibility(View.INVISIBLE);
        int discoverableTimeout = bluetoothAdapter.getScanMode();
        if (discoverableTimeout != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // Set the duration in seconds
            startActivity(discoverableIntent);
        }


        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();


        btArray=new BluetoothDevice[70];

        back.setOnClickListener(v -> {
            Intent i= new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        });
        searchAll.setOnClickListener(v -> {
            imageView.setVisibility(View.GONE);
            String change = Build.VERSION.RELEASE;
            change = change.substring(0, 2);
            float androidVersion = Float.parseFloat(change);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(androidVersion<=9){

            friends.clear();
            progress.setVisibility(View.VISIBLE);
            searchAll.setVisibility(View.INVISIBLE);

            Toast.makeText(getApplicationContext(), "searching...", Toast.LENGTH_SHORT).show();


            if (bluetoothAdapter.isDiscovering()) {
                Log.i("america", "america");
                bluetoothAdapter.cancelDiscovery();

            }
            bluetoothAdapter.startDiscovery();
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // Set the duration in seconds
            startActivity(discoverableIntent);

        }
            else if(!isLocationEnabled){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission")
                        .setMessage("For Android 10 or greater version location is necessary to scan for other devices. ")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Do something when the OK button is clicked
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Do something when the Cancel button is clicked
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });





    }





    BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {

        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();


            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(index<=1) {
                    btArray[index] = device;

                    index+=1;
                }


                if(!Objects.equals(dictionary.get(device.getAddress()), "yes")) {
                    dev_address = device.getAddress();
                    friends.add(device.getName());
                }
                dictionary.put(device.getAddress(),"yes");

            }
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                if(friends.size()==0) {
                    imageView.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "no device found", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                    searchAll.setVisibility(View.VISIBLE);
                }
                else{
                    if(friends.size()==1){

                        text.setText(friends.size()+" friend");
                    }
                    else{
                        text.setText(friends.size()+" friends");
                    }


                    update();

                    Toast.makeText(context, "device found", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                    searchAll.setVisibility(View.VISIBLE);
                }

            }

        }
    };

    private void update() {
        don  = friends.toArray(new String[friends.size()]);
        geek gk = new geek(this, don, don2, "search");


        available_devices.setAdapter(gk);
    }


    @SuppressLint("MissingPermission")
    private void init() {







        available_devices.setOnItemClickListener((parent, view, position, id) -> {
            bluetoothAdapter.cancelDiscovery();
            String info = (String) (available_devices.getItemAtPosition(position));

            Intent i=new Intent(getApplicationContext(),BluetoothChatting.class);
            i.putExtra("device_name",info);
            i.putExtra("device_address",dev_address);

            i.putExtra("option",btArray[position]);


            startActivity(i);

        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter fill= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener , fill);

        IntentFilter fill1= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener , fill1);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        bluetoothAdapter.startDiscovery();
        init();
    }
}