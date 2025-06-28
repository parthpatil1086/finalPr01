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

    private final String[] arr;
    private final int[] imageArr;
    private final int[] prices;
    private final int[] quantities;
    public static Button btn;
    public MyAdapter(Context context, int resource, String[] arr, int[] imageArr, int[] prices) {
        super(context, resource, arr);
        this.arr = arr;
        this.imageArr = imageArr;
        this.prices = prices;
        this.quantities = new int[arr.length];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate layout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_layout, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.img1);
        TextView t = convertView.findViewById(R.id.text1);
        TextView p = convertView.findViewById(R.id.text3);
        btn = convertView.findViewById(R.id.addbtn);
        TextView QuantityNum = convertView.findViewById(R.id.QuantityNum);

        // Set values
        t.setText(arr[position]);
        p.setText(prices[position] + "₹");
        img.setImageResource(imageArr[position]);
        QuantityNum.setText(String.valueOf(quantities[position]));

        btn.setOnClickListener(v -> {
            quantities[position]++;
            QuantityNum.setText(String.valueOf(quantities[position]));

            String name = arr[position];
            int price = prices[position];

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.insertOrUpdateDish(name, price);

            Toast.makeText(getContext(), name + " added (x" + quantities[position] + ")", Toast.LENGTH_SHORT).show();
            Log.d("DB_INSERT", name + " x" + quantities[position]);
        });
        return convertView;
    }
}
