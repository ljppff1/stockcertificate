package com.tust.tools.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tust.tools.R;
import com.tust.tools.service.FYSerivce;

public class FYMainActivity extends Activity implements OnClickListener{
	//TextView显示翻译内容
	private TextView content;
	//按钮：翻译
	private Button fanyi_bt;
    //翻译内容的编辑框
	private EditText content_et;
	//Handler用来后台更新界面
    public static MessageHandler mh;
    //获取翻译服务
    private FYSerivce service;
    //搜索时的显示进度
    private ProgressDialog pd;
    public static final int secss = 1010;//获取成功
    public static final int fail = 1020;//获取失败
    public static final int nonet = 1030;//没有网络
    InputMethodManager imm = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fy_main);
        service =new FYSerivce(this);
        imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
        mh = new MessageHandler();
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        TextView gg=(TextView)this.findViewById(R.id.fy_gg_text);
        if(!ToolsMainActivity.isShow){
            gg.setVisibility(View.INVISIBLE);
        }
	}

    public void init() {
    	fanyi_bt = (Button) this.findViewById(R.id.fy_mian_fanyi_bt);
    	fanyi_bt.setOnClickListener(this);
    	content = (TextView)findViewById(R.id.fy_content_text);
    	content_et = (EditText)findViewById(R.id.fy_content_et);
    	content_et.getBackground().setAlpha(200);
    	LinearLayout ll = (LinearLayout)this.findViewById(R.id.fy_ll);
    	ll.getBackground().setAlpha(150);
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.fy_mian_fanyi_bt://翻译按钮
            imm.hideSoftInputFromWindow(content_et.getWindowToken(), 0);
        	showDialog();//显示进度
        	//获取搜索内容
        	String contentString = content_et.getText().toString().trim().length()!=0?content_et.getText().toString().trim():"空";
        	//启动后台服务
        	service.getResult(contentString);
            break;
        }
    }
    
    
    public void showDialog(){
        pd = ProgressDialog.show(this, "", "正在搜索，请稍后...", true, true);
    }
    
    public boolean onKeyDown(int kCode, KeyEvent kEvent) {
        switch (kCode) {
        case KeyEvent.KEYCODE_BACK: {
                exitDialog();
            return false;
        }
        }
        return super.onKeyDown(kCode, kEvent);
    }

    /*
     * 退出弹出框
     */
    String fileName;// 以当前时间命名的文件名
    public void exitDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String confrimStr = "";
        String cancelStr = "";
        builder.setTitle("是否确认退出？");
        confrimStr = "退出小助手";
        cancelStr = "退出随身翻译";
        builder.setPositiveButton(confrimStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
            }
        });
        builder.setNeutralButton(cancelStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FYMainActivity.this, ToolsMainActivity.class);
                FYMainActivity.this.startActivity(intent);
                FYMainActivity.this.finish();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 200, 0, "清除内容");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 200:
        	content_et.setText("");
        	content.setText("");
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case secss:
            	System.out.println("(String)msg.obj"+(String)msg.obj);
            	content.setText((String)msg.obj);
            	break;
            case fail:
                showMsg("翻译获取失败");
                break;
            case nonet:
                showMsg("没有连接到网络");
                break;
            }
            if(pd!=null&&pd.isShowing()){
                pd.cancel();
            }
            super.handleMessage(msg);
        }
    }
    
    /*
     * 点击图片以后的动画效果
     */
    public void dongHua(View v) {
        v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
}