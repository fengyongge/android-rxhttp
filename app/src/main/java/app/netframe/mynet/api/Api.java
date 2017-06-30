package app.netframe.mynet.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import app.netframe.app.AppConfig;
import app.netframe.app.MyApp;
import app.netframe.bean.LoginBean;
import app.netframe.callback.ApiCallback;
import app.netframe.encrypt.Base64;
import app.netframe.encrypt.EncryptionRule;
import app.netframe.mynet.OkhttpUtils;
import app.netframe.utils.PreferencesUtils;

import static app.netframe.mynet.OkhttpUtils.getParameter;

/**
 * Created by fengyongge on 2017/5/23.
 */

public class Api {

    private static Api mInstance;
    private static SharedPreferences sp;
    private static MyApp myApp;
    private static Context cxt;
    private static String staff_id="",supplier_id="";

    public static String version = "A-2.6.2";
    public static String device = android.os.Build.MODEL;



    public static Api Inst(Context context) {

        if (mInstance == null) {
            synchronized (OkhttpUtils.class) {
                if (mInstance == null) {

                    mInstance = new Api();
                }
            }
        }

        cxt = context;
        staff_id= PreferencesUtils.getString(cxt,"staff_id");
        supplier_id= PreferencesUtils.getString(cxt,"supplier_id");

//        if(OkhttpUtils.isNetworkAvailable(context)){
//            ToastOkhttpUtils.showToast(context,"网络异常");
//        }

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

        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);
        final String apiUri = AppConfig.BASE_URL + "?c=login";
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);
        OkhttpUtils.getInstance()._postAsyn(apiUri, callback, params);
    }


    /**
     * 个人信息-修改头像
     *
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
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);

        final String apiUri = AppConfig.BASE_URL + "?c=useredit" + "&sign=" + sign;
        try {
            OkhttpUtils.getInstance()._postAsyn(apiUri, callback, pic, "header_img");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 登录接口
     * @param mobile
     * @param password
     * @param callback
     */
    public void Login(final String mobile, final String password, final ApiCallback callback) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        map.put("mobile", mobile);
        map.put("password", password);
        map.put("visitSource", "1");
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);

        String method = "staffservice/login";
        final String apiUri = AppConfig.BASE_URL +  method;
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);

        OkhttpUtils.getInstance()._postAsyn(apiUri, callback, params);

    }

    /**
     * 获取标签列表
     */
    public void getLabels(final ApiCallback callback) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);
        String s = OkhttpUtils.getpParameter(map,sign);

        String method = "memberservice/queryStaffTag/suppliers/" + supplier_id + "/operator/" + staff_id + "?"+s;
        final String apiUri = AppConfig.BASE_URL + method;
        OkhttpUtils.getInstance()._getAsyn(apiUri, callback);
    }

    /**
     * 删除标签
     */
    public void deleteTag(String tag_id, final ApiCallback callback) {

        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        map.put("supplier_id", supplier_id);
        map.put("operator_id", staff_id);
        map.put("tagids", tag_id);
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);

        String method = "memberservice/delMemberTag/suppliers/" + supplier_id + "/operator/" + staff_id;
        final String apiUri = AppConfig.BASE_URL + method;

        OkhttpUtils.getInstance()._deleteAsyn(apiUri, callback, params);
    }

    /**
     * 修改标签
     */
    public void updateMemberTag(final String name, String tag_id, final ApiCallback callback) {

        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        map.put("supplier_id", supplier_id);
        map.put("operator_id", staff_id);
        map.put("tagid", tag_id);
        map.put("name", name);
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);

        String method = "memberservice/updateMemberTag/suppliers/" + supplier_id + "/operator/" + staff_id;
        final String apiUri = AppConfig.BASE_URL + method;
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);
        OkhttpUtils.getInstance()._putAsyn(apiUri, callback, params);
    }


    /**
     * 添加标签
     */
    public void addMemberTag(final String name, final ApiCallback callback) {

        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        map.put("name", name);
        map.put("type", "2");
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);


        String method = "memberservice/addMemberTag/suppliers/" + supplier_id + "/operator/" + staff_id;
        final String apiUri = AppConfig.BASE_URL + method;
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);
        OkhttpUtils.getInstance()._postAsyn(apiUri, callback, params);
    }

    /**
     * 更新接口
     * @param callback
     */
    public void update(final ApiCallback callback) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", getTime());
        map.put("type", "android");
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);
        String s = OkhttpUtils.getpParameter(map,sign);

        String method = "apk?"+s;
        final String apiUri = AppConfig.BASE_URL + method;
        OkhttpUtils.getInstance()._getAsyn(apiUri, callback);
    }



    public void meetingASssistant(int pageIndex,final ApiCallback callback) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp",getTime());
        map.put("page",pageIndex+"");
        map.put("per_page","20");
        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);

        String s = OkhttpUtils.getpParameter(map,sign);
        String method ="meeting/assistant?"+s;
        final String apiUri = AppConfig.BASE_URL +  method;
        OkhttpUtils.getInstance()._getAsyn(apiUri, callback);
    }


    /**
     * 个人信息-修改头像
     *
     * @param pic
     * @param callback
     */
    public void updateLogo(File pic, final ApiCallback callback) {

        String method = "staffs/" + staff_id;
        final String apiUri = AppConfig.BASE_URL + method;
        try {
            OkhttpUtils.getInstance()._postAsyn(apiUri, callback, pic, "header_img");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 上传会议总结
     * @param meeting_id
     * @param formatted_address
     * @param mImgUrls
     * @param callback
     */
    public void uploadePics(String meeting_id,String formatted_address,
                            List<String> mImgUrls, String field, ApiCallback callback) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("timestamp",getTime());
        map.put("supplier_id", supplier_id);
        map.put("staff_id", staff_id);
        map.put("meeting_id", meeting_id);
        map.put("summary_position", formatted_address);

        String sign = EncryptionRule.encryption(AppConfig.APPSECRET, map);
        List<OkhttpUtils.Param> params = new ArrayList<OkhttpUtils.Param>();
        params = getParameter(map,sign);

        String method ="suppliers/"+supplier_id+"/staff/"+staff_id+"/meeting/"+meeting_id+"/upload";
        String base_url = AppConfig.BASE_URL +  method;
        OkhttpUtils.getInstance().uploadImg(base_url,mImgUrls,params,field,callback);
    }


    /**
     * json作为参数，明文的话转义{}，否则base64加密
     */
    LoginBean loginBean = new LoginBean();
    String products = Base64.Instance().encrypt(JSON.toJSONString(loginBean));
    String scene = loginBean.toString().replace("\"", "%22").replace("{", "%7b").replace("}", "%7d");


    private String getNoncestr() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "").trim();
    }

    private String getTime() {
        return System.currentTimeMillis() + "";
    }
}
