package com.e.qrdolgozat_babusane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView textResult;
    private Button btnScan, btnKiir, btnkoordinata;
    private ImageView ivqrkoord;

    private double longitude;
    private double latitude;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Timer timer;

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

     /*   if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions =
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 0);
         /*   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);*/
         /*   return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
    }*/

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
    protected void onResume() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(TimerTick);
            }

        };
        timer.schedule(task, 1000, 5000);
        super.onResume();
    }
    private Runnable TimerTick = new Runnable() {
        @Override
        public void run() {
            // Java String.format °%f -  tizedes pontos szám, tört szám, %d egész szám, %s szöveg

            if (writePermissionGranted) {
                try {
                    Naplozas.kiir(textResult.toString(),longitude, latitude);
                } catch (IOException e) {
                    Log.d("kiirasi hiba", e.getMessage());
                    e.printStackTrace();
                }
            }

        }

    };


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
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show();
        } else {

            textResult.setText("QRCode eredmény: " + result.getContents());

            if (writePermissionGranted) {
                try {
                    Naplozas.kiir(textResult.toString(),longitude,latitude);
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


       btnKiir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String szoveg = result.getContents() + String.format(getString(R.string.gps_format), longitude, latitude );
            textResult.setText(szoveg);
            textResult.setText("QRCode eredmény: " + result.getContents()  + "\n" +  "Koordináták: " + "\n" + longitude +
                    "\n" +  latitude);



            if (writePermissionGranted) {
                try {
                    Naplozas.kiir(textResult.toString(),longitude,latitude);
                } catch (IOException e) {
                    Log.d("kiirasi hiba", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    });

       /* btnkoordinata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(textResult.getText().toString(),
                            BarcodeFormat.QR_CODE, 500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = BarcodeEncoder.createBitmap(gbitMatrix);

                }catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    private void init() {
        btnKiir = findViewById(R.id.Button_kiir);
        btnScan = findViewById(R.id.Button_scan);
        textResult = findViewById(R.id.textViewEredmeny);
        btnkoordinata = findViewById(R.id.Button_koordinata);
        ivqrkoord = findViewById(R.id. iv_qrkoord);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        };

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


