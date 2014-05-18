package com.sakop.llk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.sakop.llk.PictureManager;
import com.sakop.llk.Util;

public class NotificationBoard {
	public static int WIDTH = 400;
	public static int HEIGHT = 500;

	public static void drawBackground(Canvas canvas, Context context) {
		PictureManager pm = PictureManager.getInstance();
		Bitmap upperDecoration = pm.getUpperDecoration(context);
		Bitmap lowerDecoration = pm.getLowerDecoration(context);
		Bitmap background = pm.getNetworkSettingBackground(context);

		canvas.drawBitmap(
				upperDecoration,
				null,
				new Rect(0, 0, Util.getXScaledValue(WIDTH), Util
						.getYScaledValue(45)), null);
		canvas.drawBitmap(
				background,
				null,
				new Rect(Util.getXScaledValue(2), Util.getYScaledValue(45),
						Util.getXScaledValue(WIDTH + 2), Util
								.getYScaledValue(HEIGHT - 45)), null);
		canvas.drawBitmap(
				lowerDecoration,
				null,
				new Rect(Util.getXScaledValue(10), Util
						.getYScaledValue(HEIGHT - 45), Util
						.getXScaledValue(WIDTH + 10), Util
						.getYScaledValue(HEIGHT)), null);

	}

}
