package app.netframe.utils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * @author fengyongge
 * @Description  log日志
 */
public class LogUtils {

    public static void init(boolean isLogEnable) {
        Logger.init("fyg")
                .hideThreadInfo()
                .logLevel(isLogEnable ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(2);
    }

    public static void d(String message) {
        Logger.d(message);
    }

    public static void i(String message) {
        Logger.i(message);
    }

    public static void w(String message, Throwable e) {
        String info = e != null ? e.toString() : "null";
        Logger.w(message + "：" + info);
    }

    public static void e(String message, Throwable e) {
        Logger.e(e, message);
    }

    public static void json(String json) {
        Logger.json(json);
    }
}
