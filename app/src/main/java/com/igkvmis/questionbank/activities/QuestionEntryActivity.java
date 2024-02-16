package com.igkvmis.questionbank.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codesgood.views.JustifiedTextView;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.common.AppPreferences;

public class QuestionEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Dialog alertDialog;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_entry);
        appPreferences = new AppPreferences(this);
        initView();
    }

    private void initView() {
        WebView webview = findViewById(R.id.webview);
        webview.loadUrl("http://hmstribal.cg.nic.in/testaspxtopdf.aspx");


        toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.tv_title);
        ImageView btn_logout = findViewById(R.id.btn_logout);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            title.setText("Question Entry");
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {

        final View dialogView = View.inflate(this, R.layout.dialog_no_register_msg, null);
        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(dialogView);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;

        Button dialog_btn_sign_up = alertDialog.findViewById(R.id.dialog_btn_yes);
        Button dialog_btn_cancle = alertDialog.findViewById(R.id.dialog_btn_no);
        JustifiedTextView inform_text = alertDialog.findViewById(R.id.inform_text);
        inform_text.setGravity(Gravity.CENTER);

//        if (appPreferences.getLanguageId().equals("1")) {
//            inform_text.setText("क्या आप लॉगआउट करना चाहते हैं?");
//            dialog_btn_cancle.setText("रद्द करें");
//            dialog_btn_sign_up.setText("हाँ");
//        } else {
//
//        }

        inform_text.setText("Are you sure you want to logout?");
        dialog_btn_cancle.setText("Cancel");
        dialog_btn_sign_up.setText("YES");
        alertDialog.show();

        dialog_btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                appPreferences.clearSession();
                Intent intent_logout = new Intent(QuestionEntryActivity.this,
                        LoginActivity.class);
                intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_logout);
                finish();
                Toast.makeText(QuestionEntryActivity.this, "You have been successfully logged out.", Toast.LENGTH_SHORT).show();

            }
        });

        dialog_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

}
