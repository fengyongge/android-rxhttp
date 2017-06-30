package app.netframe.mynet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.netframe.app.AppConfig;
import app.netframe.callback.ApiCallback;
import app.netframe.utils.LogUtils;
import app.netframe.utils.StringUtils;
import okhttp3.MultipartBody;


public class OkhttpUtils {
  
    public  static OkhttpUtils mInstance;
    public  static OkHttpClient mOkHttpClient;
    public static Handler mDelivery;



    public static OkhttpUtils getInstance() {

        if (mInstance == null) {
            synchronized (OkhttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpUtils();
                }
            }
        }

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());



        return mInstance;
    }





    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    public static void _getAsyn(String url, final ApiCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        LogUtils.i("请求的url："+url);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    public static void _postAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        LogUtils.i("请求的url："+url);
        deliveryResult(callback, request);
    }

    /**
     * 异步的put方式
     * @param url
     * @param callback
     * @param params
     */
    public static void _putAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildPutRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的delete方式
     * @param url
     * @param callback
     * @param params
     */
    public static void _deleteAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildDeleteRequest(url, params);
        deliveryResult(callback, request);
    }




    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    public static void _getSynchronization(String url, final ApiCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = null;
        try {
            execute = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deliveryResult(callback,execute);
    }


    /**
     * 同步基于post的文件上传
     *
     * @param params
     *
     * @return
     */
    public static Response _post(String url, File[] files, String[] fileKeys, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    public static Response _post(String url, File file, String fileKey, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    public static Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }




    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     *
     * @throws IOException
     */
    public static void _postAsyn(String url, ApiCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     *
     * @throws IOException
     */
    public static void _postAsyn(String url, ApiCallback callback, File file, String fileKey, List<Param> params) throws IOException {

        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }


    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     *
     * @throws IOException
     */
    public static void _postAsyn(String url, ApiCallback callback, File[] files, String fileKeys, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, new String[]{fileKeys}, params);
        deliveryResult(callback, request);
    }




    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    public void _downloadAsyn(final String url, final String destFileDir, final ApiCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {

//                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
//                    sendSuccessApiCallback(file.getAbsolutePath(), callback);

                } catch (IOException e)
                {
//                    sendFailedStringCallback(response.request(), e, callback);
                } finally
                {
                    try
                    {
                        if (is != null) is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null) fos.close();
                    } catch (IOException e)
                    {
                    }
                }
            }
        });
    }



    //--------------------------------------------------------------------------------------------





    public static Request buildMultipartFormRequest(String url, File[] files,
                                                    String[] fileKeys, List<Param> params) {

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }

        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);

                builder.addPart(Headers.of(),
                        fileBody);

            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }



    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    //
    public static Request buildPostRequest(String url, List<Param> params) {

        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    public static Request buildPutRequest(String url, List<Param> params) {

        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
    }

    public static Request buildDeleteRequest(String url, List<Param> params) {

        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
    }


    /**
     * 异步返回结果
     * @param callback
     * @param request
     */
    public static void deliveryResult(final ApiCallback callback, Request request) {

            mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {

                LogUtils.i("onFailure："+e.getMessage());

                if(StringUtils.isNotEmpty(e.getMessage())){

                    if(e.getMessage().contains("No address associated with hostname")){
                        sendNetErrorStringCallback("网络异常,请检查网络",callback);
                    }

                }else{
                    sendNetErrorStringCallback(e.getMessage(),callback);
                }

            }

            @Override
            public void onResponse(final Response response) {
                try {
                    String str = response.body().string();

                    LogUtils.i("后台响应：" + response.message() + "-" + response.code() + "\n后台返回数据：" + str);

                    if(response.code()>=200&&response.code()<300) {

                        if(StringUtils.isNotEmpty(str)){

                            JSONObject result = JSON.parseObject(str); //根据自己后台code做判断

                            if (Integer.parseInt(result.getString("code")) >= 200 && Integer.parseInt(result.getString("code")) < 300) {
                                sendSuccessApiCallback(result, callback);
                            } else {
                                sendFailedStringCallback(result, callback);
                            }

                        }else{
                            sendNetErrorStringCallback(response.message() + "-code:" + response.code(),callback);
                        }
                    }else{

                        sendNetErrorStringCallback(response.message() + "-code:" + response.code(),callback);

                    }



                } catch (final Exception e) {

                    sendNetErrorStringCallback(e.getMessage(),callback);
                }
            }
        });
    }


    //同步返回的
    public static void deliveryResult(final ApiCallback callback, Response execute) {
        try {
            String str = execute.body().string();
            if(StringUtils.isNotEmpty(str)){
                JSONObject result = JSON.parseObject(str);
                if(Integer.parseInt(result.getString("code"))>200&& Integer.parseInt(result.getString("code"))<300){
                    sendSuccessApiCallback(result, callback);
                }else{
                    sendFailedStringCallback(result, callback);

                }
            }
            else{
                sendNetErrorStringCallback(execute.message(),callback);
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
    }



    //回调

    public static void sendSuccessApiCallback(final JSONObject object, final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onDataSuccess(object);
                }
            }
        });
    }

    public static void sendFailedStringCallback(final JSONObject object, final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onDataError(object);
            }
        });
    }

    public static void sendNetErrorStringCallback(final String message, final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onNetError(message);
            }
        });
    }



    public static void sendReStartCallback(final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                setIsLogin();
//                Intent intent = new Intent(cxt, LoginActivity.class);
//                cxt.startActivity(intent);
            }
        });
    }

    public static void setIsLogin() {
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putBoolean("login", false);
//        editor.commit();
    }



    public static class Param {
        String key;
        String value;
        public Param() {
        }
        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    public static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


    //-----
    public  static final okhttp3.MediaType MEDIA_TYPE_PNG = okhttp3.MediaType.parse("image/png");
    public static final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    /**
     * okhttp3多图上传
     * @param base_url
     * @param mImgUrls
     * @param params
     * @param field
     */
    public void uploadImg(String base_url, List<String> mImgUrls, List<Param> params, String field, final ApiCallback callback) {

        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i <mImgUrls.size() ; i++) {
            File f=new File(mImgUrls.get(i));
            if (f!=null) {
                builder.addFormDataPart(field, f.getName(), okhttp3.RequestBody.create(MEDIA_TYPE_PNG, f));
            }
        }
        //添加其它信息
        for (Param param : params) {
            builder.addFormDataPart(param.key,param.value);
        }


        MultipartBody requestBody = builder.build();
        //构建请求
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(base_url)//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

                LogUtils.i("上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage());

                if(StringUtils.isNotEmpty(e.getMessage())) {

                    if (e.getMessage().contains("No address associated with hostname")) {
                        sendNetErrorStringCallback("网络异常,请检查网络", callback);
                    } else {
                        sendNetErrorStringCallback(e.getMessage(),callback);

                    }
                }

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                try {
                    String str = response.body().string();
                    LogUtils.i("后台响应：" + response.message() + "--" + response.code() + "后台返回数据：" + str);

                    if(StringUtils.isNotEmpty(str)){

                        JSONObject result = JSON.parseObject(str);
                        if (Integer.parseInt(result.getString("code")) >= 200 && Integer.parseInt(result.getString("code")) < 300) {
                            sendSuccessApiCallback(result, callback);
                        } else {
                            sendFailedStringCallback(result, callback);
                        }

                    }else{

                        sendNetErrorStringCallback(response.message() + "-code:" + response.code(),callback);
                    }

                } catch (final Exception e) {

                    sendNetErrorStringCallback(e.getMessage(),callback);
                }
            }

        });

    }


    //
    /**
     * 参与get请求的url参数拼接
     * @param map
     * @param sign
     * @return
     */
    public static String getpParameter(Map<String,String> map, String sign){
        StringBuilder buffer = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            buffer.append("&").append(entry.getKey()).append("=").append(
                    entry.getValue()!= null && entry.getValue().length()
                            > 0 ? entry.getValue() : "");
        }
        String parameterString = buffer.toString();
        String s2="";
        if(parameterString.length()>0){
            s2 = parameterString.substring(1,parameterString.length());
            s2+= "&sign="+ sign +"&publicKey="+ AppConfig.PUBLICKEY;
        }
        LogUtils.i("请求参数："+s2);
        return s2;
    }


    /**
     * 参与put，post请求的，参数list
     * @param map
     * @param sign
     * @return
     */
    public static List<Param> getParameter(Map<String, String> map, String sign){

        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        List<Param> params = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            params.add(new OkhttpUtils.Param(entry.getKey(),entry.getValue()!= null && entry.getValue().length()
                    > 0 ? entry.getValue() : ""));
        }
        params.add(new OkhttpUtils.Param("sign", sign));
        params.add(new OkhttpUtils.Param("publicKey", AppConfig.PUBLICKEY));

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            buffer.append("&").append(params.get(i).key).append("=").append(
                    params.get(i).value!= null && params.get(i).value.length()
                            > 0 ? params.get(i).value : "");
        }
        LogUtils.i("请求参数："+buffer.toString());

        return params;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }




}
