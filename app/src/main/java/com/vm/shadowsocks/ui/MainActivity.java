package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.MyPreference;
import com.vm.shadowsocks.core.ProxyConfig;

import java.util.Calendar;

public class MainActivity extends Activity implements
        View.OnClickListener,
        OnCheckedChangeListener,
        LocalVpnService.onStatusChangedListener {

    private static String GL_HISTORY_LOGS;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CONFIG_URL_KEY = "CONFIG_URL_KEY";

    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;

    private Switch switchProxy;
    private TextView textViewLog;
    private ScrollView scrollViewLog;
    private TextView textViewProxyUrl, textViewProxyApp;
    private Calendar mCalendar;

    private TextView txt_channel,txt_message,txt_better;
    private TableLayout tableLayout;
    private FrameLayout lyContent;

    private LoginFragment fgLogin;  //登录
    private UserFragment fgUser;    //用户
    private VipFragment fgVip;
    public  FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: 主Activity已经启动！！！");
        setContentView(R.layout.activity_main);


        //初始化FragmentManager
        fManager = getFragmentManager();
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);
        lyContent = (FrameLayout) findViewById(R.id.ly_content);

        scrollViewLog = (ScrollView) findViewById(R.id.scrollViewLog);
        textViewLog = (TextView) findViewById(R.id.textViewLog);
        findViewById(R.id.ProxyUrlLayout).setOnClickListener(this);
        findViewById(R.id.AppSelectLayout).setOnClickListener(this);

        textViewProxyUrl = (TextView) findViewById(R.id.textViewProxyUrl);
        String ProxyUrl = readProxyUrl();
        if (TextUtils.isEmpty(ProxyUrl)) {
            textViewProxyUrl.setText(R.string.config_not_set_value);
        } else {
            textViewProxyUrl.setText(ProxyUrl);
        }

        textViewLog.setText(GL_HISTORY_LOGS);
        scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);

        mCalendar = Calendar.getInstance();
        LocalVpnService.addOnStatusChangedListener(this);

        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove){
            new AppProxyManager(this);
            textViewProxyApp = (TextView) findViewById(R.id.textViewAppSelectDetail);
        } else {
            ((ViewGroup) findViewById(R.id.AppSelectLayout).getParent()).removeView(findViewById(R.id.AppSelectLayout));
            ((ViewGroup) findViewById(R.id.textViewAppSelectLine).getParent()).removeView(findViewById(R.id.textViewAppSelectLine));
        }

        bindViews();
        //模拟第一次点击链接
        txt_channel.performClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "生命周期 =======》》》》》》》 onStart: ");
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "鬼知道这是什么东西 =======》》》》》》》 onNewIntent");

        //模拟第一次点击我的
        txt_better.performLongClick();


    }

    //UI组件初始化与事件绑定
    private void bindViews(){
        txt_channel = findViewById(R.id.txt_channel);
        txt_message = findViewById(R.id.txt_message);
        txt_better  = findViewById(R.id.txt_better);

        txt_channel.setOnClickListener(this);
        txt_message.setOnClickListener(this);
        txt_better.setOnClickListener(this);
    }
    //重置所有文本的选中状态
    private void setSelected(){
        txt_channel.setSelected(false);
        txt_message.setSelected(false);
        txt_better.setSelected(false);
    }

    //隐藏所有Fragment片段
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fgLogin  != null)fragmentTransaction.hide(fgLogin);
        if(fgVip    != null)fragmentTransaction.hide(fgVip);
        if(fgUser   != null)fragmentTransaction.hide(fgUser);
    }



    String readProxyUrl() {
        SharedPreferences preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        return preferences.getString(CONFIG_URL_KEY, "");
    }

    void setProxyUrl(String ProxyUrl) {
        SharedPreferences preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(CONFIG_URL_KEY, ProxyUrl);
        editor.apply();
    }

    String getVersionName() {
        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            Log.e(TAG, "null package manager is impossible");
            return null;
        }

        try {
            return packageManager.getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "package not found is impossible", e);
            return null;
        }
    }

    boolean isValidUrl(String url) {
        try {
            if (url == null || url.isEmpty())
                return false;

            if (url.startsWith("ss://")) {//file path
                return true;
            } else { //url
                Uri uri = Uri.parse(url);
                if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))
                    return false;
                if (uri.getHost() == null)
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if((v.getId() == R.id.txt_channel)||(v.getId() == R.id.txt_message)||(v.getId() == R.id.txt_better) ){
            FragmentTransaction fTransaction = fManager.beginTransaction();
            hideAllFragment(fTransaction);
            switch (v.getId()){
                case R.id.txt_channel: //链接
                    tableLayout.setVisibility(View.VISIBLE);
                    setSelected();
                    txt_channel.setSelected(true);
                    break;
                case R.id.txt_message: //VIP
                    setSelected();
                    txt_message.setSelected(true);
                    tableLayout.setVisibility(View.GONE);
                    if(fgVip == null){
                        fgVip = new VipFragment();
                        fTransaction.add(R.id.ly_content,fgVip);
                    }else{
                        fTransaction.show(fgVip);
                    }
                    break;

                case R.id.txt_better:  //我的
                    setSelected();
                    txt_better.setSelected(true);

                    tableLayout.setVisibility(View.GONE);
                    //TODO：判断用户的登录状态，已经登录显示fgUser，未登录显示fgLogin
                    if(MyPreference.getInstance(this).getLoginName().length()<=0){
                        //未登录
                        if(fgLogin == null){
                            fgLogin = new LoginFragment();
                            fTransaction.add(R.id.ly_content,fgLogin);
                        }else{
                            fTransaction.show(fgLogin);
                        }
                    }else{
                        //已经登录
                        if(fgUser == null){
                            fgUser = new UserFragment();
                            fTransaction.add(R.id.ly_content,fgUser);
                        }else{
                            fTransaction.show(fgUser);
                        }
                    }

                    break;
            }
            fTransaction.commit();

        }else{
            if (switchProxy.isChecked()) {
                return;
            }

            if (v.getTag().toString().equals("ProxyUrl")){
                new AlertDialog.Builder(this)
                        .setTitle(R.string.config_url)
                        .setItems(new CharSequence[]{
                                getString(R.string.config_url_scan),
                                getString(R.string.config_url_manual)
                        }, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        scanForProxyUrl();
                                        break;
                                    case 1:
                                        showProxyUrlInputDialog();
                                        break;
                                }
                            }
                        })
                        .show();
            } else if (v.getTag().toString().equals("AppSelect")){
                System.out.println("abc");
                startActivity(new Intent(this, AppManager.class));
            }
        }



    }

    private void scanForProxyUrl() {
        new IntentIntegrator(this)
                .setPrompt(getString(R.string.config_url_scan_hint))
                .initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    private void showProxyUrlInputDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint(getString(R.string.config_url_hint));
        editText.setText(readProxyUrl());

        new AlertDialog.Builder(this)
                .setTitle(R.string.config_url)
                .setView(editText)
                .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() == null) {
                            return;
                        }

                        String ProxyUrl = editText.getText().toString().trim();
                        if (isValidUrl(ProxyUrl)) {
                            setProxyUrl(ProxyUrl);
                            textViewProxyUrl.setText(ProxyUrl);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLogReceived(String logString) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        logString = String.format("[%1$02d:%2$02d:%3$02d] %4$s\n",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND),
                logString);

        System.out.println(logString);

        if (textViewLog.getLineCount() > 200) {
            textViewLog.setText("");
        }
        textViewLog.append(logString);
        scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        GL_HISTORY_LOGS = textViewLog.getText() == null ? "" : textViewLog.getText().toString();
    }

    @Override
    public void onStatusChanged(String status, Boolean isRunning) {
        switchProxy.setEnabled(true);
        switchProxy.setChecked(isRunning);
        onLogReceived(status);
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (LocalVpnService.IsRunning != isChecked) {
            switchProxy.setEnabled(false);
            if (isChecked) {
                Intent intent = LocalVpnService.prepare(this);
                if (intent == null) {
                    startVPNService();
                } else {
                    startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
                }
            } else {
                LocalVpnService.IsRunning = false;
            }
        }
    }

    private void startVPNService() {
        String ProxyUrl = readProxyUrl();
        if (!isValidUrl(ProxyUrl)) {
            Toast.makeText(this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
            switchProxy.post(new Runnable() {
                @Override
                public void run() {
                    switchProxy.setChecked(false);
                    switchProxy.setEnabled(true);
                }
            });
            return;
        }

        textViewLog.setText("");
        GL_HISTORY_LOGS = null;
        onLogReceived("starting...");
        LocalVpnService.ProxyUrl = ProxyUrl;
        startService(new Intent(this, LocalVpnService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "鬼知道是什么=====》》》》》》》onActivityResult: ");
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startVPNService();
            } else {
                switchProxy.setChecked(false);
                switchProxy.setEnabled(true);
                onLogReceived("canceled.");
            }
            return;
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String ProxyUrl = scanResult.getContents();
            if (isValidUrl(ProxyUrl)) {
                setProxyUrl(ProxyUrl);
                textViewProxyUrl.setText(ProxyUrl);
            } else {
                Toast.makeText(MainActivity.this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_switch);
        if (menuItem == null) {
            return false;
        }

        switchProxy = (Switch) menuItem.getActionView();
        if (switchProxy == null) {
            return false;
        }

        switchProxy.setChecked(LocalVpnService.IsRunning);
        switchProxy.setOnCheckedChangeListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name) + getVersionName())
                        .setMessage(R.string.about_info)
                        .setPositiveButton(R.string.btn_ok, null)
                        .setNegativeButton(R.string.btn_more, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dawei101/shadowsocks-android-java")));
                            }
                        })
                        .show();

                return true;
            case R.id.menu_item_exit:
                if (!LocalVpnService.IsRunning) {
                    finish();
                    return true;
                }

                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_item_exit)
                        .setMessage(R.string.exit_confirm_info)
                        .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocalVpnService.IsRunning = false;
                                LocalVpnService.Instance.disconnectVPN();
                                stopService(new Intent(MainActivity.this, LocalVpnService.class));
                                System.runFinalization();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, null)
                        .show();

                return true;
            case R.id.menu_item_toggle_global:
                ProxyConfig.Instance.globalMode = !ProxyConfig.Instance.globalMode;
                if (ProxyConfig.Instance.globalMode) {
                    onLogReceived("Proxy global mode is on");
                } else {
                    onLogReceived("Proxy global mode is off");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "生命周期==========>>>>>>>>  onResume: ");
        if (AppProxyManager.isLollipopOrAbove) {
            if (AppProxyManager.Instance.proxyAppInfo.size() != 0) {
                String tmpString = "";
                for (AppInfo app : AppProxyManager.Instance.proxyAppInfo) {
                    tmpString += app.getAppLabel() + ", ";
                }
                textViewProxyApp.setText(tmpString);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "生命周期==========>>>>>>>>  onDestroy: ");
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }

}
