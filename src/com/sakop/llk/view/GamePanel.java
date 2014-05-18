package com.sakop.llk.view;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sakop.llk.GameMessageHandler;
import com.sakop.llk.MusicManager;
import com.sakop.llk.PictureManager;
import com.sakop.llk.R;
import com.sakop.llk.SettingWindow;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.algo.GUIConstant;
import com.sakop.llk.algo.Rules;
import com.sakop.llk.algo.TwoElementsQueue;
import com.sakop.llk.network.InformationConstants;
import com.sakop.llk.network.Parcel;
import com.sakop.llk.pojo.GameInfo;

public class GamePanel extends GameBoard {

	public Rect selected;
	public TwoElementsQueue queue = new TwoElementsQueue();
	public static int snailWidth = 25;
	public Timer hop;
	public boolean isHoppable = false;

	Handler handler = new Handler(new Callback() {

		public boolean handleMessage(Message msg) {
			// 成功消除一堆，调整蜗牛位置，调整剩余牌数
			if (msg.what == 0x00) {
				drawStatus();
				drawSnail(width / 2 - Util.getXScaledValue(snailWidth));
				Iterator<Rect> iterator = toBeCleaned.iterator();
				for (; iterator.hasNext();) {
					Rect rect = iterator.next();
					rect.left += displacement;
					rect.right += displacement;
					repaintThisArea(rect);
					iterator.remove();
				}
			}
			// 关闭笑脸
			else if (msg.what == 0x102) {
				kyodai.topLayout.removeView(faceLayout);
				isTouchable = true;
			} else if (msg.what == 0x103) {
				kyodai.topLayout.addView(faceLayout);
			}
			return false;
		}
	});

	private ExplosionView explosion;

	// 游戏还没开始时，touchable为fale
	public boolean isTouchable = false;

	public void setToucable() {
		this.isTouchable = true;
	}

	public GamePanel(Kyodai context, final Bitmap bg) {
		super(context, bg);
		preparePaint();
		addClick();
		explosion = new ExplosionView(context, holder);
		info = new GameInfo();
	}

	public Pair<Integer, Integer> mapCoordinationToIndex(MotionEvent event,
			int i) {
		int picSize = SystemState.state.getPicSize();
		int x = (int) ((event.getX(i) - displacement) / picSize);
		int y = (int) (event.getY(i) / picSize);
		return new Pair<Integer, Integer>(x, y);
	}

