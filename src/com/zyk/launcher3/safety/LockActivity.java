package com.zyk.launcher3.safety;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyk.launcher3.AppInfo;
import com.zyk.launcher3.BubbleTextView;
import com.zyk.launcher3.DeviceProfile;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.R;
import com.zyk.launcher3.allapps.AlphabeticalAppsList;
import com.zyk.launcher3.config.Config;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyk on 2016/6/27.
 */
public class LockActivity extends Activity {

	private LayoutInflater mInflater;
	private Launcher mLauncher;
	private RecyclerView mRecyclerView;
	private List<AppInfo> mAdapterItems = new ArrayList<>();
	private int mIconSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);
		mInflater = LayoutInflater.from(LockActivity.this);
		mLauncher = Config.getInstance().mLauncher;
		init();
//		//进到壁纸的设置界面
//		startActivityForResult(new Intent(Intent.ACTION_SET_WALLPAPER).setPackage(getPackageName()), 12);
	}

	private void init(){
		mAdapterItems = mLauncher.getAllAppsList();
		mRecyclerView = (RecyclerView) findViewById(R.id.activity_lock_recyclerview);
		mRecyclerView.setAdapter(new MyAdapter());
		DeviceProfile d = mLauncher.getDeviceProfile();
		mIconSize = d.allAppsIconSizePx;
		GridLayoutManager grid = new GridLayoutManager(this, d.allAppsNumCols);
		mRecyclerView.setLayoutManager(grid);
	}

	private class ViewHolder extends RecyclerView.ViewHolder {

		public AppIconTextView mAppIcon;
		public TextView mBackground;
		public ImageView mSelect;
		public ViewHolder(View itemView) {
			super(itemView);
			mAppIcon = (AppIconTextView) itemView.findViewById(R.id.activity_lock_item_appicon);
			mBackground = (TextView) itemView.findViewById(R.id.activity_lock_item_bg);
			mSelect = (ImageView) itemView.findViewById(R.id.activity_lock_item_select);
		}
	}

	private class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(mInflater.inflate(R.layout.activity_lock_item, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			AppInfo info = mAdapterItems.get(position);
			holder.mAppIcon.applyFromApplicationInfo(info, mLauncher, mIconSize);
			if(info.isLock){
				holder.mBackground.setBackgroundColor(Color.argb(88,12,200,45));
//				holder.mSelect.setImageBitmap();
			}else {
				holder.mBackground.setBackgroundColor(Color.argb(88,200,67,89));
//				holder.mSelect.setImageBitmap();
			}
		}

		@Override
		public int getItemCount() {
			return mAdapterItems.size();
		}
	}

}
