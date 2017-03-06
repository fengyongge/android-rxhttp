package app.netframe;

/**
 * @author fengyongge
 * @date 2017/3/6 0006
 * @description
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import app.netframe.app.MyApp;
import app.netframe.callback.ApiCallback;
import app.netframe.util.MD5;
import app.netframe.util.StringUtils;

import static app.netframe.MyNet.device;
import static app.netframe.MyNet.version;


/**
 * @author fengyongge
 * @date 2017/1/3 0003
 * @description
 * java提供的api
 */
public class MyNet1 {
    public final static String NET_DOMAIN ="http://api.ediankai.com/dkapiv2.php";

    private String appSecret="app123!@#";
    private String publicKey="app123456";
    private static MyNet1 mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private static SharedPreferences sp;
    private static MyApp myApp;
    private static Context cxt;



    private MyNet1() {
        mOkHttpClient = new OkHttpClient();

        mOkHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static MyNet1 Inst(Context context) {

        if (mInstance == null) {
            synchronized (MyNet1.class) {
                if (mInstance == null) {
                    mInstance = new MyNet1();
                }
            }
        }
        cxt = context;
        if (sp == null) {
            myApp = (MyApp) context.getApplicationContext();
            sp = myApp.getMustElement();
        }

//        staff_id = myApp.getStaff_id();
//        supplier_id = myApp.getSupplier_id();
//        Logger.i("supplier_id",supplier_id);
        return mInstance;
    }


    // 登录
    public void Login(final Context context, final String mobile,
                      final String password, final ApiCallback callback) {
        String noncestr = getNoncestr();
        String time = getTime();
        Map<String, String> map = new TreeMap<String, String>();
        map.put("username", mobile);
        map.put("password", password);

        map.put("device", device);
        map.put("version", version);
        map.put("time", time);
        map.put("noncestr", noncestr);
        map.put("c", "login");

        String sortString = sort(map);
        String sign = toMD5(sortString);
        final String apiUri = NET_DOMAIN + "?c=login"+ "&sign=" + sign;
        List<Param> params = new ArrayList<Param>();
        params= getParameter(map,sign);
        _postAsyn(apiUri, callback, params);
    }


    /**
     * 个人信息-修改头像
     * @param pic
     * @param token
     * @param merchant_id
     * @param callback
     */
    public void updateLogo(File pic, String token, String merchant_id, final ApiCallback callback) {

        String noncestr = getNoncestr();
        String time = getTime();
        Map<String, String> map = new TreeMap<String, String>();
        map.put("version", version);
        map.put("merchant_id", merchant_id);
        map.put("time", time);
        map.put("noncestr", noncestr);
        map.put("token", token);
        map.put("c", "useredit");
        String sortString = sort(token, map);
        String sign = toMD5(sortString);
        final String apiUri = NET_DOMAIN +"?c=useredit" + "&sign=" + sign;
        List<Param> params = new ArrayList<>();
        try {
            _postAsyn(apiUri, callback, pic, "portrait", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //----------------------------------------------



    /**
     * 参与get请求的url参数拼接
     * @param map
     * @param sign
     * @return
     */
    public String getpParameter(Map<String,String> map,String sign){
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
            s2+= "&sign="+ sign;
//            s2+= "&sign="+ sign +"&publicKey="+publicKey;
        }
        return s2;
    }


    /**
     * 参与put，post，delete请求的，参数list
     * @param map
     * @param sign
     * @return
     */
    public List<Param>  getParameter(Map<String,String> map,String sign){

        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        List<Param> params = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            params.add(new Param(entry.getKey(),entry.getValue()!= null && entry.getValue().length()
                    > 0 ? entry.getValue() : ""));
        }
        params.add(new Param("sign", sign));
//        params.add(new Param("publicKey", publicKey));
        return params;
    }




    //--------------------------加密规则-------------------------------------------

    // 给参数按字母顺序排序(头尾加参数)
    public final static String sort(String token, Map<String, String> map) {
        Iterator<String> iter = map.keySet().iterator();
        String s = token;
        while (iter.hasNext()) {
            Object key = iter.next();
            StringBuffer sb = new StringBuffer();
            s += sb.append(key + map.get(key));
        }
        String ss = s + token;
        return ss;
    }

    // 给参数按字母顺序排序
    public final static String sort(Map<String, String> map) {
        Iterator<String> iter = map.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (iter.hasNext()) {
            Object key = iter.next();
            sb.append(key + map.get(key));
        }
        return sb.toString();
    }

    public String toMD5(String or_Sign) {
//            sign = MD5.getMessageDigest(or_Sign.getBytes("UTF-8"));
        String  sign = MD5.getMessageDigest(or_Sign.getBytes()).toUpperCase();

        return sign;
    }


    //--------------------------------okhttp功能模块-----------------------------------------



    /**
     * put方式
     * @param url
     * @param callback
     * @param params
     */
    private void _putAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildPutRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * delete方式
     * @param url
     * @param callback
     * @param params
     */
    private void _deleteAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildDeleteRequest(url, params);
        deliveryResult(callback, request);
    }


    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private void _getSynchronization(String url, final ApiCallback callback)
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
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final ApiCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }


    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ApiCallback callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }



    /**
     * 同步基于post的文件上传
     *
     * @param params
     *
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
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
    private void _postAsyn(String url, ApiCallback callback, File[] files, String[] fileKeys, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
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
    private void _postAsyn(String url, ApiCallback callback, File file, String fileKey) throws IOException {
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
    private void _postAsyn(String url, ApiCallback callback, File file, String fileKey, List<Param> params) throws IOException {

//        apiUri, callback, pic, "header_img", params
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * put上传文件
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */

    private void _putAsyn(String url, ApiCallback callback, File file, String fileKey, List<Param> params) throws IOException {
        Request request = buildMultipartFormRequest1(url, new File[]{file}, new String[]{fileKey}, params);
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
        final com.squareup.okhttp.Call call = mOkHttpClient.newCall(request);
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
//                try
//                {
//                    is = response.body().byteStream();
//                    File file = new File(destFileDir, getFileName(url));
//                    fos = new FileOutputStream(file);
//                    while ((len = is.read(buf)) != -1)
//                    {
//                        fos.write(buf, 0, len);
//                    }
//                    fos.flush();
//                    //如果下载文件成功，第一个参数为文件的绝对路径
//                    sendSuccessApiCallback(file.getAbsolutePath(), callback);
//                } catch (IOException e)
//                {
//                    sendFailedStringCallback(response.request(), e, callback);
//                } finally
//                {
//                    try
//                    {
//                        if (is != null) is.close();
//                    } catch (IOException e)
//                    {
//                    }
//                    try
//                    {
//                        if (fos != null) fos.close();
//                    } catch (IOException e)
//                    {
//                    }
//                }
            }
        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


