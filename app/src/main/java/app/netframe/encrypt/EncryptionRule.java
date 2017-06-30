package app.netframe.encrypt;


import java.util.Iterator;
import java.util.Map;

import app.netframe.app.AppConfig;
import app.netframe.utils.LogUtils;

/**
 * Created by fengyongge on 2017/5/23.
 */

public class EncryptionRule {

    // 给参数按字母顺序排序(头尾加参数)
    public final static String encryption(String token, Map<String, String> map) {
        Iterator<String> iter = map.keySet().iterator();
        String s = token;
        while (iter.hasNext()) {
            Object key = iter.next();
            StringBuffer sb = new StringBuffer();
            s += sb.append(key + map.get(key));
        }

        String content = s + token;
        content = toMD5(content);
        LogUtils.i("签名之前："+(s + token)+"\n签名后："+content);
        return content;
    }

    // 给参数按字母顺序排序
    public final static String encryption(Map<String, String> map) {
        Iterator<String> iter = map.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (iter.hasNext()) {
            Object key = iter.next();
            sb.append(key + map.get(key));
        }
        return sb.toString();
    }



    public static String toMD5(String or_Sign)  {
        String sign = null;
        sign = Sha1.shaEncrypt(or_Sign);
        return sign;
    }


    /**
     * 参与get请求的url参数拼接
     * @param map
     * @param sign
     * @return
     */
    public static String test(Map<String,String> map, String sign){
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
        return s2;
    }
}
