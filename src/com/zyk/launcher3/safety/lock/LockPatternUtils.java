package com.zyk.launcher3.safety.lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.zyk.launcher3.safety.MD5;

public class LockPatternUtils {

	// private static final String TAG = "LockPatternUtils";
	private  String KEY_LOCK_PWD = null;

	private static Context mContext;

//	private static SharedPreferences preference;

	// private final ContentResolver mContentResolver;

	public LockPatternUtils(Context context) {
		mContext = context;
//		preference = PreferenceManager.getDefaultSharedPreferences(mContext);
		// mContentResolver = context.getContentResolver();
	}

	/**
	 * Deserialize a pattern.
	 * 
	 * @param string
	 *            The pattern serialized with {@link #patternToString}
	 * @return The pattern.
	 */
	public static List<LockPatternView.Cell> stringToPattern(String string) {
		List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();

		final byte[] bytes = string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			result.add(LockPatternView.Cell.of(b / 3, b % 3));
		}
		return result;
	}

	/**
	 * Serialize a pattern.
	 * 
	 * @param pattern
	 *            The pattern.
	 * @return The pattern in string form.
	 */
	public static String patternToString(List<LockPatternView.Cell> pattern) {
		if (pattern == null) {
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		return Arrays.toString(res);
	}

//	public void saveLockPattern(List<LockPatternView.Cell> pattern) {
//		Editor editor = preference.edit();
//		editor.putString(KEY_LOCK_PWD, patternToString(pattern));
//		editor.commit();
//	}
	
//	public static void remove(String key){
//		if(preference != null){
//			Editor editor = preference.edit();
//			editor.remove(key);
//			editor.commit();
//		}
//
//
//	}

//	public String getLockPaternString() {
//		return preference.getString(KEY_LOCK_PWD, "");
//	}

	public int checkPattern(List<LockPatternView.Cell> pattern) {
//		String stored = getLockPaternString();
		String newPassword = MD5.md5(patternToString(pattern));
//		System.out.println("KEY_LOCK_PWD:"+KEY_LOCK_PWD);
//		System.out.println("newPassword:"+newPassword);
		if (KEY_LOCK_PWD != null && KEY_LOCK_PWD.length() > 0) {
			return KEY_LOCK_PWD.equals(newPassword) ? 1 : 0;
		}
		return -1;
	}

//	public void clearLock() {
//		saveLockPattern(null);
//	}

	public void setKEY_LOCK_PWD(String kEY_LOCK_PWD) {
		KEY_LOCK_PWD = kEY_LOCK_PWD;
	}
	
	

}
