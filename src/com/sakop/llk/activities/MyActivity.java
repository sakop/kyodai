package com.sakop.llk.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MyActivity extends Activity {
	public static final String prefix = "LifeCycle-";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("", prefix + this.getClass().getSimpleName() + ":create");
	}

	public void onPause() {
		super.onPause();
		Log.d("", prefix + this.getClass().getSimpleName() + ":pause");
	}

	public void onResume() {
		super.onResume();
		Log.d("", prefix + this.getClass().getSimpleName() + ":resume");
	}

	public void onStop() {
		super.onStop();
		Log.d("", prefix + this.getClass().getSimpleName() + ":stop");
	}

	public void onStart() {
		super.onStart();
		Log.d("", prefix + this.getClass().getSimpleName() + ":start");
	}

	public void onRestart() {
		super.onRestart();
		Log.d("", prefix + this.getClass().getSimpleName() + ":restart");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d("", prefix + this.getClass().getSimpleName() + ":destroy");
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.d("", prefix + this.getClass().getSimpleName()
				+ ":onSaveInstanceState");
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("", prefix + this.getClass().getSimpleName()
				+ ":onRestoreInstanceState");
	}

	@Deprecated
	public int getScreenOrient() {
		Activity activity = this;
		int orient = activity.getRequestedOrientation();
		if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				&& orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			// 宽>高为横屏,反正为竖屏
			WindowManager windowManager = activity.getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
					: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		}
		return orient;
	}
}
