package app.netframe.mynet;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by fengyongge on 16/5/12.
 */

public class MyNetUtils {
    private static final String TAG = "MyNetUtils";
  
    /** 
     * 网络连接是否可用 
     */  
    public static boolean isConnnected(Context context) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (null != connectivityManager) {  
            NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();  
  
            if (null != networkInfo) {  
                for (NetworkInfo info : networkInfo) {  
                    if (info.getState() == NetworkInfo.State.CONNECTED) {  
                        Log.e(TAG, "the net is ok");  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;  
    }  
  
    /** 
     * 网络可用状态下，通过get方式向server端发送请求，并返回响应数据 
     *  
     * @param strUrl 请求网址
     * @param context 上下文
     * @return 响应数据
     */  
    public static JSONObject getResponseForGet(String strUrl, Context context) {
        if (isConnnected(context)) {  
            return getResponseForGet(strUrl);  
        }
        return getResponseForGet(strUrl);  
    }  
  
    /** 
     * 通过Get方式处理请求，并返回相应数据 
     *  
     * @param strUrl 请求网址
     * @return 响应的JSON数据
     */

    public static JSONObject getResponseForGet(String strUrl) {


        HttpGet httpRequest = new HttpGet(strUrl);
        
        return getRespose(httpRequest);
    }  
    public static String getWXResponseForGet(String url) { 
    	    try {
    	      HttpGet httpGet = new HttpGet(url);
    	      HttpClient client = new DefaultHttpClient();
    	      HttpResponse resp = client.execute(httpGet);
    	      
    	      HttpEntity entity = resp.getEntity();
    	      String respContent = EntityUtils.toString(entity, "UTF-8").trim();
    	      httpGet.abort();
    	      client.getConnectionManager().shutdown();

    	      return respContent;
    	      
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	      return null;
    	    }
    }  
  
    /** 
     * 网络可用状态下，通过post方式向server端发送请求，并返回响应数据 
     *  
     * @param market_uri 请求网址
     * @param nameValuePairs 参数信息
     * @param context 上下文
     * @return 响应数据
     */  
    public static JSONObject getResponseForPost(String market_uri, List<NameValuePair> nameValuePairs, Context context) {
        if (isConnnected(context)) {  
        	
        	
        	
            return getResponseForPost(market_uri, nameValuePairs);  
        }else {
        	
        	
        	return null;   
        	
        }  
        
    }  
  
    /** 
     * 通过post方式向服务器发送请求，并返回响应数据 
     *  
     * @param
     * @param nameValuePairs 参数信息
     * @return 响应数据
     */  
    public static JSONObject getResponseForPost(String market_uri, List<NameValuePair> nameValuePairs) {
        if (null == market_uri || "" == market_uri) {  
            return null;  
        }  
        try {  
        	HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
        	HttpPost request = new HttpPost(market_uri);
        	request.setEntity(entity);
//            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));  

            return getRespose(request);  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * 响应客户端请求 
     *  
     * @param request 客户端请求get/post
     * @return 响应数据
     */  
    public static JSONObject getRespose(HttpUriRequest request) {
        try {  
        	
        	
        	HttpClient client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
            
            HttpResponse httpResponse = client.execute(request);
            int statusCode = httpResponse.getStatusLine().getStatusCode();  
            if (HttpStatus.SC_OK == statusCode) {
                String result = EntityUtils.toString(httpResponse.getEntity());  
                Log.i(TAG, "results=" + result); 
                
                return JSON.parseObject(result);
            }  
        } catch (ClientProtocolException e) {
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return null;  
    }  
}  
