package com.sakop.llk.view;

import android.content.Context;
import android.widget.ImageView;

import com.sakop.llk.Util;

public class MyImageView extends ImageView {

	public MyImageView(Context context) {
		super(context);
	}

	public void setLocation(int left, int top) {
		setFrame(Util.getXScaledValue(left), Util.getYScaledValue(top),
				Util.getXScaledValue(left + 500),
				Util.getYScaledValue(top + 100));
	}

}
