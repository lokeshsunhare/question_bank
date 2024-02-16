package com.igkvmis.questionbank.adapter;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.activities.PRDownloaderHandler;
import com.igkvmis.questionbank.model.FileDownload;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class FilesListAdapter extends RecyclerView.Adapter {

    private List<FileDownload> list;
    private Context mContext;

    private String root = Environment.getExternalStorageDirectory().toString() + "/" + "Download" + "/" + "PR_Download" + "/";

    private static final String TAG = "FilesListAdapter";

    public FilesListAdapter(Context context, List<FileDownload> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_file_items, parent, false);
        return new FilesListHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final FileDownload cmodel = list.get(holder.getAdapterPosition());

        if (cmodel != null) {

            String FileName = cmodel.getFileURL().
                    substring(cmodel.getFileURL().lastIndexOf("/") + 1, cmodel.getFileURL().toString().length());
            PRDownloaderHandler pHandler = new PRDownloaderHandler(mContext);
            pHandler.setAllField(((FilesListHolder) holder).textViewProgressOne,
                    ((FilesListHolder) holder).buttonCancelOne,
                    ((FilesListHolder) holder).progressBarOne,
                    ((FilesListHolder) holder).buttonOne, ((FilesListHolder) holder).view_file,
                    ((FilesListHolder) holder).layoutButton,
                    ((FilesListHolder) holder).layout_after_start);
            pHandler.listener(cmodel.getFileURL(), FileName);
//            listener(holder, cmodel.getFileURL(), FileName);
            pHandler.checkExistsFile(FileName);
            ((FilesListHolder) holder).view_file.setOnClickListener(v -> {
                pHandler.viewOfflineFile(FileName);
            });


        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    private static class FilesListHolder extends RecyclerView.ViewHolder {

        TextView textViewProgressOne, buttonCancelOne;
        CircularProgressBar progressBarOne;
        TextView buttonOne;
        Button view_file;
        int downloadIdOne;
        private RelativeLayout layoutButton;
        private RelativeLayout layout_after_start;

        private FilesListHolder(View view) {
            super(view);
            textViewProgressOne = view.findViewById(R.id.textViewProgressOne);
            buttonCancelOne = view.findViewById(R.id.buttonCancelOne);
            buttonOne = view.findViewById(R.id.buttonOne);
            progressBarOne = view.findViewById(R.id.progressBarOne);
            layoutButton = view.findViewById(R.id.layoutButton);
            view_file = view.findViewById(R.id.view_file);
            layout_after_start = view.findViewById(R.id.layout_after_start);
        }
    }

//    private void checkExistsFile(RecyclerView.ViewHolder holder, String file_name) {
//        File pdfFile = new File(root + file_name);
//        if (pdfFile.exists()) {
//            ((FilesListHolder) holder).view_file.setVisibility(View.VISIBLE);
//            ((FilesListHolder) holder).layoutButton.setVisibility(View.GONE);
//        } else {
//            ((FilesListHolder) holder).view_file.setVisibility(View.GONE);
//            ((FilesListHolder) holder).layoutButton.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private String getFilePath() {
//        final File dir = new File(root);
//        if (!dir.exists())
//            dir.mkdirs();
//        Log.d(TAG, "getFilePath1: " + dir.getPath());
//        return dir.getPath();
//    }
//
//    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
////        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
//        return getReadableFileSize(currentBytes) + "/" + getReadableFileSize(totalBytes);
//    }
//
//    private static String getBytesToMBString(long bytes) {
//        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
//    }
//
//    public static String getReadableFileSize(long size) {
//        final double BYTES_IN_KILOBYTES = 1024.0;
//        final DecimalFormat dec = new DecimalFormat("###.#");
//        final String KILOBYTES = " KB";
//        final String MEGABYTES = " MB";
//        final String GIGABYTES = " GB";
//        double fileSize = 0.0;
//        String suffix = KILOBYTES;
//
//        if (size > BYTES_IN_KILOBYTES) {
//            fileSize = size / BYTES_IN_KILOBYTES;
//            if (fileSize > BYTES_IN_KILOBYTES) {
//                fileSize = fileSize / BYTES_IN_KILOBYTES;
//                if (fileSize > BYTES_IN_KILOBYTES) {
//                    fileSize = fileSize / BYTES_IN_KILOBYTES;
//                    suffix = GIGABYTES;
//                } else {
//                    suffix = MEGABYTES;
//                }
//            }
//        }
//        return String.valueOf(dec.format(fileSize) + suffix);
//    }
//
//
//    private void listener(RecyclerView.ViewHolder holder, String URL, String FileName) {
//        ((FilesListHolder) holder).buttonOne.setOnClickListener(view -> {
//
//            if (Status.RUNNING == PRDownloader.getStatus(((FilesListHolder) holder).downloadIdOne)) {
//                PRDownloader.pause(((FilesListHolder) holder).downloadIdOne);
//                return;
//            }
//
//            ((FilesListHolder) holder).buttonOne.setEnabled(false);
//            ((FilesListHolder) holder).buttonOne.setVisibility(View.GONE);
//            ((FilesListHolder) holder).layout_after_start.setVisibility(View.VISIBLE);
//
//            ((FilesListHolder) holder).progressBarOne.setIndeterminateMode(true);
////            ((FilesListHolder) holder).progressBarOne.getIndeterminateDrawable().setColorFilter(
////                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
//
//            if (Status.PAUSED == PRDownloader.getStatus(((FilesListHolder) holder).downloadIdOne)) {
//                PRDownloader.resume(((FilesListHolder) holder).downloadIdOne);
//                return;
//            }
//            ((FilesListHolder) holder).downloadIdOne = PRDownloader.download(URL, getFilePath(), FileName)
//                    .build()
//                    .setOnStartOrResumeListener(() -> {
//                        ((FilesListHolder) holder).progressBarOne.setIndeterminateMode(false);
//                        ((FilesListHolder) holder).buttonOne.setEnabled(true);
//                        ((FilesListHolder) holder).buttonOne.setText("Pause");
//                        ((FilesListHolder) holder).buttonCancelOne.setEnabled(true);
//                    })
//                    .setOnPauseListener(() -> ((FilesListHolder) holder).buttonOne.setText("Resume"))
//                    .setOnCancelListener(() -> {
//                        ((FilesListHolder) holder).buttonOne.setEnabled(true);
//                        ((FilesListHolder) holder).buttonOne.setVisibility(View.VISIBLE);
//                        ((FilesListHolder) holder).layout_after_start.setVisibility(View.GONE);
//
//                        ((FilesListHolder) holder).buttonOne.setText("Start");
//                        ((FilesListHolder) holder).buttonCancelOne.setEnabled(false);
//                        ((FilesListHolder) holder).progressBarOne.setProgress(0);
//                        ((FilesListHolder) holder).textViewProgressOne.setText("");
//                        ((FilesListHolder) holder).downloadIdOne = 0;
//                        ((FilesListHolder) holder).progressBarOne.setIndeterminateMode(false);
//                    })
//                    .setOnProgressListener(progress -> {
//                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
//                        ((FilesListHolder) holder).progressBarOne.setProgress((int) progressPercent);
//                        ((FilesListHolder) holder).textViewProgressOne.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
//                        ((FilesListHolder) holder).progressBarOne.setIndeterminateMode(false);
//                    })
//                    .start(new OnDownloadListener() {
//                        @Override
//                        public void onDownloadComplete() {
//                            ((FilesListHolder) holder).buttonOne.setEnabled(false);
//                            ((FilesListHolder) holder).buttonCancelOne.setEnabled(false);
//                            ((FilesListHolder) holder).buttonOne.setText("Completed");
//                            checkExistsFile(holder, FileName);
//                        }
//
//                        @Override
//                        public void onError(Error error) {
//                            ((FilesListHolder) holder).buttonOne.setText("Start");
//                            Toast.makeText(mContext, "some_error_occurred" + " " + "1", Toast.LENGTH_SHORT).show();
//                            ((FilesListHolder) holder).textViewProgressOne.setText("");
//                            ((FilesListHolder) holder).progressBarOne.setProgress(0);
//                            ((FilesListHolder) holder).downloadIdOne = 0;
//                            ((FilesListHolder) holder).buttonCancelOne.setEnabled(false);
//                            ((FilesListHolder) holder).progressBarOne.setIndeterminateMode(false);
//                            ((FilesListHolder) holder).buttonOne.setEnabled(true);
//                        }
//                    });
//        });
//
//        ((FilesListHolder) holder).buttonCancelOne.setOnClickListener(view -> PRDownloader.cancel(((FilesListHolder) holder).downloadIdOne));
//    }
//
//    public void viewOfflineFile(String FileName) { //file path is for syllabus
//        File pdfFile = new File(root + FileName);
//        if (pdfFile.exists()) {
//            Log.d(TAG, "viewDownloadedFile : " + pdfFile.getAbsolutePath());//Uri path = Uri.fromFile(pdfFile);
//            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".profileimage.fileprovider", pdfFile);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String url = pdfFile.getAbsolutePath().toString();
//            if (url.contains(".doc") || url.contains(".docx")) {
//                // Word document
//                intent.setDataAndType(uri, "application/msword");
//            } else if (url.contains(".pdf")) {
//                // PDF file
//                intent.setDataAndType(uri, "application/pdf");
//            } else if (url.contains(".ppt") || url.contains(".pptx")) {
//                // Powerpoint file
//                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//            } else if (url.contains(".xls") || url.contains(".xlsx")) {
//                // Excel file
//                intent.setDataAndType(uri, "application/vnd.ms-excel");
//            } else if (url.contains(".zip") || url.contains(".rar")) {
//                // WAV audio file
//                intent.setDataAndType(uri, "application/x-wav");
//            } else if (url.contains(".rtf")) {
//                // RTF file
//                intent.setDataAndType(uri, "application/rtf");
//            } else if (url.contains(".wav") || url.contains(".mp3")) {
//                // WAV audio file
//                intent.setDataAndType(uri, "audio/x-wav");
//            } else if (url.contains(".gif")) {
//                // GIF file
//                intent.setDataAndType(uri, "image/gif");
//            } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
//                // JPG file
//                intent.setDataAndType(uri, "image/jpeg");
//            } else if (url.contains(".txt")) {
//                // Text file
//                intent.setDataAndType(uri, "text/plain");
//            } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") ||
//                    url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
//                // Video files
//                intent.setDataAndType(uri, "video/*");
//            } else {
//                intent.setDataAndType(uri, "*/*");
//            }
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//            try {
//                mContext.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(mContext, "No application found which can open the file", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }


}
