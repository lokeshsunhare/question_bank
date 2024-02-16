package com.igkvmis.questionbank.activities.Encrypted_Sqlite_Test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.igkvmis.questionbank.R;

import net.sqlcipher.database.SQLiteDatabase;

public class CipherSqliteDatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_sqlite_database);
        SQLiteDatabase.loadLibs(this);
        DBHelper db = new DBHelper(this);

    }
}