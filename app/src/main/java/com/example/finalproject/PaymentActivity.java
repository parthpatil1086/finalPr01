package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    RadioGroup radioGroupPayment;
    LinearLayout upiLayout, cardLayout, qrLayout;
    EditText etUpiId, etCardNumber, etCardExpiry, etCardCvv, etQrTxnId,cName;
    Button btnDone;
    ImageView qrImage;
    public static String upiid,cardnumber,qrtxnid,cus_Name;
    public static int flag = 0,op1=0,op2=0,op3=0,name=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cName = findViewById(R.id.cName);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        upiLayout = findViewById(R.id.upiLayout);
        cardLayout = findViewById(R.id.cardLayout);
        qrLayout = findViewById(R.id.qrLayout);
        etUpiId = findViewById(R.id.etUpiId);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardExpiry = findViewById(R.id.etCardExpiry);
        etCardCvv = findViewById(R.id.etCardCvv);
        etQrTxnId = findViewById(R.id.etQrTxnId);
        qrImage = findViewById(R.id.qrImage);
        btnDone = findViewById(R.id.btnDone);

        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            upiLayout.setVisibility(View.GONE);
            cardLayout.setVisibility(View.GONE);
            qrLayout.setVisibility(View.GONE);

            if (checkedId == R.id.radioUpi) {
                upiLayout.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioCard) {
                cardLayout.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioQr) {
                qrLayout.setVisibility(View.VISIBLE);
            }
        });

        btnDone.setOnClickListener(v -> {
            int selectedId = radioGroupPayment.getCheckedRadioButtonId();
            cus_Name = cName.getText().toString().trim();
            if(!cus_Name.isEmpty()){
                name =1;
            }
            if (selectedId == R.id.radioUpi) {
                String upiId = etUpiId.getText().toString().trim();
                upiid = etUpiId.getText().toString().trim();
                if (upiId.isEmpty()) {
                    etUpiId.setError("Enter UPI ID");
                    return;
                }
                proceedToNext("UPI");

            } else if (selectedId == R.id.radioCard) {
                String cardNum = etCardNumber.getText().toString().trim();
                cardnumber = etCardNumber.getText().toString().trim();
                String expiry = etCardExpiry.getText().toString().trim();
                String cvv = etCardCvv.getText().toString().trim();
                if (cardNum.length() != 16) {
                    etCardNumber.setError("Enter 16-digit card number");
                    return;
                }
                if (expiry.isEmpty()) {
                    etCardExpiry.setError("Enter expiry date");
                    return;
                }
                if (cvv.length() != 3) {
                    etCardCvv.setError("Enter 3-digit CVV");
                    return;
                }
                proceedToNext("Card");

            } else if (selectedId == R.id.radioQr) {
                String txnId = etQrTxnId.getText().toString().trim();
                qrtxnid = etQrTxnId.getText().toString().trim();
                if (txnId.isEmpty()) {
                    etQrTxnId.setError("Enter transaction ID");
                    return;
                }
                proceedToNext("QR Code");
            }
            else if (cus_Name.isEmpty()){
                cName.setError("Enter Customer Name");
            } else {
                Toast.makeText(this, "Select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void proceedToNext(String method) {
        if(method.equals("UPI")){
            op1 = 1;
        }
        if(method.equals("Card")){
            op2 = 1;
        }
        if(method.equals("QR Code")){
            op3 = 1;
        }
        Toast.makeText(this, "Payment successful via " + method, Toast.LENGTH_SHORT).show();
        flag = 1;
//            Intent intent = new Intent(this, OrderSummaryActivity.class); // Change if needed
//            startActivity(intent);
            finish();
    }
}