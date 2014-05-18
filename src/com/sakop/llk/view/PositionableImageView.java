package com.sakop.llk.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.sakop.llk.Util;

public class PositionableImageView {

	private Bitmap bm;
	private int x, y;
	public int width = Util.getXScaledValue(260);
	public int height = Util.getYScaledValue(100);

	public PositionableImageView(Bitmap bm, int x, int y) {
		this.bm = bm;
		this.x = x;
		this.y = y;
	}

	public boolean isInMyArea(MotionEvent event) {
		if (event.getX() >= this.x && event.getX() <= this.x + this.width) {
			if (event.getY() >= this.y && event.getY() <= this.y + this.height)
				return true;
		}
		return false;

	}

	public void drawOnCanvas(Canvas canvas) {
		Rect dest = new Rect();
		dest.left = x;
		dest.right = x + width;
		dest.top = y;
		dest.bottom = y + height;
		canvas.drawBitmap(bm, null, dest, null);
	}

}
