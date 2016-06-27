package com.zyk.launcher3.safety;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zyk.launcher3.AppInfo;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.Utilities;

/**
 * Created by zyk on 2016/6/27.
 */
public class AppIconTextView extends TextView {

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

	public void applyFromApplicationInfo(AppInfo info, Launcher mLauncher, int iconSize) {
//        System.out.println("info title:"+info.title+"  iconBitmap is null:"+(info.iconBitmap == null)+"   mIconSize:"+mIconSize);
		setIcon(mLauncher.createIconDrawable(info.iconBitmap), iconSize);
		setText(info.title);
		if (info.contentDescription != null) {
			setContentDescription(info.contentDescription);
		}
		// We don't need to check the info since it's not a ShortcutInfo
		super.setTag(info);
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
}
