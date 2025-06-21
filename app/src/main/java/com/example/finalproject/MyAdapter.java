package com.example.finalproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends ArrayAdapter<String> {

    private String[] arr;
    private int[] imageArr;
    private int[] prices;
    private int[] quantities;

    public MyAdapter(Context context, int resource, String[] arr, int[] imageArr, int[] prices) {
        super(context, resource, arr);
        this.arr = arr;
        this.imageArr = imageArr;
        this.prices = prices;
        this.quantities = new int[arr.length];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_layout, parent, false);

        ImageView img = convertView.findViewById(R.id.img1);
        TextView t = convertView.findViewById(R.id.text1);
        TextView p = convertView.findViewById(R.id.text3);
        Button btn = convertView.findViewById(R.id.addbtn);
        TextView QuantityNum = convertView.findViewById(R.id.QuantityNum);

        p.setText(prices[position] + "₹");
        t.setText(arr[position]);
        img.setImageResource(imageArr[position]);

        btn.setOnClickListener(v -> {
            quantities[position]++;
            QuantityNum.setText(String.valueOf(quantities[position]));

            String name = arr[position];
            int price = prices[position];
            int qty = quantities[position];

            DatabaseHelper dbHelper = new DatabaseHelper(getContext()); // ✅ Corrected constructor
            dbHelper.insertOrUpdateDish(name, price);

            Toast.makeText(getContext(), name + " added (x" + qty + ")", Toast.LENGTH_SHORT).show();
            Log.d("DB_INSERT", name + " x" + qty);
        });

        return convertView;
    }
}
