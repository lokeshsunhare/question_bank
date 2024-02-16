package com.igkvmis.questionbank.activities.copy_hidden_folder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.igkvmis.questionbank.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyHiddenFilesActivity extends AppCompatActivity {

    private static final String TAG = "CopyHiddenFilesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_hidden_files);


    }

    public void download(View view) {
        File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses/3257aa64c1754d6aba3c7d5ab7703c08.mp4");
        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/3257aa64c1754d6aba3c7d5ab7703c08.mp4");

        Log.d(TAG, "download: " + from.getAbsolutePath());
        Log.d(TAG, "download: " + to.getAbsolutePath());
        try {
            copyFile(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while ((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
        }

        var2.close();
        var3.close();
    }
}
