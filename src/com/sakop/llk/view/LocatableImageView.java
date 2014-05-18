package com.sakop.llk.view;

import android.content.Context;
import android.widget.ImageView;

public class LocatableImageView extends ImageView {

	public LocatableImageView(Context context) {
		super(context);
	}

	public void setLocation(int left, int top) {
		setFrame(left, top, left + 40, top + 40);
	}

}
