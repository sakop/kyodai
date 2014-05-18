package com.sakop.llk.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sakop.llk.R;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.view.NotificationBoard;

public class Server extends NetworkComponent {
	public static int SERVER_PORT = 22345;
	ServerSocket ss;
	NetworkPreparationImage image;

	public Server(Kyodai context) {
		this.context = context;
		hintText = "等待玩家加入中";
	}

	public void start() {
		handler = new GameStartHandler(this);
		try {
			ss = new ServerSocket();
			ss.setSoTimeout(10000);
			ss.setReuseAddress(true); // 设置ServerSocket的选项
			ss.bind(new InetSocketAddress(Server.SERVER_PORT));
		} catch (IOException e) {
			e.printStackTrace();
			if (e.getMessage().contains("Try again")) {
				Toast.makeText(context, "等待其他玩家超时", 2000);
			} else
				Toast.makeText(context, "服务器端错误:" + e.getMessage(), 2000);
		}
		showPopup();
		ThreadPoolExecutor exec = (ThreadPoolExecutor) SystemState.state
				.get(SystemState.THREAD_POOL);
		exec.execute(new ServerThread(this));
	}

	private void showPopup() {
		FrameLayout layout = new FrameLayout(context);
		layout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		image = new NetworkPreparationImage(this);
		layout.addView(image);
		window = new PopupWindow(layout,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		window.showAtLocation(layout, Gravity.CENTER, Util.getXScaledValue(0),
				Util.getYScaledValue(-50));
		setTouchListener(layout);

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (hintText.length() < 12) {
					hintText += ".";
				} else {
					hintText = "等待玩家加入中";
				}
				image.setHintText(hintText);
				image.postInvalidate();
			}
		}, 0, 200);
	}

	private void setTouchListener(FrameLayout layout) {
		layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				int height = NotificationBoard.HEIGHT;
				if (x >= Util.getXScaledValue(140)
						&& x <= Util.getXScaledValue(260)) {
					if (y >= Util.getYScaledValue(height - 120)
							&& y <= Util.getYScaledValue(height - 40)) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						try {
							if (ss != null)
								ss.close();
						} catch (IOException e) {
						}
						window.dismiss();
					}
				}
				return false;
			}
		});
	}
}
