package com.vm.shadowsocks.core;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {

    private static MyPreference preference = null;
    private SharedPreferences sharedPreference;
    private String packageName = "";

    private static final String LOGIN_NAME = "loginName"; //登录名
    private static final String PASSWORD = "password";  //密码
    private static final String TOKEN    = "token";     //唯一标示
    private static final String IS_SAVE_PWD = "isSavePwd"; //是否保留密码

    public static synchronized MyPreference getInstance(Context context) {
        if (preference == null) preference = new MyPreference(context);
        return preference;

    }


    public MyPreference(Context context){
        packageName = context.getPackageName() + "_preferences";
        sharedPreference = context.getSharedPreferences(packageName, context.MODE_PRIVATE);
    }

    public String getLoginName(){
        String loginName = sharedPreference.getString(LOGIN_NAME, "");
        return loginName;
    }


    public void SetLoginName(String loginName){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(LOGIN_NAME, loginName);
        editor.commit();
    }

    public String getPassword(){
        String password = sharedPreference.getString(PASSWORD, "");
        return password;
    }


    public void SetPassword(String password){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }


    public String getToken(){
        String password = sharedPreference.getString(TOKEN, "");
        return password;
    }


    public void SetToken(String token){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }


    public boolean IsSavePwd(){
        Boolean isSavePwd = sharedPreference.getBoolean(IS_SAVE_PWD, false);
        return isSavePwd;
    }

    public void SetIsSavePwd(Boolean isSave){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(IS_SAVE_PWD, isSave);
        edit.commit();
    }









}
