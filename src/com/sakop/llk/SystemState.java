package com.sakop.llk;

import java.util.HashMap;
import java.util.Map;

import com.sakop.llk.algo.GUIConstant;

public class SystemState {

//	public static final String CARDS = "cards";
	public static final String STATUS_HEIGHT = "status_height";
	public static final String TITLE_HEIGHT = "title_height";
	public static final String GAME_ACTIVITY = "game_activity";
	public static final String PIC_SIZE = "picsize";
	public static final String SCREEN_WIDTH = "screen witdh";
	public static final String SCREEN_HEIGHT = "screen height";
	public static final String GAME_STATUS = "game status";
	public static final int GAME_NOT_STARTED = 0;
	public static final int GAME_PLAYING = 1;
	public static final int GAME_PAUSE = 2;
	public static final String GAME_PANEL = "game_panel";
	public static final String OUPUTSTREAM = "outputStream";
	public static final String ENEMY_IMAGE = "enemy_image";
	public static final String COMMONTHREAD = "commonThread";
	public static final String GAME_START_TIME = "game start time";
	public static final String GAME_HOLDER = "game holder";
	public static final String THREAD_POOL = "thread pool";
	public static final String CURRENT_MUSIC = "current music";

	private SystemState() {
	}
	
	public static boolean isNetworkGame = false;
	public static boolean isServer = false;

	public static final SystemState state = new SystemState();
	private Map<String, Object> map = new HashMap<String, Object>();

	public void set(String attr, Object value) {
		map.put(attr, value);
	}

	public Object get(String attr) {
		return map.get(attr);
	}

	public int getScreenWidth() {
		return (Integer) map.get(SCREEN_WIDTH);
	}

	public int getScreenHeight() {
		return (Integer) map.get(SCREEN_HEIGHT);
	}

	public int getPicSize() {
		return getScreenHeight() / (GUIConstant.VERTICAL_COUNT + 4);
	}
	
	public int getGameStatus(){
		return (Integer) map.get(GAME_STATUS);
	}
}
