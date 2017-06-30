package app.netframe.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import app.netframe.utils.LogUtils;

/**
 * Created by fengyongge on 2016/12/1 0001.
 */

public class MyApp extends Application {

    private static MyApp instance;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化
        LogUtils.init(AppConfig.DEBUG);

        instance = (MyApp) getApplicationContext();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                defaultOptions).build();
        ImageLoader.getInstance().init(config);
    }

    public static MyApp getInstance() {
        return instance;
    }

    public SharedPreferences getMustElement() {
        sp = getSharedPreferences("MUST", MODE_PRIVATE);
        return sp;
    }

}
