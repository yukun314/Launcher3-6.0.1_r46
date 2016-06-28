package com.zyk.launcher3.safety;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
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
		Button button = (Button) findViewById(R.id.activity_lock_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherModel.addLockToDatabase(LockActivity.this,"zhangsan","123456789");
				LauncherModel.addLockToDatabase(LockActivity.this,"lisi","234567890");
				LauncherModel.addLockToDatabase(LockActivity.this,"wangwu","345678901");
				LauncherModel.addLockToDatabase(LockActivity.this,"liuliu","456789012");
				LauncherModel.addLockToDatabase(LockActivity.this,"sunqi","567890123");
			}
		});

		Button button1 = (Button) findViewById(R.id.activity_lock_button1);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherModel.deleteLockFromDatabase(LockActivity.this,"sunqi","567890123");
			}
		});

		Button button2 = (Button) findViewById(R.id.activity_lock_button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherModel.updateLockFromDatabase(LockActivity.this,"liuliu","asdfeasdfgasdg");
			}
		});

		Button button3 = (Button) findViewById(R.id.activity_lock_button3);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<LockInfo> list = LauncherModel.loadLockFromDatabase(LockActivity.this);
				for(LockInfo info:list){
					System.out.println("name="+info.name+" password="+info.password);
				}

			}
		});
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

	private void onItemClick(View v, int position, ImageView imageView) {
		mAdapterItems.get(position).isLock = true;
		imageView.setImageResource(R.drawable.select);
//		AppInfo appInfo = mAdapterItems.get(position);
//		appInfo.isLock = true;
//		mAdapterItems.remove(position);
//		mAdapterItems.add(appInfo);
		System.out.println("positon:"+position);
		Intent intent = new Intent();
		intent.setClass(LockActivity.this, PatternLockActivity.class);
		LockActivity.this.startActivity(intent);
	}

	private void init(){
		mAdapterItems = mLauncher.getAllAppsList();
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
