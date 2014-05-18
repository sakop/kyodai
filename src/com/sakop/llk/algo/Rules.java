package com.sakop.llk.algo;

import java.util.LinkedList;

import android.graphics.Point;

import com.sakop.llk.view.CardView;

public class Rules implements GUIConstant {

	/**
	 * checks whether two buttons are reachable from each other
	 * 
	 * @param bitmap
	 * @param a
	 * @param b
	 * @return true if they're reachable
	 */
	public static LinkedList<Point> getPath(CardView[][] map,CardView a, CardView b) {
		LinkedList<Point> ret = null;
		if ((ret = sameRowOrColumn(map,a, b)) != null)
			return ret;
		if ((ret = notTheSameRowOrColumn(map,a, b)) != null)
			return ret;
		return ret;
	}

	// public static Map<LinkedList<Point>, LinkedList<MainPanel>>
	// getRandomDeletion(
	// String[][] bitmap, JPanel parent) {
	// int max = parent.getComponentCount();
	// if (max == 0)
	// return null;
	// int count = 0;
	// while (true) {
	// int r1 = (int) (Math.random() * max);
	// int r2 = (int) (Math.random() * max);
	// if (r1 == r2)
	// continue;
	// MainPanel p1 = (MainPanel) parent.getComponent(r1);
	// MainPanel p2 = (MainPanel) parent.getComponent(r2);
	// if (!p1.url.equalsIgnoreCase(p2.url))
	// continue;
	// LinkedList<Point> list = null;
	// LinkedList<MainPanel> plist = null;
	// HashMap<LinkedList<Point>, LinkedList<MainPanel>> ret = null;
	// if ((list = sameRowOrColumn(bitmap, p1, p2)) != null) {
	// plist = new LinkedList<MainPanel>();
	// plist.add(p1);
	// plist.add(p2);
	// ret = new HashMap<LinkedList<Point>, LinkedList<MainPanel>>();
	// ret.put(list, plist);
	// return ret;
	// }
	// if ((list = notTheSameRowOrColumn(bitmap, p1, p2)) != null) {
	// plist = new LinkedList<MainPanel>();
	// plist.add(p1);
	// plist.add(p2);
	// ret = new HashMap<LinkedList<Point>, LinkedList<MainPanel>>();
	// ret.put(list, plist);
	// return ret;
	// }
	// count++;
	// if (count > 100) {
	// RegisterAll.reset_mess(parent);
	// parent.repaint();
	// count = 0;
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	public static LinkedList<Point> sameRowOrColumn(CardView[][] map,CardView a, CardView b) {

		int x1 = a.x;
		int x2 = b.x;

		int y1 = a.y;
		int y2 = b.y;

		LinkedList<Point> points = null;
		if (x1 != x2 && y1 != y2)
			return null;

		boolean ok = false;

		/**
		 * ����л���һ������ô������������һ��ֱ������
		 */
		if (oneLine(map,x1, y1, x2, y2)) {
			points = new LinkedList<Point>();

			points.add(new Point(a.x, a.y));
			points.add(new Point(b.x, b.y));
			ok = true;
		}

		/**
		 * ���߿�����ת���������� ����к���һ������ô���ܻ�����Ҫ3��ֱ��
		 */
		// ת2����
		if (!ok) {
			if ((points = threeLines(map,x1, y1, x2, y2)) != null) {
				points.add(new Point(a.x, a.y));
				points.add(new Point(b.x, b.y));
			}
		}

		if (points != null)
			points = MapManager.getMiddlePoints(points);
		return points;
	}

	public static LinkedList<Point> notTheSameRowOrColumn(CardView[][] map,CardView a, CardView b) {

		int x1 = a.x;
		int x2 = b.x;

		int y1 = a.y;
		int y2 = b.y;

		// �����ͬ��ͬ�е�ְ��
		if (x1 == x2 || y1 == y2)
			return null;

		// ����ͬһ�У�����תһ����ɴ����ת2����ɴ�
		// תһ����
		LinkedList<Point> for2;
		if ((for2 = twoLines(map,x1, y1, x2, y2)) != null) {
			for2.add(new Point(a.x, a.y));
			for2.add(new Point(b.x, b.y));
			for2 = MapManager.getMiddlePoints(for2);
			return for2;
		}

		// ת2����
		LinkedList<Point> for3;
		if ((for3 = threeLines(map,x1, y1, x2, y2)) != null) {
			for3.add(new Point(a.x, a.y));
			for3.add(new Point(b.x, b.y));
			for3 = MapManager.getMiddlePoints(for3);
			return for3;
		}

		return null;
	}

