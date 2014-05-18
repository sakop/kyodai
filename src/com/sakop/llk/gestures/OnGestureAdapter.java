package com.sakop.llk.gestures;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import com.sakop.llk.R;

public abstract class OnGestureAdapter implements OnGestureListener {

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public abstract boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY);

}
