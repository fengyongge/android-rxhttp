package app.netframe.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fengyongge on 2017/3/16.
 */

public class Sha1 {

    /**
     * SHA加密
     *
     * @param strSrc
     *            明文
     * @return 加密之后的密文
     */
    public static String shaEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = new byte[0];
        try {
            bt = strSrc.toString().getBytes("UTF-8");
            try {
                md = MessageDigest.getInstance("SHA1");// 将此换成SHA-1、SHA-512、SHA-384等参数
                byte[] thedigest = md.digest(bt);
                strDes = bytesToHexString(thedigest); // to HexString
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//            md.update(bt);

        return strDes;
    }



//
//     combineString.append(appSecret);
//    byte[] bytesOfMessage = combineString.toString().getBytes("UTF-8");
//    //   System.out.println( combineString );
//    MessageDigest md = MessageDigest.getInstance("SHA1");
//    byte[] thedigest = md.digest(bytesOfMessage);
//    String signature = bytesToHexString(thedigest);
//   return signature;


    private static String bytesToHexString(byte[] src) {
        try {
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                return null;
            }
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return null;
        }
    }






//
//    /**
//     * byte数组转换为16进制字符串
//     *
//     * @param bts
//     *            数据源
//     * @return 16进制字符串
//     */
//    public static String bytes2Hex(byte[] bts) {
//        String des = "";
//        String tmp = null;
//        for (int i = 0; i < bts.length; i++) {
//            tmp = (Integer.toHexString(bts[i] & 0xFF));
//            if (tmp.length() == 1) {
//                des += "0";
//            }
//            des += tmp;
//        }
//        return des;
//    }







//    public static String shaEncrypt(String info) {
//        byte[] digesta = null;
//        try {
//            MessageDigest alga = MessageDigest.getInstance("SHA-1");
//            try {
//                alga.update(info.getBytes("UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            digesta = alga.digest();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        String rs = bytesToHexString(digesta);
//        return rs;
//    }

//    public static String byte2hex(byte[] b) {
//        String hs = "";
//        String stmp = "";
//        for (int n = 0; n < b.length; n++) {
//            stmp = (Integer.toHexString(b[n] & 0XFF));
//            if (stmp.length() == 1) {
//                hs = hs + "0" + stmp;
//            } else {
//                hs = hs + stmp;
//            }
//        }
//        return hs;
//    }





}
