package com.igkvmis.questionbank.activities.BlurImageAndRelatedFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.activities.PDFViewWithoutDownload.PDFViewWithoutDownloadActivity;
import com.igkvmis.questionbank.app.MyApplication;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PRDownloaderHandler.OnResponseListener {

    private List<Images> list;
    private Context context;
    private Activity activity;
    private static final String TAG = "ChatMessageListAdapter";
    private ImageListHolder mHolder;

    public ImageListAdapter(Context context, List<Images> list) {
        this.list = list;
        this.context = context;
        activity = (Activity) context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);
        return new ImageListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Images imodel = list.get(holder.getAdapterPosition());
        if (imodel != null) {
            mHolder = ((ImageListHolder) holder);

            String FileName = imodel.getIMG_URL().
                    substring(imodel.getIMG_URL().lastIndexOf("/") + 1, imodel.getIMG_URL().toString().length());

            new getFileSizeWithoutDownLoad(((ImageListHolder) holder).buttonOne)
                    .execute(imodel.getIMG_URL()
                            .replace(" ", "%20").trim());

            PRDownloaderHandler pHandler = new PRDownloaderHandler(context, ImageListAdapter.this);
            pHandler.setAllField(((ImageListHolder) holder).textViewProgressOne,
                    ((ImageListHolder) holder).buttonCancelOne,
                    ((ImageListHolder) holder).progressBarOne,
                    ((ImageListHolder) holder).buttonOne,
                    ((ImageListHolder) holder).layoutButton,
                    ((ImageListHolder) holder).layout_after_start);
            pHandler.listener(imodel.getIMG_URL().replace(" ", "%20").trim(), FileName);

            if (pHandler.checkExistsFile(FileName)) {
                File f = new File((PRDownloaderHandler.root + FileName).replace(" ", "%20").trim());
                Picasso.with(activity).load(f).fit()
                        .placeholder(R.drawable.ic_copy)
                        .error(R.drawable.ic_copy)
                        .into(((ImageListHolder) holder).image_view);
                ((ImageListHolder) holder).image_view.setOnClickListener(v -> {
                    pHandler.viewOfflineFile(FileName);
                });
            } else {
                Picasso.with(activity).load(imodel.getIMG_URL()).transform(
                        new BlurTransformation(activity, 25))
                        .fit()
                        .error(R.drawable.ic_copy).into(((ImageListHolder) holder).image_view);
            }


        }

    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public void onResponseListener() {
        notifyDataSetChanged();
    }

    public class ImageListHolder extends RecyclerView.ViewHolder {
        private ImageView image_view;

        TextView textViewProgressOne, buttonCancelOne;
        CircularProgressBar progressBarOne;
        TextView buttonOne;
        int downloadIdOne;
        private RelativeLayout layoutButton;
        private RelativeLayout layout_after_start;

        private ImageListHolder(View view) {
            super(view);

            image_view = view.findViewById(R.id.image_view);
            textViewProgressOne = view.findViewById(R.id.textViewProgressOne);
            buttonCancelOne = view.findViewById(R.id.buttonCancelOne);
            buttonOne = view.findViewById(R.id.buttonOne);
            progressBarOne = view.findViewById(R.id.progressBarOne);
            layoutButton = view.findViewById(R.id.layoutButton);
            layout_after_start = view.findViewById(R.id.layout_after_start);

        }
    }

    private class getFileSizeWithoutDownLoad extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private TextView textView;

        public getFileSizeWithoutDownLoad(TextView textView) {
            this.textView = textView;
        }

        protected String doInBackground(String... strings) {
            long size = 0;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("HEAD");
                urlConnection.getInputStream();
                size = urlConnection.getContentLength();
                urlConnection.getInputStream().close();
            } catch (Exception e) {
                return null;
            }
            return getReadableFileSize(size);
        }

        protected void onPostExecute(String fileSize) {
            textView.setText(fileSize);
        }
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

}
