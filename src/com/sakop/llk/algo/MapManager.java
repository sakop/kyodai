package com.sakop.llk.algo;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Pair;

import com.sakop.llk.SystemState;
import com.sakop.llk.view.CardView;

public class MapManager{
	private static String[][] cur_map;
	private static MapManager manager;


	private MapManager() {
	}

	public static Pair<Integer, Integer> getResolution(Activity a) {
		DisplayMetrics dm = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得手机的宽带和高度像素单位为px
		return new Pair<Integer, Integer>(dm.widthPixels, dm.heightPixels);

	}

	public static MapManager getInstance(Activity activity) {
		if (manager != null && cur_map != null)
			return manager;
		else {
			manager = new MapManager();
			return manager;
		}
	}


	public static Point mapToLocation(int x, int y) {
		int picSize = SystemState.state.getPicSize();
		Point dim = new Point();
		dim.x = x * picSize;
		dim.y = y * picSize;
		return dim;
	}

	public static Point mapToLocation(CardView cv) {
		int picSize = SystemState.state.getPicSize();
		Point dim = new Point();
		dim.x = cv.x * picSize;
		dim.y = cv.y * picSize;
		return dim;
	}

	public static Point mapToLocation(Point dim) {
		return mapToLocation(dim.x, dim.y);
	}



	public static LinkedList<Point> getMiddlePoints(LinkedList<Point> points) {
		LinkedList<Point> list = new LinkedList<Point>();
		int picSize = SystemState.state.getPicSize();
		for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
			Point name = iterator.next();
			name = MapManager.mapToLocation(name);
			name.x += picSize / 2;
			name.y += picSize / 2;
			list.add(name);
		}
		return list;
	}
}
