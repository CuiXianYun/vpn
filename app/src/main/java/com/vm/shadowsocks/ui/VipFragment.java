package com.vm.shadowsocks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vm.shadowsocks.R;

/**
 * Created by echo on 2018/7/16.
 */

public class VipFragment extends Fragment{



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fg_vip,container,false);

        return view;
    }
}
