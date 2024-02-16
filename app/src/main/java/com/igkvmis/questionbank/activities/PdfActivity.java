package com.igkvmis.questionbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.common.util.IOUtils;
import com.igkvmis.questionbank.BuildConfig;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.activities.file_chooser.utils.ConvertUriToFilePath;
import com.igkvmis.questionbank.activities.file_chooser.utils.FileUtils;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
    private int pageNumber = 0;

    private String pdfFileName;
    private PDFView pdfView;
    public ProgressDialog pDialog;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    private Button btn_picFile;
    private static final String TAG = "PdfActivity";
    private String filePath;


    private static final int REQUEST_PICK_FILE = 2;

    private TextView tvfilePath;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);

        getRuntimePermissions();
        pdfView = (PDFView) findViewById(R.id.pdfView);
        btn_picFile = findViewById(R.id.btn_picFile);
        tvfilePath = findViewById(R.id.tvfilePath);
        Button btn_upload = findViewById(R.id.btn_submit);
        btn_picFile.setOnClickListener(v -> {

//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            try {
//                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
//            } catch (Exception ex) {
//                System.out.println("browseClick :" + ex);
//            }

//            Intent intent = new Intent(this, FilePicker.class);
//            startActivityForResult(intent, REQUEST_PICK_FILE);

            //launchPicker();

            new getPDFWithoutDownLoadUsingInputStream().execute("http://192.168.1.29/_StudyAssignmentFile/Uploaded/1040_519_20200714161106079_News_2020_06_02_05_38_15.pdf");
//            new getPDFWithoutDownLoadUsingByteArray().execute("http://192.168.1.29/_StudyAssignmentFile/Uploaded//1040_540_20200714135438608_billingual_advertisement.pdf");

            //openFile(file);
        });

        btn_upload.setOnClickListener(v -> UploadFile(filePath));
        initDialog();
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

    private void openFile(File url) {

        try {

//            Uri uri = Uri.fromFile(url);
            Uri uri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                uri = Uri.fromFile(url);
            } else {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".profileimage.fileprovider", url);
            }
            Log.d(TAG, "openFile: " + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

//    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
//        protected Long doInBackground(URL... urls) {
//            int count = urls.length;
//            long totalSize = 0;
//            for (int i = 0; i < count; i++) {
//                totalSize += Downloader.downloadFile(urls[i]);
//                publishProgress((int) ((i / (float) count) * 100));
//                // Escape early if cancel() is called
//                if (isCancelled()) break;
//            }
//            return totalSize;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
//        }
//
//        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
//        }
//    }

    private class getPDFWithoutDownLoadUsingInputStream extends AsyncTask<String, Void, InputStream> {
        ProgressDialog progressDialog = new ProgressDialog(PdfActivity.this);

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
                    .onPageChange(PdfActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(PdfActivity.this)
                    .scrollHandle(new DefaultScrollHandle(PdfActivity.this))
                    .spacing(10) // in dp
                    .onPageError(PdfActivity.this)
                    .load();

            progressDialog.dismiss();
        }
    }


    private class getPDFWithoutDownLoadUsingByteArray extends AsyncTask<String, Void, byte[]> {
        ProgressDialog progressDialog = new ProgressDialog(PdfActivity.this);

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
                    .onPageChange(PdfActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(PdfActivity.this)
                    .scrollHandle(new DefaultScrollHandle(PdfActivity.this))
                    .spacing(10) // in dp
                    .onPageError(PdfActivity.this)
                    .load();

            progressDialog.dismiss();
        }
    }


    public void UploadFile(String path) {
        try {
            // Set your file path here

//            Log.d(TAG, "UploadFile: " + Environment.getExternalStorageDirectory().toString() + "/Download/4- SYLLABUS- PWDA20.pdf");
//            Log.d(TAG, "UploadFile1: " + filePath);

            FileInputStream fstrm = new FileInputStream(path);
            // Set your server page url (and the file title/description)
            File file = new File(path);
            String file_name = file.getName();

            HttpFileUpload hfu = new HttpFileUpload(this,
                    "http://192.168.1.29/DSSNew/webservices/MISAdmin.asmx/UploadFile",
                    file_name, "1204");

            hfu.Send_Now(fstrm);

        } catch (FileNotFoundException e) {
            // Error: File not found
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pickFile:
                launchPicker();
                return true;
            case R.id.upload:
                // uploadFile();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void launchPicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.*$"))
                .withTitle("Select PDF file")
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK) {

//            if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
//                selectedFile = new File
//                        (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
//                tvfilePath.setText(selectedFile.getPath());
//                Log.d(TAG, "onActivityResult: " + selectedFile);
//                Log.d(TAG, "onActivityResult1: " + selectedFile.getPath());
//                tvfilePath.setOnClickListener(v -> {
//                    openFile(selectedFile);
//                });
//            }
            final Uri uri = data.getData();
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                //tvfilePath.setText(path);
                File file = FileUtils.getFile(this, uri);

                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String filename = returnCursor.getString(nameIndex);
                int size = returnCursor.getInt(sizeIndex);
                Log.d(TAG, "onActivityResult: f " + filename);
                Log.d(TAG, "onActivityResult: s " + FileUtils.getReadableFileSize(size));

                openPath(uri);
                String actualFilepath = ConvertUriToFilePath.getPathFromURI(this, uri);

                tvfilePath.setText(actualFilepath);
                tvfilePath.setOnClickListener(v -> {
                    openFile(new File(actualFilepath));
                });
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            File file = new File(path);
            displayFromFile(file);
            if (path != null) {
                Log.d(TAG, "Path: " + path);
                filePath = path;
                int file_size = Integer.parseInt(String.valueOf(file.length() / (1024 * 1024)));
                Log.d(TAG, "file_size: " + file_size);

                //  pdfPath = path;
                // Toast.makeText(this, "Picked file: " + path, Toast.LENGTH_LONG).show();
            }

        }

    }

    public void openPath(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            //Convert your stream to data here
            if (is != null) {
                is.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilePathFromURI(Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String TEMP_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
            File copyFile = new File(TEMP_DIR_PATH + File.separator + fileName);
            Log.d("DREG", "FilePath copyFile: " + copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        pdfFileName = getFileName(uri);

        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

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

//    private void uploadFile() {
//        if (pdfPath == null) {
//            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
//            return;
//        } else {
//            showpDialog();
//
//            // Map is used to multipart the file using okhttp3.RequestBody
//            Map<String, RequestBody> map = new HashMap<>();
//            File file = new File(pdfPath);
//            // Parsing any Media type file
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
//            map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
//            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
//            Call<ServerResponse> call = getResponse.upload("token", map);
//            call.enqueue(new Callback<ServerResponse>() {
//                @Override
//                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body() != null) {
//                            hidepDialog();
//                            ServerResponse serverResponse = response.body();
//                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    } else {
//                        hidepDialog();
//                        Toast.makeText(getApplicationContext(), "problem image", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ServerResponse> call, Throwable t) {
//                    hidepDialog();
//                    Log.v("Response gotten is", t.getMessage());
//                    Toast.makeText(getApplicationContext(), "problem uploading image " + t.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }
//    }

    protected void initDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("loading");
        pDialog.setCancelable(true);
    }


    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

}