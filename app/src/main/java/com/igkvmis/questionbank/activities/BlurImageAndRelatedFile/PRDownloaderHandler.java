package com.igkvmis.questionbank.activities.BlurImageAndRelatedFile;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

public class PRDownloaderHandler {

    private Context mContext;
    public static String root = Environment.getExternalStorageDirectory().toString() + "/" + "Demo_Downloads" + "/";

    TextView textViewProgressOne, buttonCancelOne;
    CircularProgressBar progressBarOne;
    TextView buttonOne;
    int downloadIdOne;
    private RelativeLayout layoutButton;
    private RelativeLayout layout_after_start;

    private static final String TAG = "PRDownloaderHandler";

    public interface OnResponseListener {
        public void onResponseListener();
    }

    public PRDownloaderHandler.OnResponseListener mListener;


    public PRDownloaderHandler(Context mContext, PRDownloaderHandler.OnResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }


    public void setAllField(TextView textViewProgressOne,
                            TextView buttonCancelOne,
                            CircularProgressBar progressBarOne,
                            TextView buttonOne,
                            RelativeLayout layoutButton,
                            RelativeLayout layout_after_start) {
        this.textViewProgressOne = textViewProgressOne;
        this.buttonCancelOne = buttonCancelOne;
        this.progressBarOne = progressBarOne;
        this.buttonOne = buttonOne;
        this.layoutButton = layoutButton;
        this.layout_after_start = layout_after_start;
    }

    public boolean checkExistsFile(String file_name) {
        boolean flag = false;
        File pdfFile = new File(root + file_name);
        if (pdfFile.exists()) {
            flag = true;
            layoutButton.setVisibility(View.GONE);
        } else {
            flag = false;
            layoutButton.setVisibility(View.VISIBLE);
        }
        return flag;
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


    public void listener(String URL, String FileName) {
        buttonOne.setOnClickListener(view -> {

            if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                PRDownloader.pause(downloadIdOne);
                return;
            }

            buttonOne.setEnabled(false);
            buttonOne.setVisibility(View.GONE);
            layout_after_start.setVisibility(View.VISIBLE);

            progressBarOne.setIndeterminateMode(true);
//            ((FilesListHolder) holder).progressBarOne.getIndeterminateDrawable().setColorFilter(
//                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
            buttonCancelOne.setEnabled(true);
            if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                PRDownloader.resume(downloadIdOne);
                return;
            }
            downloadIdOne = PRDownloader.download(URL, getFilePath(), FileName)
                    .build()
                    .setOnStartOrResumeListener(() -> {
                        progressBarOne.setIndeterminateMode(false);
                        buttonOne.setEnabled(true);
                        buttonOne.setText("Pause");
                        buttonCancelOne.setEnabled(true);
                    })
                    .setOnPauseListener(() -> buttonOne.setText("Resume"))
                    .setOnCancelListener(() -> {
                        buttonOne.setEnabled(true);
                        buttonOne.setVisibility(View.VISIBLE);
                        layout_after_start.setVisibility(View.GONE);

                        buttonOne.setText("Start");
                        buttonCancelOne.setEnabled(false);
                        progressBarOne.setProgress(0);
                        textViewProgressOne.setText("");
                        downloadIdOne = 0;
                        progressBarOne.setIndeterminateMode(false);
                    })
                    .setOnProgressListener(progress -> {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        progressBarOne.setProgress((int) progressPercent);
                        textViewProgressOne.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                        progressBarOne.setIndeterminateMode(false);
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            buttonOne.setEnabled(false);
                            buttonCancelOne.setEnabled(false);
                            buttonOne.setText("Completed");
                            checkExistsFile(FileName);
                            if (mListener != null)
                                mListener.onResponseListener();
                        }

                        @Override
                        public void onError(Error error) {
                            buttonOne.setText("Start");
                            Toast.makeText(mContext, "some_error_occurred" + " " + "1", Toast.LENGTH_SHORT).show();
                            textViewProgressOne.setText("");
                            progressBarOne.setProgress(0);
                            downloadIdOne = 0;
                            buttonCancelOne.setEnabled(false);
                            progressBarOne.setIndeterminateMode(false);
                            buttonOne.setEnabled(true);
                        }
                    });
        });

        buttonCancelOne.setOnClickListener(view -> PRDownloader.cancel(downloadIdOne));
    }

    public void viewOfflineFile(String FileName) { //file path is for syllabus
        File pdfFile = new File(root + FileName);
        if (pdfFile.exists()) {
            Log.d(TAG, "viewDownloadedFile : " + pdfFile.getAbsolutePath());//Uri path = Uri.fromFile(pdfFile);
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".profileimage.fileprovider", pdfFile);
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
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
