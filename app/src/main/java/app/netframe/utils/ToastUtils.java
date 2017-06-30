package app.netframe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author fengyongge
 * @Description  toast
 */


public class ToastUtils {

	public static Toast toast;

	/**
	 * function 防止多次弹toast
	 * author fengyongge
	 * @param context
	 * @param content
	 */
	public static void showToast(Context context, String content){
		if(toast==null){
			toast = Toast.makeText(context,content, Toast.LENGTH_SHORT);
		}else{
			toast.setText(content);
		}
		toast.show();
	}


}