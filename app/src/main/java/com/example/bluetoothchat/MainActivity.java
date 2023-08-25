package com.example.bluetoothchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {
    private Intent i;
   private  Integer[] don2={
            R.drawable.ic_baseline_person_24
    };

    public ListView listNew;
    private SharedPreferences sharedPreferences;
    private boolean isLocationEnabled;
    public String myString;

    private Float  androidVersion;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        myString = sharedPreferences.getString("name", "");
        if (myString.length() == 0) {
            i = new Intent(getApplicationContext(), Name.class);
            startActivity(i);
        } else {
            setContentView(R.layout.activity_main);


// Get the current time



        String change = Build.VERSION.RELEASE;
        change = change.substring(0, 2);
        androidVersion = Float.valueOf(change);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listNew = findViewById(R.id.listnew);
        listNew.setDivider(null);
        listNew.setDividerHeight(60);
            ImageButton bluetoothPermission = findViewById(R.id.blue);
            ImageButton setting = findViewById(R.id.imageButton);

        setting.setOnClickListener(v -> {
            i = new Intent(getApplicationContext(), Setting.class);
            startActivity(i);
        });

        bluetoothPermission.setOnClickListener(v -> {

            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }


            if (androidVersion > 9) {
                requestpermission();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                i = new Intent(getApplicationContext(), BluetoothSearch.class);
                startActivity(i);
            }

        });
        listNew.setOnItemClickListener((parent, view, position, id) -> {
            String info = (String) (listNew.getItemAtPosition(position));
        });
    }

    }

    private void requestpermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);


        }

        else{

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isLocationEnabled) {
                    i = new Intent(getApplicationContext(), BluetoothSearch.class);

                    startActivity(i);
                }
                else{
                   checkLocationServicesEnabled();
                }
            }
        }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {


           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               checkLocationServicesEnabled();
            } else {


               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("Permission")
                       .setMessage("For Android 10 or greater version location is necessary to scan for other devices. ")
                       .setPositiveButton("OK", (dialog, which) -> {
                           // Do something when the OK button is clicked
                           requestpermission();
                       })
                       .setNegativeButton("Cancel", (dialog, which) -> {
                           // Do something when the Cancel button is clicked
                       });

               AlertDialog alertDialog = builder.create();
               alertDialog.show();
                // Permission denied


            }
        }
        if(requestCode==200){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                i = new Intent(getApplicationContext(), BluetoothSearch.class);

                startActivity(i);
            }
        }

    }

    private void checkLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are not enabled, prompt the user to enable them
             isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isLocationEnabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissions")
                        .setMessage("For Android 10 or greater version location is necessary to scan for other devices.enable your location ")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Do something when the OK button is clicked
                            isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            if(isLocationEnabled){
                                i=new Intent(getApplicationContext(),BluetoothSearch.class);
                                startActivity(i);
                            }else {
                                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(settingsIntent);
                                isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                if(isLocationEnabled){
                                    i=new Intent(getApplicationContext(),BluetoothSearch.class);
                                    startActivity(i);
                                }
                            }

                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Do something when the Cancel button is clicked
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                i=new Intent(getApplicationContext(),BluetoothSearch.class);
                startActivity(i);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.setName(myString);

        displayStore displayStore =new displayStore(getApplicationContext());

        Vector<String> displayList = (displayStore.getListFriends());
        if(displayList.size()!=0) {
            String[] don = (String[]) displayList.toArray(new String[0]);
            Log.i("jesus", String.valueOf(displayList));


            com.example.bluetoothchat.geek geek = new geek(this, don, don2, "main");
            listNew.setAdapter(geek);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        myString = sharedPreferences.getString("oldName", "");
        mBluetoothAdapter.setName(myString);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}

class  displayStore extends SQLiteOpenHelper {
    public static final String FRIEND_TABLE = "FRIEND_TABLE";
    public static final String FRIEND_NAME = "FRIEND_NAME";


    public  Vector<String> listFriends = new Vector<>();

    public displayStore(@Nullable Context context) {
        super(context,"friend.ds" , null, 1);

        SQLiteDatabase db1=this.getReadableDatabase();
        try {
            @SuppressLint("Recycle") Cursor cursor = db1.query(FRIEND_TABLE, null, null, null, null, null, null);


            while (cursor.moveToNext()) {

                @SuppressLint("Range") String columnValue = cursor.getString(cursor.getColumnIndex(FRIEND_NAME));

                listFriends.add(columnValue);


            }
        }
        catch (Exception e) {
            e.printStackTrace();

            // Handle the exception, show an error message, or take appropriate action
        }
    }

    public Vector<String> getListFriends() {
        return listFriends;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

