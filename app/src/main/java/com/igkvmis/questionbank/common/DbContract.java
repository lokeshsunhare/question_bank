package com.igkvmis.questionbank.common;

import java.util.regex.Pattern;

public class DbContract {

    public static final String CART_TABLE = "cart_table";

    public static final String SMS_ORIGIN = "NICSMS";
    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";

    public static String UNIQUE_ID = "200";


    public static final String IS_WELCOME_ACTIVITY_SHOWN = "is_Welcome_activity_shown";
    public static final String SHOW_WELCOME_ACT = "show_welcome_activity";


    public static boolean isValidLandlineNumber(CharSequence target) {
        return !(target == null || target.length() < 6 || target.length() > 13) && android.util.Patterns.PHONE.matcher(target).matches();
    }

    public static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^(?:(?:\\+|0{0,2})91(\\s*[\\ -]\\s*)?|[0]?)?[6789]\\d{9}|(\\d[ -]?){10}\\d$";
        return mobile.matches(regEx);
    }

    public static boolean isValidGSTNumber(String gst_no) {
        String regEx = "^([0][1-9]|[1-2][0-9]|[3][0-7])([a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ]{1}[0-9a-zA-Z]{1})+$";
        return gst_no.matches(regEx);
    }

    public static boolean isInteger(String str) {
        return (str.lastIndexOf("-") == 0 && !str.equals("-0")) ? str.replace("-", "").matches(
                "[0-9]+") : str.matches("[0-9]+");
    }

    public static boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValidAddress(String address) {
        String regEx = "^[#.0-9a-zA-Z\\s,-]+$";
        return address.matches(regEx);
    }

    public static boolean validateName(String name) {
        String regx = "[a-zA-Z ]+";
//        String regx = "^[A-Za-z\\\\s]{1,}[\\\\.]{0,1}[A-Za-z\\\\s]{0,}$";
        return name.matches(regx);
    }

    public enum SharedPreferenceKeys {
        USER_NAME("userName"),
        USER_EMAIL("userEmail"),
        USER_IMAGE_URL("userImageUrl"),
        USER_GENDER("gender");

        private String value;

        SharedPreferenceKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }

    public static final String GOOGLE_CLIENT_ID = "307391029465-607c9mqr415k3i2i8fib8qu7b2gj5vn3.apps.googleusercontent.com";


}
