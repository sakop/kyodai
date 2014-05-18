package com.sakop.llk.network;

import java.net.Socket;
import java.util.Timer;

import android.os.Handler;
import android.widget.PopupWindow;

import com.sakop.llk.R;
import com.sakop.llk.activities.Kyodai;

public class NetworkComponent {
	protected Timer timer;
	protected PopupWindow window;
	protected Socket socket;
	protected Kyodai context;
	protected Handler handler;
	protected String hintText,hintText2,hintText3;
}
