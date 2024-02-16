package com.igkvmis.questionbank.activities.UriToPdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.igkvmis.questionbank.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class UriToPdfActivity extends AppCompatActivity {

    private EditText txt1, txt2;
    private Button bt1, bt2;
    private Handler handler;
    private final int PICKFILE_RESULT_CODE = 10;
    private String btTag = "";
    private static final String TAG = "UriToPdfActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri_to_pdf);

        txt1 = (EditText) findViewById(R.id.txtfirstpdf);
        txt2 = (EditText) findViewById(R.id.txtsecondpdf);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btTag = ((Button) v).getTag().toString();
                showFileChooser();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btTag = ((Button) v).getTag().toString();
                showFileChooser();

            }
        });

        getRuntimePermissions();
    }

    public void mergePdfFiles(View view) {
        try {
            String[] srcs = {txt1.getText().toString(), txt2.getText().toString()};
            mergePdf(srcs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mergePdf(String[] srcs) {
        try {
            // Create document object
            Document document = new Document();
            // Create pdf copy object to copy current document to the output mergedresult file

            //  File file = new File(String.valueOf(Environment.getExternalStorageDirectory()));

            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Download/mergedresult.pdf");

            PdfCopy copy = new PdfCopy(document, fileOutputStream);
            // Open the document
            document.open();
            PdfReader pr;
            int n;
            for (int i = 0; i < srcs.length; i++) {
                // Create pdf reader object to read each input pdf file
                pr = new PdfReader(srcs[i].toString());
                // Get the number of pages of the pdf file
                n = pr.getNumberOfPages();
                for (int page = 1; page <= n; page++) {
                    // Import all pages from the file to PdfCopy
                    copy.addPage(copy.getImportedPage(pr, page));
                }
            }
            document.close(); // close the document

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    // Save tag of the clicked button
    // It is used to identify the button has been pressed
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savText", btTag);


    }

    @Override
    // Restore the tag
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        btTag = savedInstanceState.getString("savText");

    }

    private void showFileChooser() {
        Log.e("AA", "bttag=" + btTag);
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, "*/*");
        Intent intentChooser = Intent.createChooser(intent, "Select a file");
        startActivityForResult(intentChooser, PICKFILE_RESULT_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == PICKFILE_RESULT_CODE) {
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    if (bt1.getTag().toString().equals(btTag))
                        txt1.setText(FilePath);
                    else
                        txt2.setText(FilePath);

                }
            }
        }
    }

    private boolean getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                return false;
            }
        }
        return true;
    }


}


