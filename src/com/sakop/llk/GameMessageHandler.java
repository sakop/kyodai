package com.sakop.llk;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.network.InformationConstants;
import com.sakop.llk.network.Parcel;
import com.sakop.llk.view.GameOverView;
import com.sakop.llk.view.GamePanel;

public class GameMessageHandler extends Handler {

	private static GameMessageHandler instance = new GameMessageHandler();

	private GameMessageHandler() {
	}

	public static GameMessageHandler getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void handleMessage(Message msg) {
		GamePanel panel = (GamePanel) SystemState.state
				.get(SystemState.GAME_PANEL);
		final Kyodai kyodai = (Kyodai) SystemState.state
				.get(SystemState.GAME_ACTIVITY);
		if (msg.what == 0x1135) {
			if(!panel.isHoppable)
				return;
			SurfaceHolder holder = (SurfaceHolder) SystemState.state
					.get(SystemState.GAME_HOLDER);
			Pair<Bitmap, Rect> obj = (Pair<Bitmap, Rect>) msg.obj;
			Bitmap current = obj.first;
			Rect rect = obj.second;
			panel.repaintThisArea(rect);
			Canvas canvas = holder.lockCanvas(rect);
			if (msg.arg1 == 1)
				canvas.drawBitmap(current, null, rect, null);
			else {
				Rect newRect = new Rect();
				newRect.left = rect.left + Util.getYScaledValue(5);
				newRect.right = rect.right - Util.getYScaledValue(5);
				newRect.top = rect.top + Util.getYScaledValue(5);
				newRect.bottom = rect.bottom - Util.getYScaledValue(5);
				canvas.drawBitmap(current, null, newRect, null);
			}

			holder.unlockCanvasAndPost(canvas);
			holder.lockCanvas(new Rect());
			holder.unlockCanvasAndPost(canvas);
		}

		// 游戏开始的信号
		else if (msg.what == 0x102) {
			panel.drawBackground();
			panel.snailPosition = panel.width / 2
					- Util.getXScaledValue(GamePanel.snailWidth);
			panel.drawStatus();
		//	panel.drawSnail(panel.snailPosition);
			panel.drawButtons();
			SystemState.state.set(SystemState.GAME_STATUS,
					SystemState.GAME_PLAYING);
			kyodai.task = new Timer();
			// 倒计时速度
			int speed = Util.getSpeed(kyodai);
			kyodai.task.schedule(new TimerTask() {
				@Override
				public void run() {
					GameMessageHandler.this.sendEmptyMessage(0x110);
				}
			}, 0, speed);

			SystemState.state.set(SystemState.GAME_START_TIME,
					SystemClock.elapsedRealtime());
		} else if (msg.what == 0x110) {
			// 倒计时
			if (panel.getSnailPosition() <= 0) {
				// 游戏失败
				// bug fix
				if (kyodai.task == null)
					return;
				// 取消时钟，然后设置为null
				kyodai.task.cancel();
				kyodai.task = null;
				kyodai.gameOverWindow = GameOverView.showTimeoutWindow(kyodai);
				//
				if (SystemState.isNetworkGame) {
					ObjectOutputStream bos = (ObjectOutputStream) SystemState.state
							.get(SystemState.OUPUTSTREAM);
					Parcel p = new Parcel();
					p.type = InformationConstants.OVERTIME;
					try {
						bos.writeObject(p);
						bos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				SystemState.state.set(SystemState.GAME_STATUS,
						SystemState.GAME_NOT_STARTED);
				((MediaPlayer)SystemState.state.get(SystemState.CURRENT_MUSIC)).stop();
				MusicManager.lose.start();

				return;
			}
			//panel.redrawSnail(panel.getSnailPosition()
				//	- Util.getXScaledValue(GamePanel.snailWidth));
		}
		// 客户端连接失败
		else if (msg.what == 0x12345) {
			String errorMsg = msg.obj.toString();
			Toast.makeText(kyodai, "连接错误:" + errorMsg + "，请重试", 2000).show();
		}
		// 网络堆栈开始
		else if (msg.what == 0x11111) {
			kyodai.startGame();
		}
		// 对家获胜
		else if (msg.what == 0x5) {
			panel.info.enemy.cardCount = (Integer) msg.obj;
			panel.drawStatus();
			if (panel.info.enemy.cardCount == 0) {
				if (kyodai.task != null) {
					kyodai.task.cancel();
				}
				kyodai.gameOverWindow = GameOverView.showFailureWindow(kyodai);
				((MediaPlayer)SystemState.state.get(SystemState.CURRENT_MUSIC)).stop();
				MusicManager.lose.start();
			}
		}
		// 对方
		else if (msg.what == 0x6) {
			if (kyodai.task != null) {
				kyodai.task.cancel();
			}
			kyodai.gameOverWindow = GameOverView.showOpponentTimeOut(kyodai);
			((MediaPlayer)SystemState.state.get(SystemState.CURRENT_MUSIC)).stop();
			MusicManager.win.start();
		}
	}
}
