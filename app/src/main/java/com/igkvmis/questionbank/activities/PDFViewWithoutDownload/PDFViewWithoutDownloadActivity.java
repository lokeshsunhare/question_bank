package com.igkvmis.questionbank.activities.PDFViewWithoutDownload;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.common.util.IOUtils;
import com.igkvmis.questionbank.R;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PDFViewWithoutDownloadActivity extends AppCompatActivity implements OnPageChangeListener,
        OnLoadCompleteListener,
        OnPageErrorListener {
    private int pageNumber = 0;

    private String pdfFileName;
    private PDFView pdfView;
    private static final String TAG = "PDFViewWithoutDownloadA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview_without_download);
        pdfView = findViewById(R.id.pdfView);
        getRuntimePermissions();
        findViewById(R.id.btnGetPDF).setOnClickListener(v -> {

            new getPDFWithoutDownLoadUsingInputStream().execute("http://192.168.42.224/__Files/eKrishiPathShala/StudyAssignmentFile/2020_21/Uploaded/1040_520_20200803221954296_1040_519_20200714161106079_News_2020_06_02_05_38_15.pdf");
//            new getPDFWithoutDownLoadUsingByteArray().execute("http://192.168.1.29/_StudyAssignmentFile/Uploaded//1040_540_20200714135438608_billingual_advertisement.pdf");

        });
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

    private class getPDFWithoutDownLoadUsingInputStream extends AsyncTask<String, Void, InputStream> {
        ProgressDialog progressDialog = new ProgressDialog(PDFViewWithoutDownloadActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected InputStream doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");

            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (Exception e) {
                return null;

            }
            return inputStream;

        }

        protected void onPostExecute(InputStream inputStream) {

            pdfView.fromStream(inputStream)
                    .defaultPage(pageNumber)
                    .onPageChange(PDFViewWithoutDownloadActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(PDFViewWithoutDownloadActivity.this)
                    .scrollHandle(new DefaultScrollHandle(PDFViewWithoutDownloadActivity.this))
                    .spacing(10) // in dp
                    .onPageError(PDFViewWithoutDownloadActivity.this)
                    .load();

            progressDialog.dismiss();
        }
    }

    private class getPDFWithoutDownLoadUsingByteArray extends AsyncTask<String, Void, byte[]> {
        ProgressDialog progressDialog = new ProgressDialog(PDFViewWithoutDownloadActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected byte[] doInBackground(String... strings) {

            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (Exception e) {
                return null;

            }
            try {
                return IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(byte[] inputStream) {

            pdfView.fromBytes(inputStream)
                    .defaultPage(pageNumber)
                    .onPageChange(PDFViewWithoutDownloadActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(PDFViewWithoutDownloadActivity.this)
                    .scrollHandle(new DefaultScrollHandle(PDFViewWithoutDownloadActivity.this))
                    .spacing(10) // in dp
                    .onPageError(PDFViewWithoutDownloadActivity.this)
                    .load();

            progressDialog.dismiss();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

}
