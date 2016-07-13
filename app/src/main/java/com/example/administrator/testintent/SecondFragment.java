package com.example.administrator.testintent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by Administrator on 2016/7/12.
 */
public class SecondFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState)
    {
        return layoutInflater.inflate(R.layout.fragment2,container,false);
    }
}
