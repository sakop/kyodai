package com.sakop.llk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sakop.llk.PictureManager;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.activities.Kyodai;

public class GameOverView {
	public static PopupWindow showVictoryWindow(final Context context) {
		SystemState.state.set(SystemState.GAME_STATUS,
				SystemState.GAME_NOT_STARTED);
		long start = (Long) SystemState.state.get(SystemState.GAME_START_TIME);
		long now = SystemClock.elapsedRealtime();
		final long seconds = (now - start) / 1000;
		ImageView background = new ImageView(context) {
			public void onDraw(Canvas canvas) {
				NotificationBoard.drawBackground(canvas, context);
				PictureManager pm = PictureManager.getInstance();
				Bitmap clearAll = pm.getClearAllPicsBM(context);
				Bitmap timeConsuming = pm.getTimeConsumingBm(context);

				drawWinningHeader(context, canvas);

				canvas.drawBitmap(
						clearAll,
						null,
						new Rect(Util.getXScaledValue(40), Util
								.getYScaledValue(250), Util
								.getXScaledValue(235), Util
								.getYScaledValue(280)), null);
				canvas.drawBitmap(
						timeConsuming,
						null,
						new Rect(Util.getXScaledValue(40), Util
								.getYScaledValue(330), Util
								.getXScaledValue(140), Util
								.getYScaledValue(375)), null);
				Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
				p.setTextSize(Util.getYScaledValue(28));
				p.setColor(Color.RED);
				canvas.drawText(seconds + "Ãë", Util.getXScaledValue(170),
						Util.getYScaledValue(365), p);

				Bitmap confirmBm = pm.getConfirmBm(context);
				canvas.drawBitmap(
						confirmBm,
						null,
						new Rect(Util.getXScaledValue(150), Util
								.getYScaledValue(415), Util
								.getXScaledValue(235), Util
								.getYScaledValue(455)), null);
			}

		};
		PopupWindow window = new PopupWindow(background,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		background.setOnTouchListener(new TouchListener(window));
		window.setContentView(background);
		window.showAtLocation(background, Gravity.CENTER, 0, Util.getYScaledValue(-50));
		return window;
	}

	public static PopupWindow showTimeoutWindow(final Context context) {
		SystemState.state.set(SystemState.GAME_STATUS,
				SystemState.GAME_NOT_STARTED);
		ImageView background = new ImageView(context) {
			public void onDraw(Canvas canvas) {
				NotificationBoard.drawBackground(canvas, context);
				PictureManager pm = PictureManager.getInstance();

				Bitmap timeout = pm.getTimeoutBm(context);
				drawFailureHeader(context, canvas);
				canvas.drawBitmap(
						timeout,
						null,
						new Rect(Util.getXScaledValue(50), Util
								.getYScaledValue(300), Util
								.getXScaledValue(120), Util
								.getYScaledValue(340)), null);

				Bitmap confirmBm = pm.getConfirmBm(context);
				canvas.drawBitmap(
						confirmBm,
						null,
						new Rect(Util.getXScaledValue(150), Util
								.getYScaledValue(415), Util
								.getXScaledValue(235), Util
								.getYScaledValue(455)), null);
			}
		};
		PopupWindow window = new PopupWindow(background,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		background.setOnTouchListener(new TouchListener(window));
		window.setContentView(background);
		window.showAtLocation(background, Gravity.CENTER, 0, Util.getYScaledValue(-50));
		return window;
	}

	public static PopupWindow showFailureWindow(final Context context) {
		SystemState.state.set(SystemState.GAME_STATUS,
				SystemState.GAME_NOT_STARTED);
		ImageView background = new ImageView(context) {
			public void onDraw(Canvas canvas) {
				NotificationBoard.drawBackground(canvas, context);
				PictureManager pm = drawFailureHeader(context, canvas);

				drawFailureHeader(context, canvas);
				Bitmap opponentClearAllBm = pm
						.getOpponentClearAllPicsBM(context);
				canvas.drawBitmap(
						opponentClearAllBm,
						null,
						new Rect(Util.getXScaledValue(50), Util
								.getYScaledValue(300), Util
								.getXScaledValue(210), Util
								.getYScaledValue(350)), null);

				Bitmap confirmBm = pm.getConfirmBm(context);
				canvas.drawBitmap(
						confirmBm,
						null,
						new Rect(Util.getXScaledValue(150), Util
								.getYScaledValue(415), Util
								.getXScaledValue(235), Util
								.getYScaledValue(455)), null);
			}

		};
		PopupWindow window = new PopupWindow(background,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		background.setOnTouchListener(new TouchListener(window));
		window.setContentView(background);
		window.showAtLocation(background, Gravity.CENTER, 0, Util.getYScaledValue(-50));
		return window;
	}

	public static PopupWindow showOpponentTimeOut(final Context context) {
		SystemState.state.set(SystemState.GAME_STATUS,
				SystemState.GAME_NOT_STARTED);
		ImageView background = new ImageView(context) {
			public void onDraw(Canvas canvas) {
				NotificationBoard.drawBackground(canvas, context);
				PictureManager pm = PictureManager.getInstance();
				drawWinningHeader(context, canvas);
				Bitmap oppoenentTimeout = pm.getOpponentTimeoutBm(context);
				canvas.drawBitmap(
						oppoenentTimeout,
						null,
						new Rect(Util.getXScaledValue(50), Util
								.getYScaledValue(300), Util
								.getXScaledValue(150), Util
								.getYScaledValue(340)), null);

				Bitmap confirmBm = pm.getConfirmBm(context);
				canvas.drawBitmap(
						confirmBm,
						null,
						new Rect(Util.getXScaledValue(150), Util
								.getYScaledValue(415), Util
								.getXScaledValue(235), Util
								.getYScaledValue(455)), null);
			}
		};
		PopupWindow window = new PopupWindow(background,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		background.setOnTouchListener(new TouchListener(window));
		window.setContentView(background);
		window.showAtLocation(background, Gravity.CENTER, 0, Util.getYScaledValue(-50));
		return window;
	}

	private static void drawWinningHeader(final Context context, Canvas canvas) {
		PictureManager pm = PictureManager.getInstance();
		Bitmap bucketWin = pm.getBucketWinBm(context);
		Bitmap win = pm.getWinBm(context);
		canvas.drawBitmap(bucketWin, null,
				new Rect(Util.getXScaledValue(20), Util.getYScaledValue(40),
						Util.getXScaledValue(270), Util.getYScaledValue(200)),
				null);
		canvas.drawBitmap(win, null,
				new Rect(Util.getXScaledValue(240), Util.getYScaledValue(60),
						Util.getXScaledValue(380), Util.getYScaledValue(150)),
				null);
	}

	private static PictureManager drawFailureHeader(final Context context,
			Canvas canvas) {
		PictureManager pm = PictureManager.getInstance();
		Bitmap bucketLose = pm.getBucketLoseBm(context);
		Bitmap lose = pm.getLoseBm(context);

		canvas.drawBitmap(bucketLose, null,
				new Rect(Util.getXScaledValue(30), Util.getYScaledValue(60),
						Util.getXScaledValue(300), Util.getYScaledValue(200)),
				null);

		canvas.drawBitmap(lose, null,
				new Rect(Util.getXScaledValue(270), Util.getYScaledValue(60),
						Util.getXScaledValue(380), Util.getYScaledValue(150)),
				null);
		return pm;
	}
}

class TouchListener implements OnTouchListener {

	PopupWindow window;

	public TouchListener(PopupWindow window) {
		this.window = window;
	}

	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (x >= Util.getXScaledValue(140) && x <= Util.getXScaledValue(245)) {
			if (y >= Util.getYScaledValue(405)
					&& y <= Util.getYScaledValue(465)) {
				if (window != null)
					window.dismiss();
				Kyodai kyodai = (Kyodai) SystemState.state
						.get(SystemState.GAME_ACTIVITY);
				kyodai.endGame();
			}
		}
		return false;
	}

}