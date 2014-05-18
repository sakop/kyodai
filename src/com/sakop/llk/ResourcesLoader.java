package com.sakop.llk;

import android.app.Activity;
import android.util.Pair;

import com.sakop.llk.algo.MapManager;

public class ResourcesLoader {
	public static void init(Activity activity) {
		SystemState state = SystemState.state;
		Pair<Integer, Integer> pair = MapManager.getResolution(activity);
		state.set(SystemState.SCREEN_WIDTH, pair.first);
		state.set(SystemState.SCREEN_HEIGHT, pair.second);
		state.set(SystemState.GAME_STATUS, SystemState.GAME_NOT_STARTED);
		SystemState.state.set(SystemState.GAME_ACTIVITY, activity);
	}
	
	// still needs modification
	public static int getImageIndex() {
		return (int) (Math.random() * 9);
	}
}
