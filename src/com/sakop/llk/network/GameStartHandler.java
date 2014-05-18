package com.sakop.llk.network;

import android.os.Handler;
import android.os.Message;
import android.widget.PopupWindow;

import com.sakop.llk.GameMessageHandler;
import com.sakop.llk.R;

public class GameStartHandler extends Handler {

	private NetworkComponent component;

	public GameStartHandler(NetworkComponent component) {
		this.component = component;
	}

	public void handleMessage(Message message) {
		if (message.what == 0x123) {
			PopupWindow window = component.window;
			if (window != null)
				window.dismiss();
			Message msg = new Message();
			msg.what = 0x11111;
			msg.obj = message.obj;
			GameMessageHandler.getInstance().sendMessage(message);
		}
	}

}
