package com.igkvmis.questionbank.activities.Encrypted_Sqlite_Test;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.downloader.database.DbHelper;
import com.igkvmis.questionbank.app.MyApplication;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASE_PATH = "/data/data/igkv.igkvcropdoctor/databases/";
    public static String DATABASE_NAME = "CropDoctor";

    private SQLiteDatabase database;
    private Context context;
    private static final String PASS_WORD = "1234";
    private static DBHelper mInstance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.context = context;
        if (Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/igkv.igkvcropdoctor/databases/";
        }
        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    public void createDatabase() throws IOException {
        boolean dbexist = checkDataBase();
        if (!dbexist) {
            this.getReadableDatabase(PASS_WORD);
            this.close();
            try {
                copyDataBase(context);
            } catch (Exception e) {

            }
        }
    }
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    private void copyDataBase(Context context) throws IOException {
        AssetManager am = context.getAssets();
        InputStream mInput = am.open(DATABASE_NAME);
        String outFileName = DATABASE_PATH;
        createFileFromInputStream(mInput, outFileName, DATABASE_NAME);
    }

    private File createFileFromInputStream(InputStream inputStream, String FileName, String DBName) {

        try {
            File f = new File(FileName);
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(FileName + DBName);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {

        }
        return null;
    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        super.close();
    }

    public boolean openDataBase() throws SQLiteException {
        String path = DATABASE_PATH + DATABASE_NAME;
        Log.d(TAG, "openDataBase:111 path " + path);
        database = SQLiteDatabase.openDatabase(path, PASS_WORD, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return database != null;
    }

    private static final String TAG = "DBHelper";

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
