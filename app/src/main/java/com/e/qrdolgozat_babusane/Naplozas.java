package com.e.qrdolgozat_babusane;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Naplozas {

    // csv fileba írja a textview tartalmát
    public static void kiir(String textResult) throws IOException {
        Date datum = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formazottDatum = dateFormat.format(datum);

        String content = String.format("%s,%s", textResult,formazottDatum);
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), "QRcodedogaText.csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(content);
            bw.append(System.lineSeparator());
            bw.close();
        }

    }
}
