package com.zyk.launcher3.safety;

import com.zyk.launcher3.AppInfo;
import com.zyk.launcher3.config.Config;

import java.util.List;

/**
 * Created by zyk on 2016/6/29.
 */
public class SafetyUtils {
	/**
	 * 获取密码
	 */
	public static String getPassword(String name){
		List<LockInfo> list =  Config.getInstance().mLockList;
		String result = null;
		for(LockInfo info:list) {
			if(info.name.equals(name) | info.name == name) {
				result = info.password;
				break;
			}
		}
		return result;
	}

	public static String getPassword(AppInfo appInfo){
		String intentStr = appInfo.intent.toString();
		System.out.println("intentStr:"+intentStr);
		return getPassword(MD5.md5(intentStr));
	}
}
