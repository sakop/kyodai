package com.sakop.llk.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.sakop.llk.PictureManager;
import com.sakop.llk.R;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.gestures.PageFlipper;

public class HelpActivity extends MyActivity {

	private static class HelpImageView extends TextView {

		private HelpImageView(Context context, String text) {
			super(context);
			setText(text);
			setTextColor(Color.RED);
			if (SystemState.state.getScreenHeight() == 960)
				setTextSize(Util.getYScaledValue(20));
			else
				setTextSize(Util.getYScaledValue(28));
				setPadding(Util.getXScaledValue(20), Util.getYScaledValue(30),
						Util.getXScaledValue(20), Util.getYScaledValue(30));
			setLineSpacing(2, 1);
		}
	}

	ViewFlipper flipper = null;

	private TextView helpContents[] = new HelpImageView[2];
	private GestureDetector detector;

	private void initHelpContents() {
		helpContents[0] = new HelpImageView(this, getResources().getString(
				R.string.help0));
		helpContents[1] = new HelpImageView(this, getResources().getString(
				R.string.help1));
	}

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.help_view);
		flipper = (ViewFlipper) findViewById(R.id.helpFlipper);
		initHelpContents();
		for (int i = 0; i < helpContents.length; i++) {
			flipper.addView(helpContents[i]);
		}
		flipper.setBackgroundDrawable(new BitmapDrawable(PictureManager
				.getInstance().getSettingBackground(this)));

		detector = new GestureDetector(new PageFlipper(this, flipper));
	}

	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

}
