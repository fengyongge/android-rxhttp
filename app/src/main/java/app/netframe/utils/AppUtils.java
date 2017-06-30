package app.netframe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author fengyongge
 * @Description  跟App相关的辅助类及获取手机的一些常数
 */
public class AppUtils {

    /**
     * imei
     */
    public static final int IMEI = 1;

    /**
     * imsi
     */
    public static final int IMSI = 2;
    /**
     * 手机型号
     */
    public static final int MODEL = 3;
    /**
     * 手机号码
     */
    public static final int PHONENO = 4;


    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }


    /**
     * 获取版本号(内部识别号)
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 获取手机的一些信息
     *
     * @param context 上下文
     * @param type    imei imsi model phoneno
     * @return
     */
    public static String getPhoneInfo(Context context, int type) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (type) {
            case IMEI:
                return mTm.getDeviceId();
            case IMSI:
                return mTm.getSubscriberId();
            case MODEL:
                return android.os.Build.MODEL;
            case PHONENO:
                return mTm.getLine1Number();
            default:
                return "";
        }
    }

    /**
     * 获取手机cpu信息
     */
    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }


    /**
     * 获取app信息
     * @param context
     * @return
     */
    private String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }


}
