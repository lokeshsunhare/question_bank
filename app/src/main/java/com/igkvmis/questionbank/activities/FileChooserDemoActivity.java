package com.igkvmis.questionbank.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.activities.file_chooser.FileChooserActivity;
import com.igkvmis.questionbank.activities.file_chooser.utils.FileUtils;
import com.igkvmis.questionbank.common.NetworkCheckActivity;
import com.squareup.picasso.Downloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileChooserDemoActivity extends AppCompatActivity {
    private TextView tvfilePath;
    private Button btn_picFile;
    private Button btn_upload_file;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "FileChooserDemoActivity";

    private static final int REQUEST_CODE = 6384;


    // from here upload file

    URL connectURL;
    FileInputStream fileInputStream = null;
    private String str_file_name;
    private String emp_id = "123";
    private String File_Size;
    private String File_path;
    private long mLastClickTime = 0;
    public String POST_TIME = null;
    private static final int IMAGE_REQUEST = 100;

    // capture image
    private File imageFile = null;
    private String currentImagePath = null;
    private long fileLength = 0;
    private File filesForDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser_demo);
        getRuntimePermissions();
        btn_picFile = findViewById(R.id.btn_picFile);
        btn_upload_file = findViewById(R.id.btn_upload_file);
        tvfilePath = findViewById(R.id.tvfilePath);
        btn_picFile.setOnClickListener(v -> showChooser());
        findViewById(R.id.btn_capture).setOnClickListener(v -> captureImage());
        btn_upload_file.setOnClickListener(v -> validateField());
        setTextPathClear();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".profileimage.fileprovider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, IMAGE_REQUEST);
            }

        }

    }

    private File getImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void validateField() {
        boolean valid = true;
        if (tvfilePath.getText().toString().trim().isEmpty()) {
            valid = false;
            Toast.makeText(this, "Please select file to upload database", Toast.LENGTH_SHORT).show();
        }
        if (valid) {
            if (NetworkCheckActivity.isNetworkAvailable(FileChooserDemoActivity.this)) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                POST_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                saveFile(File_path);
            } else {
                Toast toast = Toast.makeText(FileChooserDemoActivity.this, "No internet connection", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }
    }

    private boolean getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) ||

                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        0);
                return false;
            }
        }
        return true;
    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);

                            File file = FileUtils.getFile(this, uri);

                            Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            String filename = returnCursor.getString(nameIndex);
                            String extension = FileUtils.getExtension(path);
                            int size = returnCursor.getInt(sizeIndex);
                            Log.d(TAG, "onActivityResult: p " + path);
                            Log.d(TAG, "onActivityResult: s " + FileUtils.getReadableFileSize(size));
                            Log.d(TAG, "onActivityResult: e " + extension);
                            fileLength = size;
                            File_path = path;
                            filesForDownload = file;

                            str_file_name = FileUtils.getFileName(this, uri);
                            File_Size = FileUtils.getReadableFileSize(size);
                            tvfilePath.setText(filename);
                            checkVisibilityUploadButton();
                            tvfilePath.setOnClickListener(v -> {
                                //openFile(file);
                                try {
                                    Intent t = FileUtils.getViewIntent(this, file);
                                    startActivity(t);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                }
                break;

            case IMAGE_REQUEST:

                if (resultCode == RESULT_OK) {

                    Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
                    //image_view.setImageBitmap(bitmap);
                    File file = new File(currentImagePath);
                    File_path = currentImagePath;
                    str_file_name = file.getName();
                    fileLength = file.length();
                    File_Size = FileUtils.getReadableFileSize(Integer.parseInt(String.valueOf(file.length())));
                    tvfilePath.setText(str_file_name);
                    checkVisibilityUploadButton();
                    tvfilePath.setOnClickListener(v -> {
                        //openFile(file);
                        try {
                            Intent t = FileUtils.getViewIntent(this, file);
                            startActivity(t);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    try {
//                        Bitmap bitmapOrg = createOriginalBitmap(currentImagePath);
//                        bitmapOrg = rotateImage(currentImagePath, bitmapOrg);
//                        final Bitmap finalBitmap = resizeBitmap(bitmapOrg);
//
//                        FileOutputStream out = null;
//                        try {
//                            out = new FileOutputStream(imageFile);
//                            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        } catch (final Exception e) {
//                        } finally {
//                            try {
//                                if (out != null) {
//                                    out.close();
//                                }
//                            } catch (final IOException e) {
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        return;
//                    }
                }

//            image_view.setDrawingCacheEnabled(true);
//            final_bitmap = image_view.getDrawingCache();

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setTextPathClear() {
        tvfilePath.setText("");
        checkVisibilityUploadButton();
    }

    public void downloadFiles(View view) {
        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + tvfilePath.getText().toString().trim());
        Log.d(TAG, "download: " + filesForDownload.getAbsolutePath());
        Log.d(TAG, "download: " + to.getAbsolutePath());
        try {
            copyFile(filesForDownload, to);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void checkVisibilityUploadButton() {
        if (tvfilePath.getText().toString().trim().isEmpty())
            btn_upload_file.setVisibility(View.GONE);
        else
            btn_upload_file.setVisibility(View.VISIBLE);
    }


    private static Bitmap rotateImage(final String imagePath, Bitmap source) throws IOException {
        final ExifInterface ei = new ExifInterface(imagePath);
        final int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                source = rotateImageByAngle(source, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                source = rotateImageByAngle(source, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                source = rotateImageByAngle(source, 270);
                break;
        }
        return source;
    }

    private Bitmap createOriginalBitmap(final String imagePath) {
        final Bitmap bitmapOrg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapOrg = BitmapFactory.decodeFile(imagePath);
        } else {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            bitmapOrg = BitmapFactory.decodeFile(imagePath, options);
        }
        return bitmapOrg;
    }

    public static Bitmap rotateImageByAngle(final Bitmap source, final float angle) {
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap resizeBitmap(Bitmap source) {
        final int heightOrg = source.getHeight();
        final int heightNew = 800;
        if (heightNew < heightOrg) {
            final int widthOrg = source.getWidth();
            final int widthNew = (heightNew * widthOrg) / heightOrg;

            final Matrix matrix = new Matrix();
            matrix.postScale(((float) widthNew) / widthOrg, ((float) heightNew) / heightOrg);
            source = Bitmap.createBitmap(source, 0, 0, widthOrg, heightOrg, matrix, false);
        }
        return source;
    }

//    private void openFile(File url) {
//
//        try {
//
//            Uri uri = Uri.fromFile(url);
////            Uri uri;
////            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
////                uri = Uri.fromFile(url);
////            } else {
////                uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".profileimage.fileprovider", url);
////            }
//            Log.d(TAG, "openFile: " + uri);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
//                // Word document
//                intent.setDataAndType(uri, "application/msword");
//            } else if (url.toString().contains(".pdf")) {
//                // PDF file
//                intent.setDataAndType(uri, "application/pdf");
//            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
//                // Powerpoint file
//                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
//                // Excel file
//                intent.setDataAndType(uri, "application/vnd.ms-excel");
//            } else if (url.toString().contains(".zip")) {
//                // ZIP file
//                intent.setDataAndType(uri, "application/zip");
//            } else if (url.toString().contains(".rar")) {
//                // RAR file
//                intent.setDataAndType(uri, "application/x-rar-compressed");
//            } else if (url.toString().contains(".rtf")) {
//                // RTF file
//                intent.setDataAndType(uri, "application/rtf");
//            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
//                // WAV audio file
//                intent.setDataAndType(uri, "audio/x-wav");
//            } else if (url.toString().contains(".gif")) {
//                // GIF file
//                intent.setDataAndType(uri, "image/gif");
//            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
//                // JPG file
//                intent.setDataAndType(uri, "image/jpeg");
//            } else if (url.toString().contains(".txt")) {
//                // Text file
//                intent.setDataAndType(uri, "text/plain");
//            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
//                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
//                // Video files
//                intent.setDataAndType(uri, "video/*");
//            } else {
//                intent.setDataAndType(uri, "*/*");
//            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void saveFile(String path) {

        try {
            connectURL = new URL("http://192.168.1.29/DSSNew/webservices/MISAdmin.asmx/saveFileDemo");
            fileInputStream = new FileInputStream(path);

        } catch (
                Exception ex) {
            Log.i("HttpFileUpload", "URL Malformatted");
        }

        new AsyncTaskUploadAssignmentFile().execute();
    }


    class AsyncTaskUploadAssignmentFile extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = new ProgressDialog(FileChooserDemoActivity.this);
        int taskNumber;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String s = "";
            try {
                String iFileName = "temp_vid.pdf";
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String Tag = "fSnd";

                Log.e(Tag, "Starting Http File Sending to URL");

                // Open a HTTP connection to the URL
                HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

                // Allow Inputs
                conn.setDoInput(true);

                // Allow Outputs
                conn.setDoOutput(true);

                // Don't use a cached copy.
                conn.setUseCaches(false);

                // Use a post method.
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"emp_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(emp_id);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"File_Size\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(File_Size);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"file_name\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(str_file_name);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + iFileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                if (fileInputStream != null) {
                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();

                    int maxBufferSize = 32768;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...

                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    int totalByteRead = 0;
                    progressDialog.setIndeterminate(false);
                    while (bytesRead > 0) {

                        totalByteRead += bytesRead;
                        Log.w(TAG, "totalByteRead: " + totalByteRead + ", totalSize: " + fileLength);
                        publishProgress((int) ((totalByteRead / (float) fileLength) * 100));

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    dos.flush();

                    Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

                    fileInputStream.close();
                }
                InputStream is = conn.getInputStream();
                // retrieve the response from server
                int ch;
                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    if (isCancelled()) {
                        is.close();
                        return null;
                    }
                    b.append((char) ch);

                }
                s = b.toString();
                //Log.i("Response", s);
                dos.close();
                // close streams

            } catch (MalformedURLException ex) {
                Log.e(TAG, "URL error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
            }

            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: " + result);
            try {
                JSONObject jsonobj = new JSONObject(result);
                String Success = "0";
                JSONObject res = jsonobj.getJSONObject("SaveQueryResult");
                String message = res.getString("Message");
                Success = res.getString("Success");
                Log.d(TAG, "Success : " + Success);
                if (Success.equals("1")) {
                    Toast.makeText(FileChooserDemoActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FileChooserDemoActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                progressDialog.setMax(100);
                progressDialog.setProgress(100);
                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("    catch  JSONException   ");
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: " + values[0]);
            progressDialog.setProgress(values[0]);
        }
    }

    public void copyFile(File src, File dst) throws IOException {

        ProgressDialog progressDialog = new ProgressDialog(FileChooserDemoActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        int totalByteRead = 0;
        progressDialog.setIndeterminate(false);
        while ((var5 = var2.read(var4)) > 0) {
            totalByteRead += totalByteRead;
            Log.w(TAG, "totalByteRead: " + totalByteRead + ", totalSize: " + src.length());
            progressDialog.setProgress((int) ((totalByteRead / (float) src.length()) * 100));
            var3.write(var4, 0, var5);
        }
        var2.close();
        var3.close();
        progressDialog.setMax(100);
        progressDialog.dismiss();
    }


}
