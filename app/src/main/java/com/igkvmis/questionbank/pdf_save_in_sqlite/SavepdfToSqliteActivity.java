package com.igkvmis.questionbank.pdf_save_in_sqlite;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.activities.PDFViewWithoutDownload.PDFViewWithoutDownloadActivity;
import com.shockwave.pdfium.PdfDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

public class SavepdfToSqliteActivity extends AppCompatActivity implements OnPageChangeListener,
        OnLoadCompleteListener,
        OnPageErrorListener {

    private static final String TAG = "SavepdfToSqliteActivity";

    private int pageNumber = 0;

    private String pdfFileName;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savepdf_to_sqlite);
        pdfView = findViewById(R.id.pdfView);

        DB_DatabaseHandler db = new DB_DatabaseHandler(this);
        TextView tv_row = findViewById(R.id.tv_row);
        TextView btn_click = findViewById(R.id.btn_click);

        tv_row.setOnClickListener(v -> {

            AnswerSheetForSQLite sheetForSQLite = new AnswerSheetForSQLite("123",
                    "123");

            byte[] bytesdb = null;
            String db_file_name = null;
            AnswerSheetForSQLite localDB_Obj = db.getStudentAnswerSheetFromLocalDB(sheetForSQLite);
            bytesdb = localDB_Obj.getPDF_File();
            db_file_name = localDB_Obj.getFile_Name();

            if (db.deleteAnswerSheet(localDB_Obj)) {

//                pdfView.fromBytes(bytesdb)
//                        .defaultPage(pageNumber)
//                        .onPageChange(SavepdfToSqliteActivity.this)
//                        .enableAnnotationRendering(true)
//                        .onLoad(SavepdfToSqliteActivity.this)
//                        .scrollHandle(new DefaultScrollHandle(SavepdfToSqliteActivity.this))
//                        .spacing(10) // in dp
//                        .onPageError(SavepdfToSqliteActivity.this)
//                        .load();

                String root = Environment.getExternalStorageDirectory().toString() + "/" + "eKrishiPathShala" + "/";
                File file_db = new File(root + "DB/");
                if (!file_db.exists()) {
                    file_db.mkdir();
                }
                File file = new File(file_db.getAbsolutePath() + "/" + db_file_name);
                if (file.exists()) {
                    file.delete();
                }
                Log.d(TAG, "onClick: deleted");
            }
        });
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File f = new File("/storage/emulated/0/eKrishiPathShala/AnswerSheet.pdf");
                byte[] PDFFile = new byte[0];
                try {
                    PDFFile = getBytes(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AnswerSheetForSQLite sheetForSQLite = new AnswerSheetForSQLite("123",
                        "123", "3121213",
                        PDFFile, "file_Name.pdf", "");

                if (db.saveAnswerSheet(sheetForSQLite)) {
                    Log.d(TAG, "onClick: inserted");

                    byte[] bytesdb = null;
                    String db_file_name = null;
                    AnswerSheetForSQLite localDB_Obj = db.getStudentAnswerSheetFromLocalDB(sheetForSQLite);
                    bytesdb = localDB_Obj.getPDF_File();
                    db_file_name = localDB_Obj.getFile_Name();

                    pdfView.fromBytes(bytesdb)
                            .defaultPage(pageNumber)
                            .onPageChange(SavepdfToSqliteActivity.this)
                            .enableAnnotationRendering(true)
                            .onLoad(SavepdfToSqliteActivity.this)
                            .scrollHandle(new DefaultScrollHandle(SavepdfToSqliteActivity.this))
                            .spacing(10) // in dp
                            .onPageError(SavepdfToSqliteActivity.this)
                            .load();

                    String root = Environment.getExternalStorageDirectory().toString() + "/" + "eKrishiPathShala" + "/";

                    File file_db = new File(root + "DB/");
                    if (!file_db.exists()) {
                        file_db.mkdir();
                    }
                    File file = new File(file_db.getAbsolutePath() + "/" + db_file_name);
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bytesdb);
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.d(TAG, "onClick: " + bytesdb);
                }
            }
        });
    }

    public byte[] getBytes(File f) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        int read;
        while ((read = fis.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
        fis.close();
        os.close();
        return os.toByteArray();
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