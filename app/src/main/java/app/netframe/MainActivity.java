package app.netframe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import app.netframe.bean.LoginBean;
import app.netframe.callback.ApiCallback;

/**
 * Created by fengyongge on 16/5/12.
 */
public class MainActivity extends AppCompatActivity {


    private EditText et_username,et_pass;
    private Button bt_login;
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

    }


    public void initView(){
        et_username = (EditText) findViewById(R.id.et_username);
        et_pass = (EditText) findViewById(R.id.et_pass);
        bt_login= (Button) findViewById(R.id.bt_login);
    }



    public void loadMore(String username,String password){
        MyNet.Inst().Login(MainActivity.this, username, password, new ApiCallback() {
            @Override
            public void onDataSuccess(JSONObject data) {
                Log.i("fyg","data:"+data);
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
