package com.sakop.llk.network;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sakop.llk.PictureManager;
import com.sakop.llk.R;
import com.sakop.llk.Util;
import com.sakop.llk.view.NotificationBoard;

public class NetworkPreparationImage extends ImageView {

	private Paint paint;
	private Paint smallPaint;
	private NetworkComponent comp;

	public NetworkPreparationImage(NetworkComponent comp) {
		super(comp.context);
		this.comp = comp;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(Util.getYScaledValue(28));
		smallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		smallPaint.setTextSize(Util.getYScaledValue(20));
		setLayoutParams(new ViewGroup.LayoutParams(
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT)));
	}

	public void setHintText(String hintText) {
		this.comp.hintText = hintText;
	}

	public void onDraw(Canvas canvas) {
		NotificationBoard.drawBackground(canvas, getContext());
		Bitmap cancel = PictureManager.getInstance().getCancelBm(getContext());
		canvas.drawBitmap(
				cancel,
				null,
				new Rect(Util.getXScaledValue(140), Util
						.getYScaledValue(NotificationBoard.HEIGHT - 100), Util
						.getXScaledValue(260), Util
						.getYScaledValue(NotificationBoard.HEIGHT - 50)), null);
		if (comp.hintText2 == null)
			canvas.drawText(comp.hintText, Util.getXScaledValue(50),
					Util.getYScaledValue(100), paint);
		else
			canvas.drawText(comp.hintText, Util.getXScaledValue(50),
					Util.getYScaledValue(100), smallPaint);
		if (comp.hintText2 != null) {
			canvas.drawText(comp.hintText2, Util.getXScaledValue(50),
					Util.getYScaledValue(180), smallPaint);
		}
		if (comp.hintText3 != null) {
			canvas.drawText(comp.hintText3, Util.getXScaledValue(50),
					Util.getYScaledValue(260), smallPaint);
		}
	}

}
