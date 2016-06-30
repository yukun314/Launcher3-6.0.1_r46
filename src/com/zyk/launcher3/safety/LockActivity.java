package com.zyk.launcher3.safety;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyk.launcher3.AppInfo;
import com.zyk.launcher3.BubbleTextView;
import com.zyk.launcher3.DeviceProfile;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.LauncherModel;
import com.zyk.launcher3.LauncherSettings;
import com.zyk.launcher3.R;
import com.zyk.launcher3.allapps.AlphabeticalAppsList;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.safety.lock.PatternLockActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyk on 2016/6/27.
 */
public class LockActivity extends Activity {

	private RelativeLayout rl;
	private LayoutInflater mInflater;
	private Launcher mLauncher;
	private RecyclerView mRecyclerView;
	private MyAdapter mAdapter;
	private List<AppInfo> mAdapterItems = new ArrayList<>();
	private int mIconSize;
	private int statusBarHeight = -1;

	public static final int newPassword = 101;//新建密码
	public static final int alterPassword = 102;//修改密码
	public final static int deletePassword = 103;//删除密码

	private int position = -1;//用于记录当前点击的Item的下标
	private ImageView mImageView;//点击Item的imageView

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1){
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);
		mInflater = LayoutInflater.from(LockActivity.this);
		mLauncher = Config.getInstance().mLauncher;

		init();
		init1();
