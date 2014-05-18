package com.sakop.llk.view;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.sakop.llk.PictureManager;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.algo.GUIConstant;
import com.sakop.llk.pojo.GameInfo;

public class GameBoard extends SurfaceView {
	protected int cardSize;
	public GameInfo info;
	protected LinkedList<Point> currentLine = null;
	protected Drawable background = null;
	protected SurfaceHolder holder;
	protected Bitmap bg;
	public int width = 540, height = 960;
	protected Kyodai kyodai;
	// 有些手机分辨率不同，为了使游戏界面居中
	protected int displacement;

	public GameBoard(final Kyodai context, final Bitmap bg) {
		super(context);
		this.kyodai = context;
		this.bg = bg;
		setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		holder = getHolder();
	}

	public Bitmap getBackgroundBitmap() {
		return bg;
	}

	public void drawButtons() {
		PictureManager pm = PictureManager.getInstance();

		SystemState state = SystemState.state;
		Rect rect = new Rect(Util.getXScaledValue(0),
				Util.getYScaledValue(130),
				Util.getXScaledValue(state.getScreenWidth()),
				Util.getYScaledValue(state.getScreenHeight()));
		repaintThisArea(rect);

		Rect lock = new Rect(0, Util.getYScaledValue(125),
				Util.getXScaledValue(540), Util.getYScaledValue(960));
		Canvas canvas = holder.lockCanvas(lock);
		for (int i = 0; i < GUIConstant.VERTICAL_COUNT + 4; i++) {
			for (int j = 0; j < GUIConstant.HORIZONTAL_COUNT + 4; j++) {
				CardView cw = GameBoard.this.info.cards[i][j];
				if (cw != null) {
					rect = Util.getRect(cw.x, cw.y, cardSize, 5);
					rect.left += displacement;
					rect.right += displacement;
					canvas.drawBitmap(
							pm.getCards(getContext(), cw.getBackground()),
							null, rect, null);
				}
			}
		}
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(canvas);
	}

	public void setCardSize(int size) {
		cardSize = size;
		int temp = (width / 2 - size * (GUIConstant.HORIZONTAL_COUNT + 2));
		if (temp != 0)
			displacement = temp / 2;
	}

	protected Paint selectPaint;

	protected void preparePaint() {
		selectPaint = new Paint();
		selectPaint.setColor(Color.RED);
		selectPaint.setStyle(Style.STROKE);
		selectPaint.setStrokeWidth(5);
	}

	// 用背景色重绘此区域
	protected void repaintThisArea(Bitmap bg, int x, int y, int repaintWidth,
			int repaintHeight) {
		Canvas canvas = holder.lockCanvas(new Rect(x, y, x + repaintWidth, y
				+ repaintHeight));
		Rect src = new Rect();
		float widthRatio = width * 1.0f / bg.getWidth();
		float heightRatio = height * 1.0f / bg.getHeight();
		src.left = (int) (x / widthRatio);
		src.top = (int) (y / heightRatio);
		src.right = (int) ((x + repaintWidth) / widthRatio);
		src.bottom = (int) ((y + repaintHeight) / heightRatio);
		Rect dst = new Rect();
		dst.left = x;
		dst.top = y;
		dst.right = x + repaintWidth;
		dst.bottom = y + repaintHeight;
		canvas.drawBitmap(bg, src, dst, null);
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(canvas);
	}

	public void repaintThisArea(Rect rect) {
		repaintThisArea(bg, rect.left, rect.top, rect.right - rect.left,
				rect.bottom - rect.top);
	}

	public void drawBackgroundAndStatus() {
		Canvas canvas = holder.lockCanvas();
		canvas.drawBitmap(bg, null, new Rect(0, 0, SystemState.state.getScreenWidth(), SystemState.state.getScreenHeight()), new Paint(Paint.ANTI_ALIAS_FLAG));
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect());
		holder.unlockCanvasAndPost(canvas);
	}
}
