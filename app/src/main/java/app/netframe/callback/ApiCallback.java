package app.netframe.callback;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by fengyongge on 16/5/12.
 */
public interface ApiCallback {

    public void onDataSuccess(JSONObject data);
    public void onDataError(JSONObject data);
    public void onNetError(String data);
}
