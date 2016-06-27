package com.zyk.launcher3.safety;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.zyk.launcher3.AppInfo;
import com.zyk.launcher3.BubbleTextView;
import com.zyk.launcher3.DeviceProfile;
import com.zyk.launcher3.FolderIcon;
import com.zyk.launcher3.IconCache;
import com.zyk.launcher3.ItemInfo;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.LauncherAppState;
import com.zyk.launcher3.ShortcutInfo;
import com.zyk.launcher3.Utilities;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.model.PackageItemInfo;

/**
 * Created by zyk on 2016/6/27.
 */
public class AppIconTextView extends TextView {

	private IconCache.IconLoadRequest mIconLoadRequest;
	private Launcher mLauncher = Config.getInstance().mLauncher;
	private DeviceProfile mDeviceProfile = mLauncher.getDeviceProfile();
	private Drawable mIcon;

	public AppIconTextView(Context context) {
		super(context);
	}

	public AppIconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppIconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AppIconTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void applyFromApplicationInfo(AppInfo info) {
		setIcon(mLauncher.createIconDrawable(info.iconBitmap), mDeviceProfile.iconSizePx);
		setText(info.title);
		if (info.contentDescription != null) {
			setContentDescription(info.contentDescription);
		}
		// We don't need to check the info since it's not a ShortcutInfo
		super.setTag(info);
		verifyHighRes();
	}

	/**
	 * Sets the icon for this view based on the layout direction.
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private Drawable setIcon(Drawable icon, int iconSize) {
		mIcon = icon;
		if (iconSize != -1) {
			mIcon.setBounds(0, 0, iconSize, iconSize);
		}

		setCompoundDrawables(null, mIcon, null, null);
		return icon;
	}

	/**
	 * Verifies that the current icon is high-res otherwise posts a request to load the icon.
	 */
	public void verifyHighRes() {
		if (mIconLoadRequest != null) {
			mIconLoadRequest.cancel();
			mIconLoadRequest = null;
		}
		if (getTag() instanceof AppInfo) {
			AppInfo info = (AppInfo) getTag();
			if (info.usingLowResIcon) {
				mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
						.updateIconInBackground(AppIconTextView.this, info);
			}
		} else if (getTag() instanceof ShortcutInfo) {
//			ShortcutInfo info = (ShortcutInfo) getTag();
//			if (info.usingLowResIcon) {
//				mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
//						.updateIconInBackground(BubbleTextView.this, info);
//			}
		} else if (getTag() instanceof PackageItemInfo) {
			PackageItemInfo info = (PackageItemInfo) getTag();
			if (info.usingLowResIcon) {
				mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
						.updateIconInBackground(AppIconTextView.this, info);
			}
		}
	}

	private boolean mDisableRelayout = false;
	/**
	 * Applies the item info if it is same as what the view is pointing to currently.
	 */
	public void reapplyItemInfo(final ItemInfo info) {
		if (getTag() == info) {
			mIconLoadRequest = null;
			mDisableRelayout = true;
			if (info instanceof AppInfo) {
				applyFromApplicationInfo((AppInfo) info);
			} else if (info instanceof ShortcutInfo) {
//				applyFromShortcutInfo((ShortcutInfo) info,
//						LauncherAppState.getInstance().getIconCache());
//				if ((info.rank < FolderIcon.NUM_ITEMS_IN_PREVIEW) && (info.container >= 0)) {
//					View folderIcon =
//							mLauncher.getWorkspace().getHomescreenIconByItemId(info.container);
//					if (folderIcon != null) {
//						folderIcon.invalidate();
//					}
//				}
			} else if (info instanceof PackageItemInfo) {
//				applyFromPackageItemInfo((PackageItemInfo) info);
			}
			mDisableRelayout = false;
		}
	}
}
