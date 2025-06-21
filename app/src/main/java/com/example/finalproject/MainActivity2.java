package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    ListView listitem;
    Button endbtn;

//    String[] arr = {"paneer","rice","roti","water"};
//    int[] prices = {120,60,20,20};
//    int[] imageArr = {
//            R.drawable.image1,
//            R.drawable.rice,
//            R.drawable.roti,
//            R.drawable.water
//    };

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

        String[] arr = getResources().getStringArray(R.array.food_items);
        String[] priceArr = getResources().getStringArray(R.array.food_prices);
        String[] imageNames = getResources().getStringArray(R.array.food_images);

        // convert image string to drawable int ids
        int[] imageArr = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageArr[i] = getResources().getIdentifier(imageNames[i], "drawable", getPackageName());
        }

        // convert price strings to int
         int[] prices = new int[priceArr.length];
         for (int i = 0; i < priceArr.length; i++) {
             prices[i] = Integer.parseInt(priceArr[i]);
         }
        listitem = findViewById(R.id.listItem);
        MyAdapter ad = new MyAdapter(this,R.layout.menu_layout,arr,imageArr,prices);
        listitem.setAdapter(ad);

        endbtn=findViewById(R.id.endbtn);
        endbtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this,OrderSummaryActivity.class);
            startActivity(intent2);
        });

    }
}