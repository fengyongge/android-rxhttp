package app.netframe.util;

import java.security.MessageDigest;

public class MD5 {

	private MD5() {}
	
	public final static String getMessageDigest(byte[] buffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(buffer);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
		md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
		e.printStackTrace();
		return "";
		}
		 
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		 
		for (int i = 0; i < charArray.length; i++) {
		byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		 
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
		int val = (md5Bytes[i]) & 0xff;
		if (val < 16) {
		hexValue.append("0");
		}
		hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
		}
		 
		// 可逆的加密算法
		public static String encryptmd5(String str) {
		char[] a = str.toCharArray();
		for (int i = 0; i < a.length; i++) {
		a[i] = (char) (a[i] ^ 'l');
		}
		String s = new String(a);
		return s;
		}
		 
}
