package com.example.finalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    public static ListView listitem;
    Button endbtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load data from resources
        String[] arr = getResources().getStringArray(R.array.food_items);
        String[] priceArr = getResources().getStringArray(R.array.food_prices);
        String[] imageNames = getResources().getStringArray(R.array.food_images);
        String[] descriptions = getResources().getStringArray(R.array.food_descriptions); // add this in strings.xml

        // Convert images
        int[] imageArr = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageArr[i] = getResources().getIdentifier(imageNames[i], "drawable", getPackageName());
        }

        // Convert prices
        int[] prices = new int[priceArr.length];
        for (int i = 0; i < priceArr.length; i++) {
            prices[i] = Integer.parseInt(priceArr[i]);
        }

        // Adapter
        listitem = findViewById(R.id.listItem);
        MyAdapter ad = new MyAdapter(this, R.layout.menu_layout, arr, imageArr, prices);
        listitem.setAdapter(ad);

        // Show AlertDialog when item clicked
        listitem.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity2.this)
                    .setTitle(arr[position])
                    .setMessage(descriptions[position])
                    .setPositiveButton("OK", null)
                    .create();

            dialog.setOnShowListener(d -> {
                // Change dialog background color
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A96531"))); // dark gray

                // Change title and message text color
                TextView title = dialog.findViewById(android.R.id.title);
                TextView message = dialog.findViewById(android.R.id.message);
                if (title != null) title.setTextColor(Color.WHITE);
                if (message != null) message.setTextColor(Color.WHITE);

                // Change button text color
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            });

            dialog.show();
        });


        // End Order Button
        endbtn = findViewById(R.id.endbtn);
        endbtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this, OrderSummaryActivity.class);
            startActivity(intent2);
        });
    }
}
