package com.igkvmis.questionbank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = MyBroadCastReceiver.class.getSimpleName();

    private Date date = new Date();
    private DateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("auto_recall", "" + intent.getAction());

        String action = intent.getAction();
        Date currentDate = new Date();

        if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
            Log.d(TAG, "onReceive: date_time " + _sdfWatchTime.format(new Date()));

//        if (action != null && !isSameDay(currentDate) &&
//                (action.equals(Intent.ACTION_TIME_CHANGED)
//                        || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
//                        || action.equals(Intent.ACTION_TIME_TICK))) {
//            date = currentDate;
//            //onDayChanged();
//            Toast.makeText(context, "Time Changed", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onReceive: CheckTime Time Changed");
//        }

//        if (action != null &&
//                (action.equals(Intent.ACTION_TIME_CHANGED)
//                        || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
//                        || action.equals(Intent.ACTION_TIME_TICK)
//                        || action.equals(Intent.ACTION_DATE_CHANGED)
//                )) {
//            Log.d(TAG, "onReceive: CheckTime Time Changed");
//        }
    }

}


//        if (intent.getAction().equals(BaseActivity.INTENT_REFRESH_LANGUAGE)) {
//            Log.d("auto_recall_language", "" + intent.getAction());
//        } else if (intent.getAction().equals(VideoViewFragment.INTENT_START_VIDEO)) {
//            Log.d("auto_recall_video", "" + intent.getAction());
//        } else if (intent.getAction().equals(DashboardViewFragment.INTENT_CALL_FRAGMENT)) {
//            Log.d("auto_CALL_FRAGMENT", "" + intent.getAction());
//        } else {
//            final Bundle bundle = intent.getExtras();
//            try {
//                if (bundle != null) {
//                    Object[] pdusObj = (Object[]) bundle.get("pdus");
//                    for (Object aPdusObj : pdusObj) {
//                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
//                        String senderAddress = currentMessage.getDisplayOriginatingAddress();
//                        String message = currentMessage.getDisplayMessageBody();
//
//                        Log.e("Received_sms", "Received SMS: " + message + ", Sender: " + senderAddress);
//
//                        // if the SMS is not from our gateway, ignore the message
//
//                        if (!senderAddress.toLowerCase().contains("NICSMS".toLowerCase())) {
//                            Log.e(TAG, "SMS is not for our app!");
//                            return;
//                        }
//                        // verification code from sms
//                        String verificationCode = getVerificationCode(message);
////                        String verificationCode = message.split(": ")[1];
//
//                        Log.e("Received_Otp", "OTP received: " + verificationCode.trim());
//
//                        Intent hhtpIntent = new Intent(FarmerLoginSignUpActivity.INTENT_GET_OTP_DATA);
//                        hhtpIntent.putExtra("otp", verificationCode);
//                        context.sendBroadcast(hhtpIntent);
//
//
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("Received_sms", "Exception: " + e.getMessage());
//            }
//        }

//    }
//
//}
