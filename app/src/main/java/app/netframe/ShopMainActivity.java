package app.netframe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.netframe.bean.LoginBean;
import app.netframe.bean.UrlBean;
import app.netframe.util.ImageUtils;
import app.netframe.util.UploadHelper;

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
        Intent intent = this.getIntent();
        user=(LoginBean)intent.getSerializableExtra("bean");
        tv_name.setText(user.getUsername());
        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headImg();
            }
        });
    }

    // 换头像
    private void goUploadhead() {
        Log.i("fyg","1111");
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



    // URI转绝对路径
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


    private void showTakePicture() {
        ImageUtils.imageUriFromCamera = ImageUtils
                .createImagePathUri(ShopMainActivity.this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
        // 返回图片在onActivityResult中通过以下代码获取
        // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
        startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);

    }

    private void showChoosePicture() {

        Intent intent = new Intent(Intent.ACTION_PICK, null);

        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, 1);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    ImageUtils.cropImage(this, data.getData(), false);
                }
                break;
            // 如果是调用相机拍照时
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
                    Log.i("fyg","--bitmap_url-" + bitmap_url);
                    goUploadhead();
                }
                break;

            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
