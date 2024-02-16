package com.igkvmis.questionbank.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.igkvmis.questionbank.R;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class FilePRDownloadActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    private String pdfFileName;
    private PDFView pdfView;
    public ProgressDialog pDialog;
    private int pageNumber = 0;
    TextView textViewProgressOne, buttonCancelOne;
    ProgressBar progressBarOne;
    Button buttonOne;
    Button view_file;
    int downloadIdOne;
    private static final String TAG = "FilePRDownloadActivity";
    private RelativeLayout layoutButton;
    private String root = Environment.getExternalStorageDirectory().toString() + "/" + "Download" + "/" + "PR_Download" + "/";

    private String URL = "http://192.168.1.29/_StudyAssignmentFile/Uploaded/1040_519_20200714161106079_News_2020_06_02_05_38_15.pdf";
    private String FileName = URL.
            substring(URL.lastIndexOf("/") + 1, URL.toString().length());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_prdownload);
        pdfView = findViewById(R.id.pdfView);

        textViewProgressOne = findViewById(R.id.textViewProgressOne);
        buttonCancelOne = findViewById(R.id.buttonCancelOne);
        buttonOne = findViewById(R.id.buttonOne);
        progressBarOne = findViewById(R.id.progressBarOne);
        layoutButton = findViewById(R.id.layoutButton);


        getRuntimePermissions();
        Button btn_picFile = findViewById(R.id.btn_picFile);
        btn_picFile.setOnClickListener(v -> {
            new getPDFWithoutDownLoadUsingInputStream().execute(URL);
        });
        view_file = findViewById(R.id.view_file);
        view_file.setOnClickListener(v -> {
            viewOfflineFile();
        });
        listener();
        checkExistsFile();

    }

    private void checkExistsFile() {
        File pdfFile = new File(root + FileName);
        if (pdfFile.exists()) {
            view_file.setVisibility(View.VISIBLE);
            layoutButton.setVisibility(View.GONE);
        } else {
            view_file.setVisibility(View.GONE);
            layoutButton.setVisibility(View.VISIBLE);
        }
    }

    private String getFilePath() {
        final File dir = new File(root);
        if (!dir.exists())
            dir.mkdirs();
        Log.d(TAG, "getFilePath1: " + dir.getPath());
        return dir.getPath();
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
//        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
        return getReadableFileSize(currentBytes) + "/" + getReadableFileSize(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    public static String getReadableFileSize(long size) {
        final double BYTES_IN_KILOBYTES = 1024.0;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        double fileSize = 0.0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    private void listener() {
        buttonOne.setOnClickListener(view -> {

            if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                PRDownloader.pause(downloadIdOne);
                return;
            }

            buttonOne.setEnabled(false);
            progressBarOne.setIndeterminate(true);
            progressBarOne.getIndeterminateDrawable().setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

            if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                PRDownloader.resume(downloadIdOne);
                return;
            }
            downloadIdOne = PRDownloader.download(URL, getFilePath(), FileName)
                    .build()
                    .setOnStartOrResumeListener(() -> {
                        progressBarOne.setIndeterminate(false);
                        buttonOne.setEnabled(true);
                        buttonOne.setText("Pause");
                        buttonCancelOne.setEnabled(true);
                    })
                    .setOnPauseListener(() -> buttonOne.setText("Resume"))
                    .setOnCancelListener(() -> {
                        buttonOne.setText("Start");
                        buttonCancelOne.setEnabled(false);
                        progressBarOne.setProgress(0);
                        textViewProgressOne.setText("");
                        downloadIdOne = 0;
                        progressBarOne.setIndeterminate(false);
                    })
                    .setOnProgressListener(progress -> {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        progressBarOne.setProgress((int) progressPercent);
                        textViewProgressOne.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                        progressBarOne.setIndeterminate(false);
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            buttonOne.setEnabled(false);
                            buttonCancelOne.setEnabled(false);
                            buttonOne.setText("Completed");
                            checkExistsFile();
                        }

                        @Override
                        public void onError(Error error) {
                            buttonOne.setText("Start");
                            Toast.makeText(getApplicationContext(), "some_error_occurred" + " " + "1", Toast.LENGTH_SHORT).show();
                            textViewProgressOne.setText("");
                            progressBarOne.setProgress(0);
                            downloadIdOne = 0;
                            buttonCancelOne.setEnabled(false);
                            progressBarOne.setIndeterminate(false);
                            buttonOne.setEnabled(true);
                        }
                    });

        });

        buttonCancelOne.setOnClickListener(view -> PRDownloader.cancel(downloadIdOne));
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
        ProgressDialog progressDialog = new ProgressDialog(FilePRDownloadActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected InputStream doInBackground(String... strings) {

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
                    .onPageChange(FilePRDownloadActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(FilePRDownloadActivity.this)
                    .scrollHandle(new DefaultScrollHandle(FilePRDownloadActivity.this))
                    .spacing(10) // in dp
                    .onPageError(FilePRDownloadActivity.this)
                    .load();

            progressDialog.dismiss();
        }
    }

//    private void displayFromFile(File file) {
//
//        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
//        pdfFileName = getFileName(uri);
//
//        pdfView.fromFile(file)
//                .defaultPage(pageNumber)
//                .onPageChange(this)
//                .enableAnnotationRendering(true)
//                .onLoad(this)
//                .scrollHandle(new DefaultScrollHandle(this))
//                .spacing(10) // in dp
//                .onPageError(this)
//                .load();
//    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
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

    //called to show file from mobile storage
    public void viewOfflineFile() { //file path is for syllabus
        File pdfFile = new File(root + FileName);
        if (pdfFile.exists()) {
            Log.d(TAG, "viewDownloadedFile : " + pdfFile.getAbsolutePath());//Uri path = Uri.fromFile(pdfFile);
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".profileimage.fileprovider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = pdfFile.getAbsolutePath().toString();
            if (url.contains(".doc") || url.contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.contains(".ppt") || url.contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.contains(".xls") || url.contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.contains(".zip") || url.contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.contains(".wav") || url.contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") ||
                    url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }
        } else {

        }

    }

}
