package com.e.qrdolgozat_babusane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textResult;
    private Button btnScan, btnKiir;

    private boolean writePermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QR CODE SCAN");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }

            });


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            textResult.setText("Nincs jogosultsága a filbeíráshoz");
            String[] permissions =
                    {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 0);

            return;
        }
        textResult.setText("Engedély megadva");


    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 0) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {

                    writePermissionGranted = true;
                } else {
                    writePermissionGranted = false;
                }
            }
                }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show();
        } else {

            textResult.setText("QRCode eredmény: " + result.getContents());

            if (writePermissionGranted) {
                try {
                    Naplozas.kiir(textResult.toString());
                } catch (IOException e) {
                    Log.d("kiirasi hiba", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        try {
            Uri url = Uri.parse(result.getContents());
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intent);
        } catch (Exception exception) {
            Log.d("URI ERROR", exception.toString());
        }

        super.onActivityResult(requestCode, resultCode, data);




       btnKiir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            textResult.setText("QRCode eredmény: " + result.getContents());

            if (writePermissionGranted) {
                try {
                    Naplozas.kiir(textResult.toString());
                } catch (IOException e) {
                    Log.d("kiirasi hiba", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    });
    }

    private void init() {
        btnKiir = findViewById(R.id.Button_kiir);
        btnScan = findViewById(R.id.Button_scan);
        textResult = findViewById(R.id.textViewEredmeny);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

            writePermissionGranted = true;
        } else {
            writePermissionGranted = false;
            String[] permissions =
                    {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }



    }


