package com.arbo.hero.spinner;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/9/11.
 */
public class SelectDialog extends AlertDialog {
    protected SelectDialog(Context context) {
        super(context);
    }

    protected SelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.slt_cnt_type);
    }
}
