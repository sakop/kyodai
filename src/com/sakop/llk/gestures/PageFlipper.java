package com.sakop.llk.gestures;

import com.sakop.llk.R;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class PageFlipper extends OnGestureAdapter {

	private Animation leftIn, leftOut, rightIn, rightOut;
	private ViewFlipper flipper;

	public PageFlipper(Activity activity, ViewFlipper flipper) {
		leftIn = AnimationUtils.loadAnimation(activity, R.anim.left_in);
		leftOut = AnimationUtils.loadAnimation(activity, R.anim.left_out);
		rightIn = AnimationUtils.loadAnimation(activity, R.anim.right_in);
		rightOut = AnimationUtils.loadAnimation(activity, R.anim.right_out);
		this.flipper = flipper;
	}

	private static final int FLIP_DISTANCE = 50;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
			flipper.setInAnimation(leftIn);
			flipper.setOutAnimation(leftOut);
			flipper.showPrevious();
		} else if (e1.getX() - e2.getX() < FLIP_DISTANCE) {
			flipper.setInAnimation(rightIn);
			flipper.setOutAnimation(rightOut);
			flipper.showNext();
		}
		return true;
	}

}
