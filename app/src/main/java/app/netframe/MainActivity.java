package app.netframe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import app.netframe.bean.LoginBean;
import app.netframe.callback.ApiCallback;
import app.netframe.mynet.api.Api;
import app.netframe.ui.ShopMainActivity;

/**
 * Created by fengyongge on 16/5/12.
 */
public class MainActivity extends AppCompatActivity {


    private EditText et_username,et_pass;
    private Button bt_login,bt_upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMore(et_username.getText().toString(),et_pass.getText().toString());
            }
        });
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this,ShopMainActivity.class);
                startActivity(intent);
            }
        });

    }


    public void initView(){
        et_username = (EditText) findViewById(R.id.et_username);
        et_pass = (EditText) findViewById(R.id.et_pass);
        bt_login= (Button) findViewById(R.id.bt_login);
        bt_upload= (Button) findViewById(R.id.bt_upload);
    }



    public void loadMore(String username,String password){
        Api.Inst(MainActivity.this).Login(MainActivity.this, username, password, new ApiCallback() {
            @Override
            public void onDataSuccess(JSONObject data) {
                String code =  data.getString("code");
                LoginBean loginBean = JSON.parseObject(data.getString("data"),LoginBean.class);
                if(code.equals("0")){
                    Intent intent = new Intent(MainActivity.this,ShopMainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean",loginBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,data.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataError(com.alibaba.fastjson.JSONObject data) {
                Toast.makeText(MainActivity.this,data.getString("msg"),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetError(String data) {

            }
        });
    }





}
