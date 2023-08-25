package com.example.bluetoothchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Profile extends AppCompatActivity {
    public  ImageView profileColor,profileBackground,profileImage;
    private  AmbilWarnaDialog ambilWarnaDialog;
    private int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Button profile_background = findViewById(R.id.background);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        String myString = sharedPreferences.getString("name", "");

        Button buttonEdit = findViewById(R.id.button_edit);
        buttonEdit.setText(myString);
        Button color = findViewById(R.id.color);
        defaultColor= ContextCompat.getColor(getApplicationContext(),R.color.profile);
        profileColor=findViewById(R.id.profileColor);
        profileBackground=findViewById(R.id.profileBackground);
        profileImage=findViewById(R.id.profileImage);
        ImageButton backEdit = findViewById(R.id.back_setting2);

        try {
            int colorProfile = sharedPreferences.getInt("profileColor", 0);
            int colorBackground = sharedPreferences.getInt("profileBackground", 0);

            profileColor.setBackgroundColor(colorProfile);

            Drawable drawable = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_person_24);
            if (drawable != null) {
                drawable = drawable.mutate();
                drawable.setTint(colorProfile);
            }



            profileImage.setImageDrawable(drawable);

            //back
            profileBackground.setBackgroundColor(colorBackground);
            profileImage.setBackgroundColor(colorBackground);
        }
        catch (Resources.NotFoundException e) {
            // Handle the exception if the color resource is not found

        }
        backEdit.setOnClickListener(v -> {
            Intent i=new Intent(getApplicationContext(),Setting.class);
            startActivity(i);
        });
        color.setOnClickListener(v -> {
            ambilWarnaDialog = new AmbilWarnaDialog(Profile.this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color1) {
                    try {
                        profileColor.setBackgroundColor(color1);

                        Drawable drawable = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_person_24);
                        if (drawable != null) {
                            drawable = drawable.mutate();
                            drawable.setTint(color1);
                        }



                        profileImage.setImageDrawable(drawable);

// If you are using an ImageView to display the drawable
                        SharedPreferences sharedPreferences1 = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        editor1.putInt("profileColor", color1);
                        editor1.apply();
                    }
                    catch (Resources.NotFoundException e) {
                        // Handle the exception if the color resource is not found

                    }
                }
            });
            ambilWarnaDialog.show();


        });

        profile_background.setOnClickListener(v -> {
            ambilWarnaDialog = new AmbilWarnaDialog(Profile.this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color12) {
                    defaultColor= color12;
                    try {
                        profileBackground.setBackgroundColor(color12);
                        profileImage.setBackgroundColor(color12);


                        SharedPreferences sharedPreferences12 = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor12 = sharedPreferences12.edit();
                        editor12.putInt("profileBackground", color12);
                        editor12.apply();
                    }
                    catch (Resources.NotFoundException e) {
                        // Handle the exception if the color resource is not found

                    }
                }
            });
            ambilWarnaDialog.show();
        });
        buttonEdit.setOnClickListener(v -> {
            Intent i= new Intent(getApplicationContext(),editName.class);
            startActivity(i);

        });

    }
}