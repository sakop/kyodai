package com.sakop.llk;

import android.app.Activity;
import android.media.MediaPlayer;

public class MusicManager {
	public static MediaPlayer elec, elec2, win, lose, rearrange, choose,
			smileFaceSound;
	static {
		Activity activity = (Activity) SystemState.state
				.get(SystemState.GAME_ACTIVITY);
		elec = MediaPlayer.create(activity, R.raw.elec);
		elec2 = MediaPlayer.create(activity, R.raw.elec);
		win = MediaPlayer.create(activity, R.raw.win);
		lose = MediaPlayer.create(activity, R.raw.lose);
		rearrange = MediaPlayer.create(activity, R.raw.reset);
		choose = MediaPlayer.create(activity, R.raw.choose);
		smileFaceSound = MediaPlayer.create(activity, R.raw.smile_face);
	}

}
