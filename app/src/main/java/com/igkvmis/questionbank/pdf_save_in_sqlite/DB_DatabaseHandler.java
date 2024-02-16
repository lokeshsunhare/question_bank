package com.igkvmis.questionbank.pdf_save_in_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class DB_DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DB_DatabaseHandler";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "KP_database";

    public final static String DATABASE_PATH = "/data/data/com.igkvmis.questionbank/databases/";

    private static final String ANSWER_SHEET_TBL = "AnswerSheet_Tbl";

    public DB_DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        boolean dbexist = checkDataBase();
        if (dbexist) {
        } else {
            try {
                copyDataBase(context);
            } catch (Exception e) {
                Log.d(TAG, "DB_DatabaseHandler: " + e.toString());
            }
        }
    }

    //Check database already exist or not
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
            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {

        }

        return null;
    }

    public void onCreate(SQLiteDatabase db) {
        String CreateAnswerSheet_Tbl = "CREATE TABLE " + ANSWER_SHEET_TBL + " (\n" +
                "\t`T_Id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`Question_Paper_Id`\tINTEGER,\n" +
                "\t`Student_Id`\tINTEGER,\n" +
                "\t`Course_Id`\tINTEGER,\n" +
                "\t`PDF_File`\tBLOB,\n" +
                "\t`File_Name`\tTEXT,\n" +
                "\t`Live_Answer_Sheet_Id`\tINTEGER\n" +
                ");";

        db.execSQL(CreateAnswerSheet_Tbl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ANSWER_SHEET_TBL);
    }

    public AnswerSheetForSQLite getStudentAnswerSheetFromLocalDB(AnswerSheetForSQLite obj) {
        byte[] PDF_File = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + ANSWER_SHEET_TBL + " WHERE Question_Paper_Id = " + obj.getQuestion_Paper_Id() + " and Student_Id = " + obj.getStudent_Id();
        Cursor cursor = db.rawQuery(selectQuery, null);
//        List<AnswerSheetForSQLite> lites = new ArrayList<>();
        AnswerSheetForSQLite fdb_obj = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    PDF_File = cursor.getBlob(cursor.getColumnIndex("PDF_File"));
                    String Question_Paper_Id = cursor.getString(cursor.getColumnIndex("Question_Paper_Id"));
                    String Student_Id = cursor.getString(cursor.getColumnIndex("Student_Id"));
                    String Course_Id = cursor.getString(cursor.getColumnIndex("Course_Id"));
                    String File_Name = cursor.getString(cursor.getColumnIndex("File_Name"));
                    String Live_Answer_Sheet_Id = cursor.getString(cursor.getColumnIndex("Live_Answer_Sheet_Id"));
//                    lites.add(new AnswerSheetForSQLite(Student_Id, Question_Paper_Id, Course_Id,
//                            PDF_File, File_Name, Live_Answer_Sheet_Id));

                    fdb_obj = new AnswerSheetForSQLite(Student_Id, Question_Paper_Id, Course_Id,
                            PDF_File, File_Name, Live_Answer_Sheet_Id);

                    // Log.d(TAG, "getStudentId: " + lites.size());
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return fdb_obj;
    }

    public int getRow(AnswerSheetForSQLite obj) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + ANSWER_SHEET_TBL + " WHERE Question_Paper_Id = " + obj.getQuestion_Paper_Id() + " and Student_Id = " + obj.getStudent_Id();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<AnswerSheetForSQLite> lites = new ArrayList<>();
        if (cursor != null) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public boolean saveAnswerSheet(AnswerSheetForSQLite obj) {
        boolean is_entry;
        Log.d(TAG, "saveAnswerSheet: count" + getRow(obj));

        if (getRow(obj) > 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Question_Paper_Id", obj.getQuestion_Paper_Id());
            contentValues.put("Student_Id", obj.getStudent_Id());
            contentValues.put("Course_Id", obj.getCourse_Id());
            contentValues.put("PDF_File", obj.getPDF_File());
            contentValues.put("File_Name", obj.getFile_Name());
            contentValues.put("Live_Answer_Sheet_Id", obj.getLive_Answer_Sheet_Id());
            is_entry = db.update(ANSWER_SHEET_TBL, contentValues, "Question_Paper_Id = ? and Student_Id = ?", new String[]{obj.getQuestion_Paper_Id(), obj.getStudent_Id()}) == 1;
            Log.d(TAG, "saveAnswerSheet: if");
            db.close();
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Question_Paper_Id", obj.getQuestion_Paper_Id());
            contentValues.put("Student_Id", obj.getStudent_Id());
            contentValues.put("Course_Id", obj.getCourse_Id());
            contentValues.put("PDF_File", obj.getPDF_File());
            contentValues.put("File_Name", obj.getFile_Name());
            contentValues.put("Live_Answer_Sheet_Id", obj.getLive_Answer_Sheet_Id());
            is_entry = db.insert(ANSWER_SHEET_TBL, null, contentValues) != -1;
            Log.d(TAG, "saveAnswerSheet: else");
            db.close();
        }
        return is_entry;
    }

    public boolean deleteAnswerSheet(AnswerSheetForSQLite obj) {
        boolean is_delete;
        SQLiteDatabase db = getWritableDatabase();
        is_delete = db.delete(ANSWER_SHEET_TBL, "Question_Paper_Id = ? and Student_Id = ?", new String[]{obj.getQuestion_Paper_Id(), obj.getStudent_Id()}) == 1;
        db.close();
        return is_delete;
    }

}