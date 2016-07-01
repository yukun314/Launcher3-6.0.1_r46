package com.zyk.launcher3.safety.lock;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zyk.launcher3.R;
import com.zyk.launcher3.safety.LockActivity;
import com.zyk.launcher3.safety.lock.LockPatternView.Cell;

/**
 * resultCode 0处理失败 
 * 1 处理成功
 */
public class PatternLockActivity extends Activity{
	private LockPatternView lockPatternView;
	private RadioGroup mRadioGroup;
	private RadioButton mUpdateRadioButton,mDeleteRadioButton;

	private boolean isFirst = true;
	private boolean isReset = true;
	private String firstPassword = "";
	private final String cxszss = "重新设置手势";
	private final String wjmm ="忘记密码?";
	private LockPatternUtils lockPatternUtils;
	private TextView msg1,msg2,msg3;
	private int type;
	private int resultCode;
	public static final String STYPE = "stype";
	public static final String KEY = "password";
	public static final String PARENTID = "parentId";//忘记密码时使用
	public static final String DATA = "data";//返回的数据
	public static final String RESULTCODE = "resultCode";//当用于解锁时(UNLOCK) 此值必须有
	private int parentId ;
	/**
	 * 用于设置密码
	 */
	public static final int SETTING = 1;
	/**
	 * 用于解锁
	 */
	public static final int UNLOCK = 2;
	
	/**
	 * 用于重置密码
	 */
	public static final int RESETPASSWORD = 3;

