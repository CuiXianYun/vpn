package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.MyPreference;
import com.vm.shadowsocks.core.UserInfo;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by echo on 2018/7/16.
 */

public class LoginFragment extends Fragment implements View.OnClickListener{


    //用户注册 http://127.0.0.0/addMember
    //用户登录 http://127.0.0.0/verifyMember
    private static final String TAG = "LoginFragment";

    Context mContext;

    private EditText userName,passWord;
    private Button   btLogin,btRegister;

    OkHttpClient client = new OkHttpClient();

    private String mUser,mPwd;


    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:  //注册成功
                    showDialog();
                    Log.d(TAG, "====>>>>>注册成功！");
                    break;
                case 1:  //TODO：注册失败、需要清空EditText
                    Toast.makeText(mContext,"注册失败，请重新注册",Toast.LENGTH_LONG).show();
                    break;
                case 2: //登录成功
                    Log.d(TAG, "====>>>>>登录成功！");
                    //username和token需要持久化存储
                    UserInfo userInfo =  (UserInfo) msg.obj;
                    MyPreference.getInstance(getActivity()).SetLoginName(userInfo.getName());
                    MyPreference.getInstance(getActivity()).SetToken(userInfo.getToken());

                    //从当前的fragment跳到Activity中
                    Intent intent = new Intent(mContext,MainActivity.class);
                    startActivity(intent);
                    break;
                case 3: //登录失败
                    Toast.makeText(mContext,"登录失败，请重新登录",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fg_login,container,false);

        userName   = view.findViewById(R.id.et_username);
        passWord   = view.findViewById(R.id.et_password);
        btLogin    = view.findViewById(R.id.bt_login);
        btRegister = view.findViewById(R.id.bt_reg);



        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_login:  //用户登录
                mUser = userName.getText().toString().trim();
                mPwd  = passWord.getText().toString().trim();
                getLogin(mUser,mPwd);
                break;
            case R.id.bt_reg:    //用户注册
                mUser = userName.getText().toString().trim();
                mPwd  = passWord.getText().toString().trim();
                getRegister(mUser,mPwd);
                break;
        }
    }


    private void showDialog() {

        new AlertDialog.Builder(mContext)
                .setTitle("注册成功！")
                .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          //TODO：跳转到登录界面
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    //用户注册
    private void getRegister(String user,String pwd) {

        String url = "http://157a.com:8108/addMember?name="+user+"&password="+pwd;

        final Request request=new Request.Builder()
                .get()
                .tag(this)
                .url(url)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        Log.d(TAG, "注册json="+json);
                        try{
                            JSONObject object1 = new JSONObject(json);
                            int status  = object1.getInt("status");
                            if(status==0){
                                uiHandler.sendEmptyMessage(0);
                            }else{
                                uiHandler.sendEmptyMessage(1);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //用户登录http://127.0.0.0/verifyMember
    private void getLogin(String user,String pwd){
        String url = "http://157a.com:8108/verifyMember?name="+user+"&password="+pwd;
        final Request request = new Request.Builder()
                .get()
                .tag(this)
                .url(url)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try{
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        String json = response.body().string();
                        Log.d(TAG, "登录json="+json);
                        try{
                            JSONObject object1 = new JSONObject(json);
                            int status  = object1.getInt("status");
                            if(status==0){  //登录成功

                                JSONObject object2 = object1.getJSONObject("data");
                                String name  = object2.getString("name");
                                String token = object2.getString("token");

                                UserInfo user = new UserInfo();
                                user.setName(name);
                                user.setToken(token);

                                Message message = new Message();
                                message.what = 2;
                                message.obj = user;
                                
                                uiHandler.sendMessage(message);
                            }else{
                                uiHandler.sendEmptyMessage(3);  //登录失败
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
