package com.example.bluetoothchat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class Name extends AppCompatActivity {

    private AppCompatEditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        editText=findViewById(R.id.editTextPhone);
        Button next = findViewById(R.id.button2);

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "ApplySharedPref", "ObsoleteSdkInt"})
            @Override
            public void onClick(View v) {
                String enterText= Objects.requireNonNull(editText.getText()).toString();
                if(enterText.length()!=0) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
                    editor.putString("oldName",adapter.getName());
                    editor.putString("name", enterText);
                    editor.putBoolean("notificationHide",true);
                    editor.putBoolean("notificationSound",true);

                    int color = ContextCompat.getColor(Name.this, R.color.profile);
                    int colorBack = ContextCompat.getColor(Name.this, R.color.background);
                    editor.putInt("profileColor",color);
                    editor.putInt("profileBackground",colorBack);

                    editor.commit();

                    Intent i=new Intent(getApplicationContext(),MainActivity.class);
                    if(enterText.length()==0){
                        Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vi.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            //noinspection deprecation
                            vi.vibrate(100);
                        }

                        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 1000);
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                        Toast.makeText(Name.this, "please enter your name " + enterText, Toast.LENGTH_SHORT).show();


                    }
                    else {
                        i.putExtra("username", enterText);
                        Toast.makeText(Name.this, "Welcome! " + enterText, Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                }
                else{
                    Toast.makeText(Name.this, "please enter your name", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}