	/**
	 * 重置 或 删除密码
	 */
	public static final int RESETORDELETE = 4;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1){
				lockPatternView.clearPattern();
			}
		}
		
	};
	
	private Timer timer = new Timer();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			setResult(0);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode > 0){//忘记密码 从找回密码界面返回来后 进行新密码设置项
			type = SETTING;
			init(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent =getIntent();
		type = intent.getIntExtra(STYPE,0);
		String password = intent.getStringExtra(KEY);
		parentId = intent.getIntExtra(PARENTID, 0);
		resultCode = intent.getIntExtra(RESULTCODE,0);
		setContentView(R.layout.activity_pattern_lock);
		lockPatternUtils = new LockPatternUtils(this);
		lockPatternUtils.setKEY_LOCK_PWD(password);
		lockPatternView = (LockPatternView) findViewById(R.id.activity_lock_lpv);
		mRadioGroup = (RadioGroup) findViewById(R.id.activity_pattern_lock_rg);
		mUpdateRadioButton = (RadioButton) findViewById(R.id.activity_pattern_lock_rb1);
		mDeleteRadioButton = (RadioButton) findViewById(R.id.activity_pattern_lock_rb2);

		msg1 = (TextView) findViewById(R.id.activity_lock_msg1);
		msg1.setTextSize(18);
		msg1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = msg1.getText().toString();
				if(str == cxszss || str.equals(cxszss)){
					isFirst = true;
					firstPassword = "";
					msg1.setText(null);
					msg2.setTextColor(Color.rgb(255, 255, 255));
					msg2.setText("绘制解锁图案");
				}else if(str == wjmm || str.equals(wjmm)){
//					Intent intent = new Intent();
//					intent.setClass(PatternLockActivity.this,ForgetPasswordActivity.class);
//					intent.putExtra(PARENTID,parentId);
//					startActivityForResult(intent, 1);
				}
			}
		});
		msg2 = (TextView) findViewById(R.id.activity_lock_msg2);
		msg2.setTextSize(18);
		msg3 = (TextView) findViewById(R.id.activity_lock_msg3);
		msg3.setTextSize(26);
		
		init(type);
		
		lockPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {

			@Override
			public void onPatternStart() {
			}

			@Override
			public void onPatternCleared() {
			}

			@Override
			public void onPatternCellAdded(List<Cell> pattern) {	
			}

			@Override
			public void onPatternDetected(List<Cell> pattern) {
				if(pattern.size() > 3){
					if(type == SETTING){
						setting(pattern);
					}else if(type == UNLOCK){
						unlock(pattern);
					}else if(type == RESETPASSWORD){
						resetPassword(pattern);
					} else if(type == RESETORDELETE) {
						resetPassword(pattern);
					}
				}else{
					msg2.setTextColor(Color.rgb(250, 118, 118));
					msg2.setText("至少链接4个点，请重新输入");
					lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
					timer.schedule(new MyTimerTask(), 500);
				}
			}
			
		});
	}
	
	private void init(int type){
		if(type == SETTING){
			msg3.setTextColor(Color.rgb(232, 232, 232));
			msg3.setText("请设置手势密码");
			
			msg2.setTextColor(Color.rgb(255, 255, 255));
			msg2.setText("绘制解锁图案");
			
			msg1.setText(null);
			mRadioGroup.setVisibility(View.GONE);
		}else if(type == UNLOCK || type == RESETPASSWORD){
			msg3.setTextColor(Color.rgb(232, 232, 232));
			msg3.setText("请解锁");
			
			msg2.setTextColor(Color.rgb(255, 255, 255));
			msg2.setText("绘制解锁图案");
			mRadioGroup.setVisibility(View.GONE);
//			msg1.setTextColor(Color.rgb(250, 118, 118));
//			msg1.setText(wjmm);
		}else if(type == RESETORDELETE) {
			msg3.setTextColor(Color.rgb(232, 232, 232));
			msg3.setText("请解锁");

			msg2.setTextColor(Color.rgb(255, 255, 255));
			msg2.setText("绘制解锁图案");
			mRadioGroup.setVisibility(View.VISIBLE);
		}
	}

	private void setting(List<Cell> pattern){
		if(isFirst){
			firstPassword = LockPatternUtils.patternToString(pattern);
//			System.out.println("firstPassword:"+firstPassword);
			isFirst = false;
			msg2.setTextColor(Color.rgb(255, 255, 255));
			msg2.setText("请再次绘制解锁图案");
			
			msg1.setTextColor(Color.rgb(250, 118, 118));
			msg1.setText(cxszss);
			timer.schedule(new MyTimerTask(), 500);
		}else{
			String str = LockPatternUtils.patternToString(pattern);
			if(str == firstPassword || str.equals(firstPassword)){//两次输入的密码一样
				String password = LockPatternUtils.patternToString(pattern);
//				lockPatternUtils.saveLockPattern(pattern);
				Intent intent = new Intent();
				System.out.println("两次密码一致:"+password);
				intent.putExtra(DATA,password);
				if(type == SETTING) {
					setResult(LockActivity.newPassword, intent);
				} else if(type == RESETORDELETE) {
					setResult(LockActivity.alterPassword,intent);
				}
				finish();
			}else{
				msg2.setTextColor(Color.rgb(250, 118, 118));
				msg2.setText("两次密码输入不一致，请重新输入");
				lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				timer.schedule(new MyTimerTask(), 500);
			}
		}
	}
	
	private void unlock(List<Cell> pattern){
		int result = lockPatternUtils.checkPattern(pattern);
		if(result == 1){//密码正确
			setResult(resultCode);
			finish();
		}else{
			msg2.setTextColor(Color.rgb(250, 118, 118));
			msg2.setText("密码输入错误");
			lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
			timer.schedule(new MyTimerTask(), 700);
		}
	}
	
	private void resetPassword(List<Cell> pattern){
		if(isReset){
			int result = lockPatternUtils.checkPattern(pattern);
			if(result == 1){//密码正确
				if(mDeleteRadioButton.isChecked()){
					setResult(LockActivity.deletePassword);
					finish();
				}else {
					lockPatternView.clearPattern();
					isReset = false;
					msg3.setTextColor(Color.rgb(232, 232, 232));
					msg3.setText("请设置手势密码");

					msg2.setTextColor(Color.rgb(255, 255, 255));
					msg2.setText("绘制解锁图案");
					timer.schedule(new MyTimerTask(), 500);
				}
			}else{
				msg2.setTextColor(Color.rgb(250, 118, 118));
				msg2.setText("密码输入错误");
				lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				timer.schedule(new MyTimerTask(), 500);
			}
		}else{
			setting(pattern);
		}
		
	}
	
	private class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			handler.sendEmptyMessage(1);
		}
	};
}
