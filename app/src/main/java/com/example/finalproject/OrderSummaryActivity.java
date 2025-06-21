package com.example.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    TextView totalPriceText;
    ListView orderListView;
    Button resetBtn, shareBtn;
    DatabaseHelper dbHelper;
    ArrayList<String> orderList;
    int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        totalPriceText = findViewById(R.id.total_price_text);
        orderListView = findViewById(R.id.order_list_view);
        resetBtn = findViewById(R.id.reset_button);
        shareBtn = findViewById(R.id.share_button);

        dbHelper = new DatabaseHelper(this);
        orderList = new ArrayList<>();

        loadOrders();

        resetBtn.setOnClickListener(v -> {
            dbHelper.clearOrders();
            orderList.clear();
            totalPriceText.setText("Total: ₹0");
            ((ArrayAdapter<?>) orderListView.getAdapter()).notifyDataSetChanged();
        });

        shareBtn.setOnClickListener(v -> {
            if (orderList.isEmpty()) {
                Toast.makeText(this, "Order is empty", Toast.LENGTH_SHORT).show();
            } else {
                exportOrderSummaryAsPDFAndSend(orderList, totalPrice);
            }
        });
    }

    private void loadOrders() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nameofdish, price, quantity FROM tasks", null);
        totalPrice = 0;

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int price = cursor.getInt(1);
                int quantity = cursor.getInt(2);
                int subtotal = price * quantity;
                totalPrice += subtotal;
                orderList.add(name + " x" + quantity + " - ₹" + subtotal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        totalPriceText.setText("Total: ₹" + totalPrice);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        orderListView.setAdapter(adapter);
    }

    private void exportOrderSummaryAsPDFAndSend(ArrayList<String> orderList, int totalPrice) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 60;
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText("Order Summary", 150, y, paint);
        y += 50;

        // Add date/time
        paint.setTextSize(13);
        paint.setFakeBoldText(false);
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Date: " + timestamp, 20, y, paint);
        y += 40;

        for (String order : orderList) {
            canvas.drawText(order, 20, y, paint);
            y += 30;
        }

        y += 30;
        canvas.drawText("Total: ₹" + totalPrice, 20, y, paint);
        pdfDocument.finishPage(page);

        // Save to file
        File file = new File(getExternalFilesDir(null), "OrderSummary.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
        } catch (IOException e) {
            Toast.makeText(this, "PDF creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Share
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Send PDF using"));
    }
}
