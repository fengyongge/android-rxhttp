package app.netframe;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import app.netframe.bean.UrlBean;
import app.netframe.callback.ApiCallback;
import app.netframe.util.MD5;
import app.netframe.util.NetUtil;

/**
 * Created by fengyongge on 16/5/12.
 */

    public class MyNet {
        private static MyNet _instance = null;
        public final static String NET_DOMAIN ="http://api.ediankai.com/dkapiv2.php";
        public final static int DATA_SUCCESS = 10;
        public final static int DATA_ERROR = 11;
        public final static int NET_ERROR = 12;
        public static String device = android.os.Build.MODEL;
        public static String version = "A-1.1.0";
        private com.alibaba.fastjson.JSONObject jsonObject;
        private String strUrl;

        private MyNet() {

        }

        public static MyNet Inst() {
            if (_instance == null) {
                _instance = new MyNet();
            }
            return _instance;
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
        final String apiUri = NET_DOMAIN + "?c=login" + "&sign=" + sign;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", mobile));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("device", device));
        params.add(new BasicNameValuePair("version", version));
        params.add(new BasicNameValuePair("time", time));
        params.add(new BasicNameValuePair("noncestr", noncestr));
        commRequest(context, apiUri, params, callback);
    }


    public UrlBean useredit(final Context context, String token,
                            String merchant_id) {
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
        final String apiUri = NET_DOMAIN + "?c=useredit" + "&sign=" + sign;
        return new UrlBean(apiUri, noncestr, time, version);
    }



    public void commRequest(final Context ctx, String apiUri,
                            List<NameValuePair> params, final ApiCallback callback) {
        Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == DATA_SUCCESS) {

                    callback.onDataSuccess((JSONObject) msg.obj);
                } else if (msg.arg1 == DATA_ERROR) {
                    if (jsonObject.getString("code").trim().equals("403")) {

                        //判断重新登录

                    } else {
                        callback.onDataError((JSONObject) msg.obj);
                    }

                } else if (msg.arg1 == NET_ERROR) {
                    callback.onNetError("网络不好,请查看");
                }
                return true;
            }

        });

        request(ctx, apiUri, params, handler);
    }

    private void request(final Context ctx, final String apiUri,
                         final List<NameValuePair> params, Handler handler) {
        final Messenger messager = new Messenger(handler);
        final Message message = Message.obtain();
        new Thread() {
            @Override
            public void run() {

                try {
                    jsonObject = NetUtil
                            .getResponseForPost(apiUri, params, ctx);

                    if (jsonObject != null
                            && jsonObject.toString().contains("code")) {
                        if (jsonObject.getString("code").equals("0")) {
                            message.obj = jsonObject;
                            message.arg1 = DATA_SUCCESS;

                        } else {
                            message.obj = jsonObject;
                            message.arg1 = DATA_ERROR;

                        }
                        messager.send(message);

                    } else {

                        message.arg1 = NET_ERROR;
                        messager.send(message);

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            };
        }.start();

    }

    public String toMD5(String or_Sign) {

        String sign = MD5.getMessageDigest(or_Sign.getBytes()).toUpperCase();
        return sign;
    }

    // 给参数按字母顺序排序
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

    private String getTime() {
        return System.currentTimeMillis() / 1000 + "";
    }

    private String getNoncestr() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "").trim();
    }

}


