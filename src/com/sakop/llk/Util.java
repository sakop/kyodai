package com.sakop.llk;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.util.Log;
import android.view.Window;

import com.sakop.llk.algo.GUIConstant;
import com.sakop.llk.view.CardView;

public class Util {
	public static int getStatusBarHeight(Activity a) {
		if (SystemState.state.get(SystemState.STATUS_HEIGHT) != null) {
			return (Integer) SystemState.state.get(SystemState.STATUS_HEIGHT);
		}
		Rect frame = new Rect();
		a.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		SystemState.state.set(SystemState.STATUS_HEIGHT, frame.top);
		Log.d("", "Sudoku:statusHeight:" + frame.top);
		return frame.top;
	}

	public static String getHeaderImage(Context context) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		return sp.getString("headerImage", "匿名玩家");
	}

	public static String getDefaultServerIp(Context context) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		return sp.getString("serverIp", "192.168.0.104");
	}

	public static void setDefaultServerIp(Context context, String ip) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("serverIp", ip);
		editor.commit();
	}

	public static int getHeaderImageIndex(Context context) {
		String indexStr = getHeaderImage(context);
		int index = 0;
		if (indexStr.length() < 2) {
			index = Integer.parseInt(indexStr);
		}
		return index;
	}

	public static int getTitleHeight(Activity a) {
		if (SystemState.state.get(SystemState.TITLE_HEIGHT) != null) {
			return (Integer) SystemState.state.get(SystemState.TITLE_HEIGHT);
		}
		int contentTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		int statusHeight = getStatusBarHeight(a);
		SystemState.state.set(SystemState.TITLE_HEIGHT, contentTop
				- statusHeight);
		Log.d("", "Sudoku:titleHeight:" + (contentTop - statusHeight));
		return contentTop - statusHeight;
	}

	public static int getYStart(Activity a) {
		return getTitleHeight(a) + getStatusBarHeight(a);
	}

	public static Rect getRect(int x, int y, int factor,int temp) {
		return new Rect(x * factor + Util.getYScaledValue(temp), y * factor + Util.getYScaledValue(temp), 
				x * factor + factor - Util.getYScaledValue(temp), y * factor
				+ factor - Util.getYScaledValue(temp));
	}

	public static CardView getRandomCard(CardView[][] cards) {
		while (true) {
			int x = (int) (Math.random() * (GUIConstant.HORIZONTAL_COUNT + 4));
			int y = (int) (Math.random() * (GUIConstant.VERTICAL_COUNT + 4));
			if (cards[y][x] != null)
				return cards[y][x];
		}

	}

	private static float yScale = SystemState.state.getScreenHeight() * 1f / 960;

	public static int getYScaledValue(int value) {
		return (int) (yScale * value);
	}

	private static float xScale = SystemState.state.getScreenWidth() * 1f / 540;

	public static int getXScaledValue(int value) {
		return (int) (xScale * value);
	}

	public static int getScaledFontSize(int value) {
		return (int) (value / yScale);
	}

	public static void makeRandom(CardView[][] cards) {
		for (int i = 0; i < 1000; i++) {
			CardView c1 = getRandomCard(cards);
			CardView c2 = getRandomCard(cards);
			if (!c1.equals(c2)) {
				int c1X = c1.x;
				int c1Y = c1.y;
				cards[c1.y][c1.x] = c2;
				cards[c2.y][c2.x] = c1;
				c1.x = c2.x;
				c1.y = c2.y;
				c2.x = c1X;
				c2.y = c1Y;
			}
		}
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	public static void setHeaderImage(Context context, String string) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("headerImage", string);
		editor.commit();
	}

	public static String getNickName(Context context) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		return sp.getString("nickname", "匿名玩家");
	}

	public static int getSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		return sp.getInt("speed", 1000);
	}

	public static void setSpeed(Context context, int newSpeed) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("speed", newSpeed);
		editor.commit();
	}

	public static void setNickName(Context context, String newNickName) {
		SharedPreferences sp = context.getSharedPreferences("kyodai",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("nickname", newNickName);
		editor.commit();
	}

}
