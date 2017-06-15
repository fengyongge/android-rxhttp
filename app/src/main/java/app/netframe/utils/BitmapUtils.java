package app.netframe.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;



import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by fengyongge on 2016/05/20.
 */
public class BitmapUtils {
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 加载picture
     *
     * @param data
     * @param context
     * @return
     */
    public static Bitmap getFromPhoto(Intent data, Context context) {
        Uri uri = data.getData();
        String[] filePath = new String[]{MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePath, null, null, null);
        Bitmap b = null;
        if (cursor.moveToFirst()) {
            String picturePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, option);//预加载
            option.inSampleSize = computeSampleSize(option, -1, 256 * 256);
            option.inJustDecodeBounds = false;
            b = BitmapFactory.decodeFile(picturePath, option);//加载

        }
        return Bitmap.createBitmap(b);
    }

    /**
     * carame 取照片
     *
     * @param data
     * @return
     */
    public static Bitmap getFromCarame(Intent data) {
        return (Bitmap) data.getExtras().get("data");
    }

    /**
     * @param bitmap
     * @return
     */
    public static String bitmap2base64(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();
        } catch (IOException e) {

        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Base64.encode(baos.toByteArray(), Base64.DEFAULT) == null)
            return "";

        return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
    }

    /**
     * @param s
     * @return
     */
    public static Bitmap base642bitmap(String s) {
        byte[] bytes = Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }


}
