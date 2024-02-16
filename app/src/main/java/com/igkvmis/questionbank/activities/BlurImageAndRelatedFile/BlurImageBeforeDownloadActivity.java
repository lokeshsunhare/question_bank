package com.igkvmis.questionbank.activities.BlurImageAndRelatedFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.igkvmis.questionbank.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlurImageBeforeDownloadActivity extends AppCompatActivity
//        implements PRDownloaderHandler.OnResponseListener
{

    //    private ImageView image_view;
    //    private String IMAGE_URL = "http://192.168.42.224/Images/eKrishiPathShala/Slider/1.jpg";
//    TextView textViewProgressOne, buttonCancelOne;
//    CircularProgressBar progressBarOne;
//    TextView buttonOne;
//    int downloadIdOne;
//    private RelativeLayout layoutButton;
//    private RelativeLayout layout_after_start;
    private RecyclerView recycler_image_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_image_before_download);
        initView();
    }

    private void initView() {
        recycler_image_list = findViewById(R.id.recycler_image_list);
        recycler_image_list.setHasFixedSize(true);
        recycler_image_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recycler_image_list.setNestedScrollingEnabled(false);

        List<Images> images = new ArrayList<>();
        images.add(new Images("", "", "https://i1.wp.com/www.oakridge.in/wp-content/uploads/2020/02/Sample-jpg-image-500kb.jpg"));
        images.add(new Images("", "", "https://budgetstockphoto.com/samples/pics/abstractwire.jpg"));
        images.add(new Images("", "", "https://budgetstockphoto.com/samples/pics/carspeed.jpg"));
        images.add(new Images("", "", "https://budgetstockphoto.com/samples/pics/dice.jpg"));

        ImageListAdapter adapter = new ImageListAdapter(BlurImageBeforeDownloadActivity.this, images);
        recycler_image_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

//        image_view = findViewById(R.id.image_view);
//        Picasso.with(this).load("http://192.168.42.224/Images/eKrishiPathShala/Slider/1.jpg").transform(
//                new BlurTransformation(this, 25))
//                .fit()
//                .error(R.drawable.ic_copy).into(image_view);


    }
}

