package cn.jmessage.android.uikit.chatting.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import com.swqs.schooltrade.app.TradeApplication;

import android.os.Environment;
import android.text.format.DateFormat;

/**
 * Created by Ken on 2015/11/13.
 */
public class FileHelper {

    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getUserAvatarPath(String userName) {
        return TradeApplication.PICTURE_DIR + userName + ".png";
    }

    public static String createAvatarPath(String userName) {
        String dir = TradeApplication.PICTURE_DIR;
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file;
        if (userName != null) {
            file = new File(dir, userName + ".png");
        }else {
            file = new File(dir, new DateFormat().format("yyyy_MMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".png");
        }
        return file.getAbsolutePath();
    }
}
