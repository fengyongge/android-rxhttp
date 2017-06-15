package app.netframe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.netframe.bean.LoginBean;
import app.netframe.bean.UrlBean;
import app.netframe.mynet.MyNet;
import app.netframe.utils.ImageUtils;
import app.netframe.utils.UploadHelper;

/**
 * Created by fengyongge on 16/5/12.
 */

public class ShopMainActivity extends AppCompatActivity {

    private TextView tv_name;
    private ImageView iv_logo;
    private AlertDialog alertDialog;
    private String bitmap_url;
    UrlBean headBean;
    LoginBean user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            final JSONObject object = (JSONObject) msg.obj;
            try {
                if (object.getString("code").trim().equals("0")) {
                        ImageLoader.getInstance().displayImage(bitmap_url, iv_logo);
                    Toast.makeText(ShopMainActivity.this,object.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
            } catch (Exception e) {
                // TODO: handle exception
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headImg();
            }
        });
    }

    // 换头像
    private void goUploadhead() {
        headBean = MyNet.Inst().useredit(ShopMainActivity.this, user.getToken(), user.getMerchant_id());
        new Thread() {
            private Map<String, String> files;
            @Override
            public void run() {
                files = new HashMap<String, String>();
                files.put("portrait", bitmap_url);

                final Map<String, String> map = new HashMap<String, String>();
                map.put("version", headBean.getVersion());
                map.put("time", headBean.getTime());
                map.put("noncestr", headBean.getNoncestr());
                map.put("merchant_id", user.getMerchant_id());
                map.put("token", user.getToken());

                try {
                    JSONObject result;
                    result = UploadHelper.postFile(headBean.getApiUri(), map, files);
                    Log.i("fyg","头像" + result);
                    Message message = new Message();
                    message.obj = result;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    // TODO Auto-generated catch block

                    e.printStackTrace();
                }

            }
        }.start();
    }








    private void headImg() {
        List<String> list = new ArrayList<>();
        View vv = LayoutInflater.from(this).inflate(R.layout.item_dialog_title, null);
        list.add("相册");
        list.add("相机");
        alertDialog = new AlertDialog.Builder(ShopMainActivity.this).setCustomTitle(vv)
                .setAdapter(new ArrayAdapter<String>(this, R.layout.select_dialog, R.id.tv_carame, list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showChoosePicture();
                } else if (which == 1) {
                    showTakePicture();
                }
            }
        }).create();
        alertDialog.show();
    }


    private void showTakePicture() {
        ImageUtils.imageUriFromCamera = ImageUtils.createImagePathUri(ShopMainActivity.this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
        // 返回图片在onActivityResult中通过以下代码获取
        // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
        startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);

    }

    private void showChoosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 从相册获取
            case 1:
                if (data != null) {
                    ImageUtils.cropImage(this, data.getData(), false);
                }
                break;
            // 调用相机拍照时
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (ImageUtils.imageUriFromCamera != null && resultCode == -1) {
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera,false);
                }
                break;
            // 取得裁剪后的图片
            case 3:
                break;
            case ImageUtils.CROP_IMAGE:
                if (ImageUtils.cropImageUri != null && data != null
                        && resultCode == -1) {
                      bitmap_url = getAbsoluteImagePath(ImageUtils.cropImageUri);
                      ImageLoader.getInstance().displayImage("file://" + bitmap_url, iv_logo);
                    }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * URI转绝对路径
     * @param uri
     * @return
     */
    public String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * 压缩图片 防止oom
     * @param srcPath
     * @return
     */
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {
        Bitmap c_bitmap = null ;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            c_bitmap = BitmapFactory.decodeStream(isBm, null, null);
            return c_bitmap;
        } catch (Exception e) {
            return c_bitmap;
        }

    }


}
