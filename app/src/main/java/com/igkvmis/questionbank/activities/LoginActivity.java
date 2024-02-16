package com.igkvmis.questionbank.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.igkvmis.questionbank.R;
import com.igkvmis.questionbank.app.MyApplication;
import com.igkvmis.questionbank.common.AllApiActivity;
import com.igkvmis.questionbank.common.AppPreferences;
import com.igkvmis.questionbank.common.DbContract;
import com.igkvmis.questionbank.common.NetworkCheckActivity;
import com.igkvmis.questionbank.model.LoginRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private AppPreferences appPreferences;

    private LinearLayout layout_password;
    private AppCompatTextView actv_login_with_pass;
    private TextView tv_lbl_pass;

    private EditText et_login_id;
    private AppCompatTextView actv_login_user;
    private long mLastClickTime = 0;
    public String POST_TIME = null;

    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        appPreferences = new AppPreferences(LoginActivity.this);
        initView();
    }

    private void initView() {

        if (appPreferences.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, QuestionEntryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        actv_login_with_pass = findViewById(R.id.actv_login_with_pass);
        et_login_id = findViewById(R.id.et_login_id);
        et_password = findViewById(R.id.et_password);

        actv_login_with_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldForLoginWithPassword();
            }
        });
    }

    private void validateFieldForLoginWithPassword() {

        String pass = "";
        String mobile = "";
        String valid_mobile = "";

        boolean valid = true;
        if (et_login_id.getText().toString().trim().isEmpty()) {
            valid = false;
            et_login_id.setError(mobile);
            et_login_id.requestFocus();
        }
//        else if (!et_login_id.getText().toString().trim().isEmpty() &&
//                !DbContract.isValidPhoneNumber(et_login_id.getText().toString().trim())) {
//            valid = false;
//            et_login_id.setError(valid_mobile);
//            et_login_id.requestFocus();
//        }
        else if (et_password.getText().toString().trim().isEmpty()) {
            valid = false;
            et_password.setError(pass);
            et_password.requestFocus();
        }
        if (valid) {
            if (NetworkCheckActivity.isNetworkAvailable(LoginActivity.this)) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                POST_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                // userLogin(et_login_id.getText().toString().trim(), et_password.getText().toString().trim());
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void userLogin(String user_id, String pass) {
//
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.show();
//        progressDialog.setMessage("Please wait....");
//        Api api = AllApiActivity.getInstance().getApi();
//        Call<LoginRes> loginResCall = api.getLogin(user_id, pass);
//        loginResCall.enqueue(new Callback<LoginRes>() {
//            @Override
//            public void onResponse(Call<LoginRes> call, retrofit2.Response<LoginRes> response) {
//                List<com.igkvmis.questionbank.model.Response> responseList = response.body().getResponse();
//                for (int i = 0; i < responseList.size(); i++) {
//                    String status = responseList.get(i).getSuccess();
//                    String msg = responseList.get(i).getMsg();
//                    String Admin_ID = responseList.get(i).getAdminID();
//                    String User_Type = responseList.get(i).getUserType();
//                    if (status.equals("1")) {
//                        appPreferences.createLogin(Admin_ID, User_Type);
//                        Intent intent = new Intent(LoginActivity.this, QuestionEntryActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginRes> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

}