	/**
	 * ����2����֮���Ƿ�ɴ�
	 * 
	 * @param map
	 *            �洢�ַ�����λͼ
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static boolean oneLine(CardView[][] map,int x1, int y1, int x2, int y2) {
		if (y1 != y2 && x1 != x2)
			return false;

		else if (x1 == x2) {
			for (int i = Math.min(y1, y2) + 1; i < Math.max(y2, y1); i++) {
				if (map[i][x1] != null) {
					return false;
				}
			}
		}

		else if (y1 == y2) {
			for (int i = Math.min(x1, x2) + 1; i < Math.max(x2, x1); i++) {
				if (map[y1][i] != null) {
					return false;
				}
			}
		}
		return true;
	}

	public static LinkedList<Point> twoLines(CardView[][] map,int x1, int y1, int x2, int y2) {

		LinkedList<Point> list = null;
		if (x1 != x2 && y1 != y2) {
			/**
			 * ���ڵ�һ���ƣ���4������ȥѰ��
			 */
			// ������,����ɴ������Ƿ�����2һ���߿ɴ�
			for (int i = y1 - 1; i > -1; i--) {
				if (map[i][x1] != null)
					continue;
				if (oneLine(map,x1, y1, x1, i)) {
					if (oneLine(map,x1, i, x2, y2)) {
						list = null;
						list = new LinkedList<Point>();
						list.add(new Point(x1, i));
						return list;
					} else
						continue;
				} else
					break;
			}

			// ������,����ɴ������Ƿ�����2һ���߿ɴ�
			for (int i = y1 + 1; i < GUIConstant.VERTICAL_COUNT + 4; i++) {
				if (map[i][x1] != null)
					continue;
				if (oneLine(map,x1, y1, x1, i)) {
					if (oneLine(map,x1, i, x2, y2)) {
						list = null;
						list = new LinkedList<Point>();
						list.add(new Point(x1, i));
						return list;
					} else
						continue;
				} else
					break;
			}

			// ������,����ɴ������Ƿ�����2һ���߿ɴ�
			for (int i = x1 - 1; i > -1; i--) {
				if (map[y1][i] != null)
					continue;
				if (oneLine(map,x1, y1, i, y1)) {
					if (oneLine(map,i, y1, x2, y2)) {
						list = null;
						list = new LinkedList<Point>();
						list.add(new Point(i, y1));
						return list;
					} else
						continue;
				} else
					break;
			}

			// ������,����ɴ������Ƿ�����2һ���߿ɴ�
			for (int i = x1 + 1; i < GUIConstant.HORIZONTAL_COUNT + 4; i++) {
				if (map[y1][i] != null)
					continue;
				if (oneLine(map,x1, y1, i, y1)) {
					if (oneLine(map,i, y1, x2, y2)) {
						list = null;
						list = new LinkedList<Point>();
						list.add(new Point(i, y1));
						return list;
					} else
						continue;
				} else
					break;
			}
			return null;
		}
		// ��ͬһ��ͬһ�в��Ǹ÷�����ְ��
		else
			return null;
	}

	public static LinkedList<Point> threeLines(CardView[][] map,int x1, int y1, int x2, int y2) {
		LinkedList<Point> ret = null;
		/**
		 * ���ڵ�һ���ƣ���4������ȥѰ��
		 */
		// ������,����ɴ������Ƿ�����2һ���߿ɴ�
		for (int i = y1 - 1; i > -1; i--) {
			if (map[i][x1] != null)
				continue;
			if (oneLine(map,x1, y1, x1, i)) {
				if ((ret = twoLines(map,x1, i, x2, y2)) != null) {
					ret.add(new Point(x1, i));
					return ret;
				} else
					continue;
			} else
				break;
		}

		// ������,����ɴ������Ƿ�����2һ���߿ɴ�
		for (int i = y1 + 1; i < GUIConstant.VERTICAL_COUNT + 4; i++) {
			if (map[i][x1] != null)
				continue;
			if (oneLine(map,x1, y1, x1, i)) {
				if ((ret = twoLines(map,x1, i, x2, y2)) != null) {
					ret.add(new Point(x1, i));
					return ret;
				} else
					continue;
			} else
				break;
		}

		// ������,����ɴ������Ƿ�����2һ���߿ɴ�
		for (int i = x1 - 1; i > -1; i--) {
			if (map[y1][i] != null)
				continue;
			if (oneLine(map,x1, y1, i, y1)) {
				if ((ret = twoLines(map,i, y1, x2, y2)) != null) {
					ret.add(new Point(i, y1));
					return ret;
				} else
					continue;
			} else
				break;
		}

		// ������,����ɴ������Ƿ�����2һ���߿ɴ�
		for (int i = x1 + 1; i < GUIConstant.HORIZONTAL_COUNT + 4; i++) {
			if (map[y1][i] != null)
				continue;
			if (oneLine(map,x1, y1, i, y1)) {
				if ((ret = twoLines(map,i, y1, x2, y2)) != null) {
					ret.add(new Point(i, y1));
					return ret;
				} else
					continue;
			} else
				break;
		}
		return null;
	}
}
