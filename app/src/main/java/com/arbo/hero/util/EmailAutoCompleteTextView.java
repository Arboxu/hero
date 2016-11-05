package com.arbo.hero.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arbo.hero.R;

public class EmailAutoCompleteTextView extends AutoCompleteTextView implements View.OnFocusChangeListener, TextWatcher {
    private boolean clearInput;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    private static final String TAG = "EmailAutoCompleteTextView";
    private String[] emailSufixs = new String[]{"@qq.com", "@163.com", "@126.com", "@gmail.com", "@sina.com", "@hotmail.com",
            "@yahoo.cn", "@sohu.com", "@foxmail.com", "@139.com", "@yeah.net", "@vip.qq.com", "@vip.sina.com"};

    public void setClearInput(boolean val)
    {
        this.clearInput = val;
    }

    public boolean getClearInput()
    {
        return clearInput;
    }

    public EmailAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EmailAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }

    public EmailAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs,android.R.attr.editTextStyle);
        init(context);
    }

    public void setAdapterString(String[] es) {
        if(es != null && es.length > 0)
            this.emailSufixs = es;
    }


    private void init(final Context context){
        //adapter中使用默认的emailSufixs中的数据，可以通过setAdapterString来更改
        this.setAdapter(new EmailAutoCompleteAdapter(context,R.layout.autocomplete_item,emailSufixs));
        this.setThreshold(1);

        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.search_param_delete);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //this.setGravity(Gravity.CENTER);
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    protected void replaceText(CharSequence text){
        //当我们在下拉框中选择一项时，android会默认使用AutoCompleteTextView中Adapter里的文本来填充文本域
        //因为这里Adapter中只是存了常用email的后缀
        //因此要重新replace逻辑，将用户输入的部分与后缀合并
        Log.i(TAG + " replaceText", text.toString());
        String t = this.getText().toString();
        int index = t.indexOf("@");
        if(index != -1)
            t = t.substring(0, index);
        super.replaceText(t + text);
    }

    protected void performFiltering(CharSequence text,int keyCode){
        //该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，若它匹配
        //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
        Log.i(TAG + " performFiltering", text.toString() + "   " + keyCode);
        String t = text.toString();

        //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
        //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
        int index = t.indexOf("@");
        if(index == -1) {
            if(t.matches("^[a-zA-Z0-9_]+$")) {
                super.performFiltering("@", keyCode);
            }
            else
                this.dismissDropDown();//当用户中途输入非法字符时，关闭下拉提示框
        } else {
            super.performFiltering(t.substring(index), keyCode);
        }
    }


    private class EmailAutoCompleteAdapter extends ArrayAdapter<String>{

        public EmailAutoCompleteAdapter(Context context, int textViewResourceId, String[] email_s) {
            super(context, textViewResourceId, email_s);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            Log.i(TAG, "in GetView");
            View v = convertView;
            if(v==null)
                v = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_item,null);
            TextView tv = (TextView)v.findViewById(R.id.autotv);
            String t = EmailAutoCompleteTextView.this.getText().toString();
            int index = t.indexOf("@");
            if(index !=  -1){
                t = t.substring(0,index);
            }
            //将用户输入的文本与adapter中的email后缀拼接后，在下拉框中显示
            tv.setText(t+getItem(position));
            return v;
        }
    }

/*----------------------------------------------------------------------------------*/
/*----------------------以下为清除功能---------------------------------------------------*/
/*----------------------------------------------------------------------------------*/


    // 重置edittext, 居中并失去焦点
    private void searchContentLostFocus() {

        setClearIconVisible(false);
        //this.setGravity(Gravity.CENTER);
        this.clearFocus();
        InputMethodManager manager = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    // 获取焦点
    private void searchContentGetFocus(final EditText searchContent) {
        this.requestFocus();
        this.setGravity(Gravity.START|Gravity.CENTER);
        this.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(searchContent, 0);
            }
        });
        // 光标置于文字最后
        this.setSelection(this.getText().length());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    Log.i("onTouchEvent:",""+touchable);
                    setClearInput(touchable);
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {

        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
            searchContentGetFocus(this);

            String text = EmailAutoCompleteTextView.this.getText().toString();
            //当该文本域重新获得焦点后，重启自动完成
            if(!"".equals(text)){
                performFiltering(text,0);
            }
        } else {
            //当文本域丢失焦点后，检查输入email地址的格式
            EmailAutoCompleteTextView etv = (EmailAutoCompleteTextView)view;
            String text = etv.getText().toString();
            //使用正则表达式判断
            if(text!=null && text.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$")){
                Toast.makeText(getContext(),"邮箱可用",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"邮箱不正确",Toast.LENGTH_SHORT).show();

            }
            searchContentLostFocus();
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        Log.i("onTextChanged:",""+hasFoucs);
        if(hasFoucs){
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }





    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.search_param_delete);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //this.setGravity(Gravity.CENTER);
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }
}
