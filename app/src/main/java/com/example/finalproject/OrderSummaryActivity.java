package com.example.finalproject;

import static com.example.finalproject.PaymentActivity.cardnumber;
import static com.example.finalproject.PaymentActivity.cus_Name;
import static com.example.finalproject.PaymentActivity.flag;
import static com.example.finalproject.PaymentActivity.name;
import static com.example.finalproject.PaymentActivity.op1;
import static com.example.finalproject.PaymentActivity.op2;
import static com.example.finalproject.PaymentActivity.op3;
import static com.example.finalproject.PaymentActivity.qrtxnid;
import static com.example.finalproject.PaymentActivity.upiid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    TextView totalPriceText;
    ListView orderListView;
    Button resetBtn, shareBtn,proceedtopaybtn;
    DatabaseHelper dbHelper;
    ArrayList<String> orderList;

    int totalPrice = 0;
    File pdfFile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        totalPriceText = findViewById(R.id.total_price_text);
        orderListView = findViewById(R.id.order_list_view);
        resetBtn = findViewById(R.id.reset_button);
        shareBtn = findViewById(R.id.share_button);
        proceedtopaybtn = findViewById(R.id.proceedtopaybtn);

        dbHelper = new DatabaseHelper(this);
        orderList = new ArrayList<>();

        loadOrders();

        resetBtn.setOnClickListener(v -> {
            dbHelper.clearOrders();
            orderList.clear();
            totalPriceText.setText("Total: ₹0");
            flag =0;
            op1=0;
            op2=0;
            op3=0;
            name=0;
            ((ArrayAdapter<?>) orderListView.getAdapter()).notifyDataSetChanged();
        });

        shareBtn.setOnClickListener(v -> {
            if (orderList.isEmpty()) {
                Toast.makeText(this, "Order is empty", Toast.LENGTH_SHORT).show();
            } else {
                exportOrderSummaryAsPDFAndPrint(orderList, totalPrice);
            }
        });

        proceedtopaybtn.setOnClickListener(v -> {
            Intent intent3 = new Intent(OrderSummaryActivity.this, PaymentActivity.class);
            startActivity(intent3);
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

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
//        orderListView.setAdapter(adapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_list_item,  // your layout file
                R.id.custom_text,           // the TextView in your layout
                orderList                   // your list data
        );
        orderListView.setAdapter(adapter);
    }

    private void exportOrderSummaryAsPDFAndPrint(ArrayList<String> orderList, int totalPrice) {
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


        paint.setTextSize(15);
        canvas.drawText("Total: ₹" + totalPrice, 20, y, paint);
        y += 40;

        // Add date/time/Name
        paint.setTextSize(13);
        if (name==1) {
            canvas.drawText("Customer Name : " + cus_Name, 20, y, paint);
            y += 30;
        }
        paint.setFakeBoldText(false);
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Date: " + timestamp, 20, y, paint);
        y += 40;

        for (String order : orderList) {
            canvas.drawText(order, 20, y, paint);
            y += 25;
        }

        y += 20;
        paint.setTextSize(15);
        paint.setFakeBoldText(true);
        if(flag==1){
            canvas.drawText("Bill Status : Paid ", 20, y, paint);
        }
        else {
            canvas.drawText("Bill Status : Un-Paid", 20, y, paint);
        }
        y += 40;

        if (op1 == 1) {
            canvas.drawText("Payment Done Through : UPI ", 20, y, paint);
            y += 30;
            canvas.drawText("With UPI id : "+upiid, 20, y, paint);
        }
        if (op2 == 1) {
            canvas.drawText("Payment Done Through :Credit/Debit Card ", 20, y, paint);
            y += 30;
            canvas.drawText("With Card Number : "+cardnumber, 20, y, paint);
        }
        if (op3 == 1) {
            canvas.drawText("Payment Done Through : QR code ", 20, y, paint);
            y += 30;
            canvas.drawText("With Transition ID : "+qrtxnid, 20, y, paint);
        }

        pdfDocument.finishPage(page);

        // Save to file
        pdfFile = new File(getExternalFilesDir(null), "OrderSummary.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
        } catch (IOException e) {
            Toast.makeText(this, "PDF creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Print the file
        printPDF(pdfFile);
    }

    private void printPDF(File file) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                                 CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("OrderSummary.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build();

                callback.onLayoutFinished(pdi, true);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                                CancellationSignal cancellationSignal, WriteResultCallback callback) {
                try (FileInputStream fis = new FileInputStream(file);
                     FileOutputStream fos = new FileOutputStream(destination.getFileDescriptor())) {
                    byte[] buf = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buf)) > 0) {
                        fos.write(buf, 0, bytesRead);
                    }
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } catch (IOException e) {
                    callback.onWriteFailed(e.toString());
                }
            }
        };

        printManager.print("Order Summary", printAdapter, null);
    }
}
