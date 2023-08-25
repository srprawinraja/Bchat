package com.example.bluetoothchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class editName extends AppCompatActivity {
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        edit=findViewById(R.id.editTextPhone);

        Button ok = findViewById(R.id.button2);
        ImageButton back = findViewById(R.id.back_setting2);
        edit.requestFocus();

        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        }, 200);



        back.setOnClickListener(v -> {
            Intent i=new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        });

        ok.setOnClickListener(v -> {
            String name=edit.getText().toString();

            if(name.length()==0){
                Toast.makeText(editName.this, "enter your name", Toast.LENGTH_SHORT).show();
                Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vi.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vi.vibrate(100);
                }
            }
            else{
                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                Toast.makeText(editName.this, "Change your Name Successfully", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),Profile.class);
                startActivity(i);
            }
        });
    }


}