//		//进到壁纸的设置界面
//		startActivityForResult(new Intent(Intent.ACTION_SET_WALLPAPER).setPackage(getPackageName()), 12);
	}

	private void init1(){
//		Button button = (Button) findViewById(R.id.activity_lock_button);
//		button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LauncherModel.addLockToDatabase(LockActivity.this,"zhangsan","123456789");
//				LauncherModel.addLockToDatabase(LockActivity.this,"lisi","234567890");
//				LauncherModel.addLockToDatabase(LockActivity.this,"wangwu","345678901");
//				LauncherModel.addLockToDatabase(LockActivity.this,"liuliu","456789012");
//				LauncherModel.addLockToDatabase(LockActivity.this,"sunqi","567890123");
//			}
//		});
//
//		Button button1 = (Button) findViewById(R.id.activity_lock_button1);
//		button1.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LauncherModel.deleteLockFromDatabase(LockActivity.this,"sunqi","567890123");
//			}
//		});
//
//		Button button2 = (Button) findViewById(R.id.activity_lock_button2);
//		button2.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LauncherModel.updateLockFromDatabase(LockActivity.this,"liuliu","asdfeasdfgasdg");
//			}
//		});
//
//		Button button3 = (Button) findViewById(R.id.activity_lock_button3);
//		button3.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				List<LockInfo> list = LauncherModel.loadLockFromDatabase(LockActivity.this);
//				for(LockInfo info:list){
//					System.out.println("name="+info.name+" password="+info.password);
//				}
//
//			}
//		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(statusBarHeight <= 0) {
			Rect rect = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			// 状态栏高度
			statusBarHeight = rect.top;
			rl.setPadding(0,statusBarHeight,0, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == newPassword){
			String password = data.getStringExtra(PatternLockActivity.DATA);
			password = MD5.md5(password);
			AppInfo appInfo = mAdapterItems.get(position);
			String name = appInfo.intent.getComponent().getPackageName();
			name = MD5.md5(name);
			LauncherModel.addLockToDatabase(this, name, password);
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageResource(R.drawable.select);
			appInfo.isLock = true;
			LockInfo info = new LockInfo();
			info.name = name;
			info.password = password;
			Config.getInstance().mLockList.add(info);
			Toast.makeText(this,"设置密码成功",Toast.LENGTH_SHORT).show();
		}else if(resultCode == alterPassword) {
			String password = data.getStringExtra(PatternLockActivity.DATA);
			password = MD5.md5(password);
			AppInfo appInfo = mAdapterItems.get(position);
			String name = appInfo.intent.getComponent().getPackageName();
			name = MD5.md5(name);
			LauncherModel.updateLockFromDatabase(this, name, password);
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageResource(R.drawable.select);
			appInfo.isLock = true;
			LockInfo info = new LockInfo();
			info.name = name;
			info.password = password;
			List<LockInfo> list = Config.getInstance().mLockList;
			for(LockInfo lockInfo:list) {
				if(lockInfo.name.equals(name)){
					lockInfo.password = password;
					break;
				}
			}
			Toast.makeText(this,"修改密码成功",Toast.LENGTH_SHORT).show();
		}else if(resultCode == deletePassword){
			AppInfo appInfo = mAdapterItems.get(position);
			String name = appInfo.intent.getComponent().getPackageName();
			name = MD5.md5(name);
			LauncherModel.deleteLockFromDatabase(this, name);
			LockInfo li = null;
			List<LockInfo> list = Config.getInstance().mLockList;
			for(LockInfo lockInfo:list) {
				if(lockInfo.name.equals(name)){
					li = lockInfo;
					break;
				}
			}
			if(li != null){
				Config.getInstance().mLockList.remove(li);
			}
			appInfo.isLock = false;
			mImageView.setImageBitmap(null);
			mImageView.setVisibility(View.GONE);
			Toast.makeText(this,"删除密码成功",Toast.LENGTH_SHORT).show();
		}
	}

	private void onItemClick(View v, int position, ImageView imageView) {
		mImageView = imageView;
		this.position = position;
		AppInfo appinfo = mAdapterItems.get(position);
		Intent intent = new Intent();
		intent.setClass(LockActivity.this, PatternLockActivity.class);
		if(appinfo.isLock) {
			intent.putExtra(PatternLockActivity.STYPE, PatternLockActivity.RESETORDELETE);
			intent.putExtra(PatternLockActivity.KEY, SafetyUtils.getPassword(appinfo));
			LockActivity.this.startActivityForResult(intent, alterPassword);
		} else {
			intent.putExtra(PatternLockActivity.STYPE,PatternLockActivity.SETTING);
			LockActivity.this.startActivityForResult(intent, newPassword);
		}

	}

	private void init(){
		rl = (RelativeLayout) findViewById(R.id.activity_lock_item_rl);

		mRecyclerView = (RecyclerView) findViewById(R.id.activity_lock_recyclerview);
		mAdapter = new MyAdapter();
		mRecyclerView.setAdapter(new MyAdapter());
		DeviceProfile d = mLauncher.getDeviceProfile();
		mIconSize = d.allAppsIconSizePx;
		GridLayoutManager grid = new GridLayoutManager(this, d.allAppsNumCols);
		mRecyclerView.setLayoutManager(grid);
		int itemSpacePx = getResources().getDimensionPixelSize(R.dimen.all_apps_icon_top_bottom_padding);
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(itemSpacePx));
		new Thread(){
			@Override
			public void run() {
				super.run();
				mAdapterItems = mLauncher.getAllAppsList();
				List<LockInfo> list = Config.getInstance().mLockList;
				for(AppInfo appInfo:mAdapterItems){
					String name = MD5.md5(appInfo.intent.getComponent().getPackageName());
					for(LockInfo lockInfo:list){
						if(lockInfo.name.equals(name) | lockInfo.name == name){
							appInfo.isLock = true;
							break;
						}
					}
				}
				mHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	private class ViewHolder extends RecyclerView.ViewHolder {

		public int position;
		public AppIconTextView mAppIcon;
		public ImageView mSelect;
		public ViewHolder(View itemView) {
			super(itemView);
			mAppIcon = (AppIconTextView) itemView.findViewById(R.id.activity_lock_item_appicon);
			mSelect = (ImageView) itemView.findViewById(R.id.activity_lock_item_select);
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSelect.getLayoutParams();
			lp.width = mIconSize*2/3;
			lp.height = mIconSize*2/3;
			mSelect.setLayoutParams(lp);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(v, position,mSelect);
				}
			});
		}
	}

	private class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(mInflater.inflate(R.layout.activity_lock_item, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.position = position;
			AppInfo info = mAdapterItems.get(position);
			holder.mAppIcon.applyFromApplicationInfo(info);
			if(info.isLock){
				holder.mSelect.setImageResource(R.drawable.select);
			}else {
				holder.mSelect.setImageBitmap(null);
			}
		}

		@Override
		public int getItemCount() {
			return mAdapterItems.size();
		}
	}

}
