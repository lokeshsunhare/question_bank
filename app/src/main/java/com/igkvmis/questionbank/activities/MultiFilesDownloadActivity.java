package com.igkvmis.questionbank.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.adapter.FilesListAdapter;
import com.igkvmis.questionbank.model.FileDownload;

import java.util.ArrayList;
import java.util.List;

public class MultiFilesDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_files_download);
        RecyclerView recycler_list = findViewById(R.id.recycler_list);

        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(recycler_list, false);
        recycler_list.setNestedScrollingEnabled(false);
        List<FileDownload> list = new ArrayList<>();

        list.add(new FileDownload("http://192.168.1.29/_StudyAssignmentFile/Uploaded/1040_519_20200714161106079_News_2020_06_02_05_38_15.pdf"));
        list.add(new FileDownload("https://media.giphy.com/media/Bk0CW5frw4qfS/giphy.gif"));
        list.add(new FileDownload("http://techslides.com/demos/sample-videos/small.mp4"));
        list.add(new FileDownload("http://africau.edu/images/default/sample.pdf"));
        list.add(new FileDownload("http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_10mb.mp4"));

        FilesListAdapter adapter = new FilesListAdapter(this, list);
        recycler_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
