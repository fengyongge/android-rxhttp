package app.netframe.utils;

import android.widget.TextView;

import java.util.List;

/**
 * @author fengyongge
 * @Description  过滤null，显示toast
 */


public class StringUtils {
	public static final String EMPTY = "";
	public static final int INDEX_NOT_FOUND = -1;
	private static final int PAD_LIMIT = 8192;

	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}
	public static boolean isNotEmpty(List<String> cs) {
		if (cs == null) {
			
			return false;
		}
		
		if (cs.size() == 0) {
					
			return false;
		}
		
		return true;
	}
	
	
	public static String getString(String cs, String name) {
		if (isEmpty(cs)) {
			return name+" ";
		}else {
			
			return "";
		}
	}
	public static String getListString(List<String> cs, String name) {
		if (cs == null) {
			return name+" ";
		}
		if (cs.size() == 0) {
			return name+" ";
		}
		else {
			
			return "";
		}
	}



	/**
	 * function 给控件设置一个可以为null的值
	 * author fengyongge
	 * @param textView
	 * @param s
	 */
	public static void filtNull(TextView textView, String s) {
		if (s != null) {
			textView.setText(s);
		} else {
			textView.setText(filtNull(s));
		}
	}

	/**
	 * function 判断过滤单个string为null
	 * author fengyongge
	 * @param s
	 * @return
	 */
	public static String filtNull(String s) {
		if (s!=null) {
			return  s;
		} else {
			s="null";
		}
		return  s;
	}




}