//    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException
//    {
//        return getInstance()._post(url, files, fileKeys, params);
//    }
//
//    public static Response post(String url, File file, String fileKey) throws IOException
//    {
//        return getInstance()._post(url, file, fileKey);
//    }
//
//    public static Response post(String url, File file, String fileKey, Param... params) throws IOException
//    {
//        return getInstance()._post(url, file, fileKey, params);
//    }
//
//    public static void postAsyn(String url, ApiCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException
//    {
//        getInstance()._postAsyn(url, callback, files, fileKeys, params);
//    }
//
//
//    public static void postAsyn(String url, ApiCallback callback, File file, String fileKey) throws IOException
//    {
//        getInstance()._postAsyn(url, callback, file, fileKey);
//    }
//
//
//    public static void postAsyn(String url, ApiCallback callback, File file, String fileKey, Param... params) throws IOException
//    {
//        getInstance()._postAsyn(url, callback, file, fileKey, params);
//    }


    //****************************


    private Request buildMultipartFormRequest1(String url, File[] files,
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
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
    }




    private Request buildMultipartFormRequest(String url, File[] files,
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

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private void deliveryResult(final ApiCallback callback, Request request) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                if(StringUtils.isNotEmpty(e.getMessage())){
                    sendNetErrorStringCallback(e.getMessage(),callback);
                }else{
                    sendNetErrorStringCallback(callback);
                }

            }

            @Override
            public void onResponse(final Response response) {
                try {
                    String str = response.body().string();

                    Log.i("fyg","str:"+str);

                    JSONObject result = JSON.parseObject(str);

                    //如果后台报错，result对象为null
                    if(result!=null){
                        if(Integer.parseInt(result.getString("code"))==0||Integer.parseInt(result.getString("code"))>=200&&Integer.parseInt(result.getString("code"))<300){
                            sendSuccessApiCallback(result, callback);
                        }else{
                            sendFailedStringCallback(result, callback);
                        }
                    }
                    else{
                        sendNetErrorStringCallback(response.message()+"-code:"+response.code(),callback);
                    }

                } catch (final Exception e) {

                    sendNetErrorStringCallback(callback);
                }

            }
        });
    }



    //同步返回的
    private void deliveryResult(final ApiCallback callback, Response execute) {
        try {
            String str = execute.body().string();
            JSONObject result = JSON.parseObject(str);

            if(result!=null){
                if(Integer.parseInt(result.getString("code"))>200&&Integer.parseInt(result.getString("code"))<300){
                    sendSuccessApiCallback(result, callback);
                }else{
                    sendReStartCallback(callback);
                }
            }else{
                sendFailedStringCallback(result, callback);
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void sendSuccessApiCallback(final JSONObject object, final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onDataSuccess(object);
                }
            }
        });
    }

    private void sendFailedStringCallback(final JSONObject object, final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onDataError(object);
            }
        });
    }

    private void sendNetErrorStringCallback(final String message,final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onNetError(message);
            }
        });
    }

    private void sendNetErrorStringCallback(final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)

                    callback.onNetError("网络异常");
            }
        });
    }

    private void sendReStartCallback(final ApiCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                setIsLogin();
                Intent intent = new Intent(cxt, ShopMainActivity.class);
                cxt.startActivity(intent);
            }
        });
    }


    private Request buildPostRequest(String url, List<Param> params) {

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


    private Request buildPutRequest(String url, List<Param> params) {

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

    private Request buildDeleteRequest(String url, List<Param> params) {

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
    private void setIsLogin() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("login", false);
        editor.commit();
    }

    private String getTime() {

        return System.currentTimeMillis() / 1000 + "";
    }

    private String getNoncestr() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "").trim();
    }

}

