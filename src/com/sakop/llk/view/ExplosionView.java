package com.sakop.llk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import com.sakop.llk.PictureManager;
import com.sakop.llk.R;
import com.sakop.llk.Util;
public class ExplosionView {

	private int[] resources = new int[] { R.drawable.bom_f01,
			R.drawable.bom_f02, R.drawable.bom_f03, R.drawable.bom_f04,
		//	R.drawable.bom_f05, R.drawable.bom_f06, R.drawable.bom_f07,
			R.drawable.bom_f08, R.drawable.bom_f09, R.drawable.bom_f10,
			R.drawable.bom_f11, R.drawable.bom_f12, R.drawable.bom_f13,
			R.drawable.bom_f14, R.drawable.bom_f15, R.drawable.bom_f16,
		//	R.drawable.bom_f17, R.drawable.bom_f18, R.drawable.bom_f19,
			R.drawable.bom_f20, R.drawable.bom_f21, R.drawable.bom_f22,
			R.drawable.bom_f23, R.drawable.bom_f24, R.drawable.bom_f25,
			R.drawable.bom_f26};
	public static final int EXPLOSION_SIZE = 40;
	public static final int displacement = Util.getXScaledValue((60 - EXPLOSION_SIZE) / 2);

	private Context context;
	private SurfaceHolder holder;

	public ExplosionView(Context context, SurfaceHolder holder) {
		this.context = context;
		this.holder = holder;
	}

	public void startDraw(int x1, int y1, int x2, int y2) {
		int picSize = Util.getYScaledValue(EXPLOSION_SIZE);
		Rect rect = new Rect(x1 + displacement, y1 + displacement, x1 + picSize
				+ displacement, y1 + picSize + displacement);
		Rect rect2 = new Rect(x2 + displacement, y2 + displacement, x2
				+ picSize + displacement, y2 + picSize + displacement);
		PictureManager pm = PictureManager.getInstance();
		for (int i = 0; i < resources.length; i++) {
			Canvas canvas = holder.lockCanvas();
			Bitmap bitmap = pm.getCachedBitmap(context, resources[i]);
			canvas.drawBitmap(bitmap, null, rect, null);
			canvas.drawBitmap(bitmap, null, rect2, null);
			holder.unlockCanvasAndPost(canvas);
//			holder.lockCanvas(new Rect());
//			holder.unlockCanvasAndPost(canvas);
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}

	}
}
