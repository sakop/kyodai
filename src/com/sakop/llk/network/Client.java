package com.sakop.llk.network;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import android.app.AlertDialog;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sakop.llk.R;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.view.NotificationBoard;

public class Client extends NetworkComponent {
	String serverAddr;

	NetworkPreparationImage image;

	public Client(Kyodai context) {
		this.context = context;
		hintText = "等待主机加入中";
	}

	public void start(String serverAddr) {
		this.serverAddr = serverAddr;
		SystemState.isServer = false;
		handler = new GameStartHandler(this);
		showPopup();
		ThreadPoolExecutor exec = (ThreadPoolExecutor) SystemState.state
				.get(SystemState.THREAD_POOL);
		exec.execute(new ClientThread(this));
	}

	private void showPopup() {
		FrameLayout layout = new FrameLayout(context);
		layout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		image = new NetworkPreparationImage(this);
		hintText = "等待" + serverAddr;
		layout.addView(image);
		window = new PopupWindow(layout,
				Util.getXScaledValue(NotificationBoard.WIDTH),
				Util.getYScaledValue(NotificationBoard.HEIGHT));
		window.showAtLocation(layout, Gravity.CENTER, Util.getXScaledValue(0),
				Util.getYScaledValue(-50));
		setTouchListener(layout);

		timer = new Timer();
		final int fixedLength = new String("等待" + serverAddr).length();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (hintText.length() < fixedLength + 3) {
					hintText += ".";
				} else {
					hintText = "等待" + serverAddr;
				}
				image.setHintText(hintText);
				image.postInvalidate();
			}
		}, 0, 200);
	}

	public static void popUpServerAddressInput(final Kyodai context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setPadding(Util.getXScaledValue(10), 0, 0, 0);
		layout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		layout.setOrientation(0);
		TextView text = new TextView(context);
		text.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		text.setText("主机IP");
		final EditText editor = new EditText(context);
		editor.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
		editor.setSingleLine(true);
		editor.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		editor.setText(Util.getDefaultServerIp(context));
		Button ok = new Button(context);
		ok.setText("确定");
		ok.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
		layout.addView(text);
		layout.addView(editor);
		layout.addView(ok);
		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setView(layout).setTitle("输入主机IP").show();
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String ipAddress = editor.getText().toString();
				if (IPAddressFilter.check(ipAddress)) {
					Util.setDefaultServerIp(context, ipAddress);
					new Client(context).start(ipAddress);
					dialog.dismiss();
				} else {
					Toast.makeText(context, "请输入合法的IP地址", 500).show();
				}
			}
		});
	}

	private void setTouchListener(FrameLayout layout) {
		layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				int height = NotificationBoard.HEIGHT;
				if (x >= Util.getXScaledValue(140)
						&& x <= Util.getXScaledValue(260)) {
					if (y >= Util.getYScaledValue(height - 100)
							&& y <= Util.getYScaledValue(height - 50)) {
						if (timer != null)
							timer.cancel();
						timer = null;
						try {
							if (socket != null)
								socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						window.dismiss();
					}
				}
				return false;
			}
		});
	}
}