	// 尝试消除一对相同的pair
	public void eliminatePairs(CardView first, CardView second) {
		LinkedList<Point> path = null;
		int picSize = SystemState.state.getPicSize();
		CardView[][] cards = info.cards;
		if ((path = Rules.getPath(cards, first, second)) != null) {
			isHoppable = false;
			if (hop != null)
				hop.cancel();
			selected = null;
			currentLine = path;
			cards[first.y][first.x] = null;
			cards[second.y][second.x] = null;

			int x1 = first.x * picSize + displacement;
			int y1 = first.y * picSize;
			int x2 = second.x * picSize + displacement;
			int y2 = second.y * picSize;

			repaintThisArea(bg, x1, y1, picSize, picSize);
			repaintThisArea(bg, x2, y2, picSize, picSize);

			// 先消去两个图形，再画线
			drawLine();
			// 画烟雾炸弹
			playElec();

			info.me.cardCount -= 2;

			ThreadPoolExecutor exec = (ThreadPoolExecutor) SystemState.state
					.get(SystemState.THREAD_POOL);
			if (SystemState.isNetworkGame) {
				exec.execute(new Runnable() {
					public void run() {
						ObjectOutputStream bos = (ObjectOutputStream) SystemState.state
								.get(SystemState.OUPUTSTREAM);
						Parcel parcel = new Parcel();
						parcel.type = InformationConstants.CARD_COUNT;
						parcel.extraInfo = info.me.cardCount;
						try {
							bos.writeObject(parcel);
							bos.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

			}
			explosion.startDraw(x1, y1, x2, y2);
			Message message = new Message();
			message.what = 0x00;
			handler.sendMessage(message);

			queue.clear();
			if (info.me.cardCount == 0) {
				// game victory
				if (kyodai.task != null)
					kyodai.task.cancel();
				kyodai.gameOverWindow =  GameOverView.showVictoryWindow(getContext());
				((MediaPlayer) SystemState.state.get(SystemState.CURRENT_MUSIC))
						.stop();
				MusicManager.win.start();
			}
			if (info.me.cardCount == 82) {
			}
		}
	}

	public void showSmileFace() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ThreadPoolExecutor exec = (ThreadPoolExecutor) SystemState.state
						.get(SystemState.THREAD_POOL);
				if (faceLayout == null) {
					makeFace();
				}
				if (hop != null) {
					unselect();

				}
				isTouchable = false;
				handler.sendEmptyMessage(0x103);
				exec.execute(new Runnable() {
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0x102);
					}
				});
			}
		},0);
	}

	private void makeFace() {
		faceLayout = new LinearLayout(kyodai);
		faceLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		faceLayout.setGravity(Gravity.CENTER);
		ImageView view = new ImageView(kyodai) {
			public void onDraw(Canvas canvas) {
				Pair<Bitmap, Bitmap> smile1 = PictureManager.getInstance()
						.getSmileFace1(kyodai);
				Rect dst1 = new Rect(0, 0, Util.getYScaledValue(170), Util.getYScaledValue(340));
				Rect dst2 = new Rect(Util.getYScaledValue(170), 0, Util.getYScaledValue(340), Util.getYScaledValue(340));
				canvas.drawBitmap(smile1.first, null, dst1, null);
				canvas.drawBitmap(smile1.second, null, dst2, null);
			}
		};
		view.setLayoutParams(new ViewGroup.LayoutParams(Util.getYScaledValue(340), Util.getYScaledValue(340)));
		faceLayout.addView(view);
	}

	LinearLayout faceLayout;

	private void addClick() {
		setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (((Integer) (SystemState.state.get(SystemState.GAME_STATUS)) != SystemState.GAME_PLAYING))
					return false;

				if (!isTouchable){
					return false;
				}
				
				if (event.getAction() != MotionEvent.ACTION_DOWN) {
					return false;
				}

				// 功能键的范围内
				if (event.getY() <= Util.getYScaledValue(120)) {
					// 按了返回键或者设置键
					dealUserInput(event.getX(), event.getY());
					return false;
				}

				Pair<Integer, Integer> indexPair = mapCoordinationToIndex(
						event, 0);
				int x = indexPair.first;
				int y = indexPair.second;
				CardView[][] cards = info.cards;
				CardView selection = cards[y][x];
				// not card
				if (selection == null)
					return false;
				queue.enque(selection);
				if (queue.size() == 2) {
					CardView first = queue.getFirst();
					CardView second = queue.getSecond();
					// 如果用户点了2下同一个，取消
					if (first.equals(second)) {
						queue.clear();
						unselect();
						return false;
					}
					if (first.getBackground() == second.getBackground()) {
						eliminatePairs(first, second);
						return false;
					}
				}

				// 清除前一个选中的
				if (selected != null) {
					unselect();
				}

				select(x, y);
				MusicManager.choose.start();
				return false;
			}

		});
	}

	private void playElec() {
		if (MusicManager.elec2.isPlaying())
			MusicManager.elec.start();
		else
			MusicManager.elec2.start();
	}

	private void select(final int x, final int y) {
		selected = Util.getRect(x, y, SystemState.state.getPicSize(), 0);
		selected.left += displacement;
		selected.right += displacement;
		hop = new Timer();
		hop.schedule(new TimerTask() {
			int isBig = 0;

			@Override
			public void run() {
				isHoppable = true;
				CardView[][] cardViews = info.cards;
				Bitmap bm = PictureManager.getInstance().getCards(kyodai,
						cardViews[y][x].getBackground());
				Pair<Bitmap, Rect> obj = new Pair<Bitmap, Rect>(bm, selected);
				Message message = new Message();
				message.what = 0x1135;
				message.obj = obj;
				if (isBig == 1) {
					message.arg1 = 0;
					isBig = 0;
				} else {
					message.arg1 = 1;
					isBig = 1;
				}
				GameMessageHandler.getInstance().sendMessage(message);
			}
		}, 0, 250);

	}

	public void unselect() {
		if (selected == null)
			return;
		hop.cancel();
		hop = null;
		CardView previousSelected = GamePanel.this.info.cards[selected.top
				/ cardSize][(selected.left - displacement) / cardSize];
		repaintThisArea(selected);
		Canvas canvas = holder.lockCanvas(selected);
		PictureManager pm = PictureManager.getInstance();

		Rect rect = Util.getRect(previousSelected.x, previousSelected.y,
				cardSize, 5);
		rect.left += displacement;
		rect.right += displacement;
		canvas.drawBitmap(
				pm.getCards(getContext(), previousSelected.getBackground()),
				null, rect, null);
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(canvas);
		selected = null;
	}

	// 工具按钮的处理
	protected void dealUserInput(float x2, float y2) {
		if (x2 >= Util.getXScaledValue(5) && x2 <= Util.getXScaledValue(80)) {
			if (y2 >= Util.getYScaledValue(30)
					&& y2 <= Util.getYScaledValue(90))
				// 返回键
				kyodai.showExitTips(true);
		} else if (x2 >= Util.getXScaledValue(475)
				&& x2 <= Util.getXScaledValue(535)) {
			if (y2 >= Util.getYScaledValue(30)
					&& y2 <= Util.getYScaledValue(90)) {
				// 功能键
				kyodai.task.cancel();
				SystemState.state.set(SystemState.GAME_STATUS,
						SystemState.GAME_PAUSE);
				kyodai.settingWindow = SettingWindow
						.showSettingPopUp(getContext());
			}
		}
	}

	protected void preparePaint() {
		super.preparePaint();
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.YELLOW);

		wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		wordPaint.setStyle(Style.STROKE);
		wordPaint.setStrokeWidth(1);
		wordPaint.setTextSize(Util.getYScaledValue(32));
		wordPaint.setColor(Color.MAGENTA);
	}

	private Paint linePaint;
	private Vector<Rect> toBeCleaned = new Vector<Rect>();

	private Path makeSawTooth(int x1, int x2, int y1, int y2) {
		return makeSawToothByPhase(x1, x2, y1, y2, Util.getYScaledValue(10));
	}

	private Path makeSawToothByPhase(int x1, int x2, int y1, int y2, int phase) {
		Path p = new Path();
		p.moveTo(x1, y1);
		if (x1 == x2) {
			int times = Math.abs((int) ((y1 - y2) / 2));
			for (int i = 1; i < times - 1; i++) {
				int amplitude = (int) (Math.random() * phase);
				int abs = (int) (Math.random() * 2);
				// 向左振幅
				if (abs == 0)
					amplitude = 0 - amplitude;
				if (y1 < y2)
					p.lineTo(x1 + amplitude, y1 + i * 2);
				else
					p.lineTo(x1 + amplitude, y1 - i * 2);
			}
			p.lineTo(x2, y2);
		} else if (y1 == y2) {
			int times = Math.abs((int) ((x1 - x2) / 2));
			for (int i = 1; i < times - 1; i++) {
				int amplitude = (int) (Math.random() * phase);
				int abs = (int) (Math.random() * 2);
				// 向左振幅
				if (abs == 0)
					amplitude = 0 - amplitude;
				if (x1 < x2)
					p.lineTo(x1 + i * 2, y1 + amplitude);
				else
					p.lineTo(x1 - i * 2, y1 + amplitude);
			}
			p.lineTo(x2, y2);
		}
		return p;
	}

	public void drawLine() {
		Path path = new Path();
		toBeCleaned.addAll(getInvalidateRectangles(currentLine));
		Canvas canvas = holder.lockCanvas();
		// one line
		if (currentLine.size() == 2) {
			Point p1 = currentLine.get(0);
			Point p2 = currentLine.get(1);
			path = makeSawTooth(p1.x + displacement, p2.x + displacement, p1.y,
					p2.y);
			canvas.drawPath(path, linePaint);
		}

		// two lines,the first point is the transition point
		if (currentLine.size() == 3) {
			Point center = currentLine.get(0);
			Point p1 = currentLine.get(1);
			Point p2 = currentLine.get(2);
			path = makeSawTooth(p1.x + displacement, center.x + displacement,
					p1.y, center.y);
			canvas.drawPath(path, linePaint);
			path = makeSawTooth(center.x + displacement, p2.x + displacement,
					center.y, p2.y);
			canvas.drawPath(path, linePaint);
		}

		// three lines,the first and the second point are two transition point
		if (currentLine.size() == 4) {
			Point center = currentLine.get(0);
			Point center2 = currentLine.get(1);
			Point p1 = currentLine.get(2);
			Point p2 = currentLine.get(3);
			path = makeSawTooth(p1.x + displacement, center2.x + displacement,
					p1.y, center2.y);
			canvas.drawPath(path, linePaint);
			path = makeSawTooth(center2.x + displacement, center.x
					+ displacement, center2.y, center.y);
			canvas.drawPath(path, linePaint);
			path = makeSawTooth(center.x + displacement, p2.x + displacement,
					center.y, p2.y);
			canvas.drawPath(path, linePaint);
		}
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect());
		holder.unlockCanvasAndPost(canvas);
		currentLine = null;
	}

	public Rect getPassableRect(int x1, int x2, int y1, int y2) {
		Rect rect = new Rect();
		int picSize = SystemState.state.getPicSize();
		if (x1 == x2) {
			int x = x1;
			int yMin = y1 > y2 ? y2 : y1;
			int yMax = y1 > y2 ? y1 : y2;
			rect.left = x - picSize / 2;
			rect.top = yMin - picSize / 2;
			rect.right = x + picSize / 2;
			rect.bottom = yMax + picSize / 2;
		} else if (y1 == y2) {
			int y = y1;
			int xMin = x1 > x2 ? x2 : x1;
			int xMax = x1 > x2 ? x1 : x2;
			rect.left = xMin - picSize / 2;
			rect.top = y - picSize / 2;
			rect.right = xMax + picSize / 2;
			rect.bottom = y + picSize / 2;
		}
		return rect;
	}

	private Vector<Rect> getInvalidateRectangles(List<Point> currentLine) {
		Vector<Rect> ret = new Vector<Rect>();
		if (currentLine.size() == 2) {
			Point p1 = currentLine.get(0);
			Point p2 = currentLine.get(1);
			ret.add(getPassableRect(p1.x, p2.x, p1.y, p2.y));
		} else if (currentLine.size() == 3) {
			Point center = currentLine.get(0);
			Point p1 = currentLine.get(1);
			Point p2 = currentLine.get(2);
			ret.add(getPassableRect(p1.x, center.x, p1.y, center.y));
			ret.add(getPassableRect(center.x, p2.x, center.y, p2.y));
		} else if (currentLine.size() == 4) {
			Point center = currentLine.get(0);
			Point center2 = currentLine.get(1);
			Point p1 = currentLine.get(2);
			Point p2 = currentLine.get(3);
			ret.add(getPassableRect(p1.x, center2.x, p1.y, center2.y));
			ret.add(getPassableRect(center2.x, center.x, center2.y, center.y));
			ret.add(getPassableRect(center.x, p2.x, center.y, p2.y));
		}
		return ret;
	}

	final int x = 400;
	final int y = 900;

	public void drawBackground() {
		// 去掉原始背景
		setBackgroundResource(0);

		PictureManager pm = PictureManager.getInstance();
		Bitmap temp = bg;
		bg = Bitmap.createBitmap(bg, 315, 0, 325, 480);
		temp.recycle();
		Canvas canvas = holder.lockCanvas();
		canvas.drawBitmap(bg, null, new Rect(0, 0, width / 2, height), null);
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect());
		holder.unlockCanvasAndPost(canvas);

		// 再画状态栏，还包括了无头像和蜗牛的获取
		if (pm.getImage(PictureManager.SNAIL) == null) {
			Bitmap statusBm = pm.getCachedBitmap(getContext(),
					R.drawable.status);
			Bitmap realStatusBm = Bitmap
					.createBitmap(statusBm, 320, 0, 600, 90);
			Bitmap snail = Bitmap.createBitmap(statusBm, 105, 310, 45, 40);
			Bitmap nobody = Bitmap.createBitmap(statusBm, 105, 350, 45, 45);
			pm.addImage(PictureManager.SNAIL, snail);
			pm.addImage(PictureManager.NOBODY, nobody);
			pm.addImage(PictureManager.STATUS_BACKGROUND, realStatusBm);

			Bitmap vsBm = pm.getCachedBitmap(getContext(), R.drawable.vs);
			Bitmap vs = Bitmap.createBitmap(vsBm, 490, 330, 50, 65);
			vsBm.recycle();
			pm.addImage(PictureManager.VS, vs);
		}
	}

	public int snailPosition;
	private Paint wordPaint;

	public int getSnailPosition() {
		return snailPosition;
	}

	public void redrawSnail(int snailPosition) {
		this.snailPosition = snailPosition;
		Bitmap snail = PictureManager.getInstance().getImage(
				PictureManager.SNAIL);
		Canvas canvas = holder.lockCanvas(new Rect(0, 0, width, Util
				.getYScaledValue(120)));
		// 进度条的蜗牛
		// 当正在重绘蜗牛，却按了回退键时，会产生不想要的异常
		try {
			canvas.drawBitmap(
					snail,
					null,
					new Rect(snailPosition, Util.getYScaledValue(80),
							snailPosition + Util.getXScaledValue(40), Util
									.getYScaledValue(120)), null);
			holder.unlockCanvasAndPost(canvas);
			holder.lockCanvas(new Rect());
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
		}
	}

	// 画状态栏
	public void drawStatus() {
		Canvas canvas = holder.lockCanvas(new Rect(0, 0, width, Util
				.getYScaledValue(120)));
		PictureManager pm = PictureManager.getInstance();
		Bitmap realStatusBm = pm.getImage(PictureManager.STATUS_BACKGROUND);

		int index = Util.getHeaderImageIndex(getContext());
		Bitmap headerImage = PictureManager.getInstance().getHeaderImage(
				getContext(), index);

		Bitmap vs = pm.getImage(PictureManager.VS);

		// 先画状态栏
		canvas.drawBitmap(realStatusBm, null,
				new Rect(0, 0, width, Util.getYScaledValue(120)), null);
		// 然后是头像
		canvas.drawBitmap(headerImage, null,
				new Rect(Util.getXScaledValue(155), Util.getYScaledValue(40),
						Util.getXScaledValue(200), Util.getYScaledValue(85)),
				null);
		// vs
		canvas.drawBitmap(vs, null,
				new Rect(Util.getXScaledValue(255), Util.getYScaledValue(35),
						Util.getXScaledValue(305), Util.getYScaledValue(95)),
				null);

		int enemyImage = info.enemy.headerImage;
		// 无对战时敌人头像
		if (enemyImage == -1) {
			Bitmap nobody = pm.getImage(PictureManager.NOBODY);
			canvas.drawBitmap(
					nobody,
					null,
					new Rect(Util.getXScaledValue(350), Util
							.getYScaledValue(35), Util.getXScaledValue(405),
							Util.getYScaledValue(80)), null);
		}
		// 有对战时敌人头像和敌人的比分
		else {
			canvas.drawBitmap(
					pm.getHeaderImage(getContext(), enemyImage),
					null,
					new Rect(Util.getXScaledValue(350), Util
							.getYScaledValue(40), Util.getXScaledValue(405),
							Util.getYScaledValue(85)), null);
			// 敌方还剩下多少张牌
			canvas.drawText(getRemainingCardsText(info.enemy.cardCount),
					Util.getXScaledValue(435), Util.getYScaledValue(70),
					wordPaint);
		}

		// 话返回和设置按钮
		Bitmap backBm = PictureManager.getInstance().getBackButtonImage(
				getContext());
		canvas.drawBitmap(backBm, null,
				new Rect(Util.getXScaledValue(5), Util.getYScaledValue(40),
						+Util.getXScaledValue(65), Util.getYScaledValue(75)),
				null);

		Bitmap settingBm = PictureManager.getInstance().getSettingButtonImage(
				getContext());
		canvas.drawBitmap(settingBm, null,
				new Rect(Util.getXScaledValue(490), Util.getYScaledValue(30),
						Util.getXScaledValue(535), Util.getYScaledValue(75)),
				null);

		// 我方还剩下多少张牌
		canvas.drawText(getRemainingCardsText(info.me.cardCount),
				Util.getXScaledValue(90), Util.getYScaledValue(70), wordPaint);

		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect());
		holder.unlockCanvasAndPost(canvas);
	}

	public void drawSnail(int snailPosition) {
		this.snailPosition = snailPosition;
		Canvas canvas = holder.lockCanvas(new Rect(0, 0, width, Util
				.getYScaledValue(120)));
		Bitmap snail = PictureManager.getInstance().getImage(
				PictureManager.SNAIL);
		// 进度条的蜗牛
		canvas.drawBitmap(
				snail,
				null,
				new Rect(snailPosition, Util.getYScaledValue(80), snailPosition
						+ Util.getXScaledValue(snailWidth), Util
						.getYScaledValue(120)), null);
		holder.unlockCanvasAndPost(canvas);
		holder.lockCanvas(new Rect());
		holder.unlockCanvasAndPost(canvas);
	}

	private String getRemainingCardsText(int count) {
		if (count >= 10)
			return String.valueOf(count);
		else
			return "0" + String.valueOf(count);
	}

	public void rearrange() {
		if (hop != null)
			hop.cancel();
		CardView[][] cards = info.cards;
		for (int i = 0; i < 100; i++) {
			int x1 = (int) (Math.random() * (GUIConstant.HORIZONTAL_COUNT + 4));
			int y1 = (int) (Math.random() * (GUIConstant.VERTICAL_COUNT + 4));

			int x2 = (int) (Math.random() * (GUIConstant.HORIZONTAL_COUNT + 4));
			int y2 = (int) (Math.random() * (GUIConstant.VERTICAL_COUNT + 4));

			CardView c1 = cards[y1][x1];
			CardView c2 = cards[y2][x2];
			if (c1 != null && c2 != null) {
				CardView temp = c1;
				cards[y1][x1] = c2;
				cards[y2][x2] = temp;
				CardView.exchangePosition(c1, c2);
			}
		}
		MusicManager.rearrange.start();
		drawButtons();
	}

}
