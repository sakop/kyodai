package com.sakop.llk.activities;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.sakop.llk.GameMessageHandler;
import com.sakop.llk.MusicManager;
import com.sakop.llk.PictureManager;
import com.sakop.llk.R;
import com.sakop.llk.ResourcesLoader;
import com.sakop.llk.SettingWindow;
import com.sakop.llk.ShakeListener;
import com.sakop.llk.ShakeListener.OnShakeListener;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.algo.GUIConstant;
import com.sakop.llk.algo.MapManager;
import com.sakop.llk.gestures.OnGestureAdapter;
import com.sakop.llk.network.Client;
import com.sakop.llk.network.CommonThread;
import com.sakop.llk.network.InformationConstants;
import com.sakop.llk.network.Parcel;
import com.sakop.llk.network.Server;
import com.sakop.llk.view.CardView;
import com.sakop.llk.view.GamePanel;

public class Kyodai extends MyActivity implements OnShakeListener {
	/** Called when the activity is first created. */
	boolean inited = false;
	public static GamePanel panel;
	Handler handler;
	public FrameLayout topLayout;
	// 用于控制游戏的计时器
	public Timer task;
	public ViewFlipper flipper;
	private GestureDetector detector;
	private LinearLayout linearLayout;
	public PopupWindow gameOverWindow;

	@SuppressWarnings("deprecated")
	private void init() {
		ResourcesLoader.init(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		handler = GameMessageHandler.getInstance();
		ShakeListener sl = new ShakeListener(this);
		sl.setOnShakeListener(this);
		detector = new GestureDetector(this, new OnGestureAdapter() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				synchronized (panel.info.smileFaceCount) {
					if (panel.info.smileFaceCount <= 0)
						return false;
					if (e1.getX() - e2.getX() < Util.getXScaledValue(50)) {
						panel.info.smileFaceCount--;
						Log.d("", "kyodai:fling");
						if (SystemState.state.getGameStatus() == SystemState.GAME_PLAYING
								&& SystemState.isNetworkGame) {
							Parcel parcel = new Parcel();
							parcel.type = InformationConstants.SMILE_FACE;
							ObjectOutputStream bos = (ObjectOutputStream) SystemState.state
									.get(SystemState.OUPUTSTREAM);
							MusicManager.smileFaceSound.start();
							try {
								bos.writeObject(parcel);
								bos.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				return false;
			}
		});

		// 初始化线程池
		ExecutorService pool = Executors.newFixedThreadPool(10);
		SystemState.state.set(SystemState.THREAD_POOL, pool);
	}

	public boolean onTouchEvent(MotionEvent event) {
		Log.d("", "kyodai-touch count:" + event.getPointerCount());
		if (event.getPointerCount() == 2) {
			try {
				if (panel.hop != null) {
					panel.hop.cancel();
				}
				if (panel.selected != null) {
					panel.unselect();
				}
				panel.queue.clear();
				Pair<Integer, Integer> p0 = panel.mapCoordinationToIndex(event,
						0);
				Pair<Integer, Integer> p1 = panel.mapCoordinationToIndex(event,
						1);
				CardView[][] cards = panel.info.cards;
				CardView view1 = cards[p0.second][p0.first];
				CardView view2 = cards[p1.second][p1.first];

				if (view1 != null && view2 != null) {
					if (view1.equals(view2))
						return false;
					if (view1.getBackground() == view2.getBackground()) {
						panel.eliminatePairs(view1, view2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return detector.onTouchEvent(event);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		init();

		panel = createMainPanel();
		CardView[][] cards = prepareCard();
		panel.setCardSize(SystemState.state.getPicSize());
		panel.info.cards = cards;
		panel.info.me.headerImage = Util.getHeaderImageIndex(this);
		panel.info.speed = Util.getSpeed(this);

		SystemState.state.set(SystemState.GAME_PANEL, panel);

		topLayout = new FrameLayout(this);
		topLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		topLayout.addView(panel);
		panel.requestFocus();

		// 放置按钮，其他view的容器
		linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		topLayout.addView(linearLayout);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.setOrientation(1);

		addFunctionButtons();

		setContentView(topLayout);
		player = MediaPlayer.create(this, R.raw.bg);
		player.setLooping(true);
		player.start();
		SystemState.state.set(SystemState.CURRENT_MUSIC, player);
	}

	MediaPlayer player;

	private CardView[][] prepareCard() {
		CardView cards[][] = new CardView[GUIConstant.VERTICAL_COUNT + 4][GUIConstant.HORIZONTAL_COUNT + 4];
		int count = 0;
		int currentBackground = -1;
		PictureManager pm = PictureManager.getInstance();

		for (int i = 3; i < 3 + GUIConstant.VERTICAL_COUNT; i++) {
			for (int j = 1; j < 1 + GUIConstant.HORIZONTAL_COUNT; j++) {
				if (count % 2 == 0)
					currentBackground = pm.getRandomCardIndex();
				count++;
				CardView cw = new CardView(currentBackground);
				cw.x = j;
				cw.y = i;
				cards[i][j] = cw;
				panel.info.me.cardCount++;
			}
		}

		Util.makeRandom(cards);
		panel.info.cards = cards;
		return cards;
	}

	@SuppressWarnings("deprecation")
	private GamePanel createMainPanel() {
		PictureManager pm = PictureManager.getInstance();
		Bitmap bigBackground = pm.getCachedBitmap(this,
				R.drawable.bigbackground);
		bigBackground = Bitmap.createBitmap(bigBackground, 0, 0, 640, 480);
		Pair<Integer, Integer> resolution = MapManager.getResolution(this);
		GamePanel panel = new GamePanel(this, bigBackground);
		panel.setBackgroundDrawable(new BitmapDrawable(bigBackground));
		panel.width = resolution.first * 2;
		panel.height = resolution.second;
		panel.setLayoutParams(new ViewGroup.LayoutParams(resolution.first * 2,
				resolution.second));
		SystemState.state.set(SystemState.GAME_HOLDER, panel.getHolder());
		return panel;
	}

	private ImageView singleGame, multiplayer, gameSetting, help = null;

	private void addFunctionButtons() {
		PictureManager pm = PictureManager.getInstance();
		Bitmap mainBg = pm.getCachedBitmap(this, R.drawable.mainbg3);

		singleGame = new ImageView(this);
		singleGame.setLayoutParams(new ViewGroup.LayoutParams(Util
				.getXScaledValue(260), Util.getYScaledValue(100)));

		Bitmap singleGameBm = Bitmap.createBitmap(mainBg, 5, 200, 200, 80);
		singleGame.setImageBitmap(singleGameBm);

		multiplayer = new ImageView(this);
		multiplayer.setLayoutParams(new ViewGroup.LayoutParams(Util
				.getXScaledValue(260), Util.getYScaledValue(100)));

		Bitmap bigBackgroundBm = pm.getCachedBitmap(this,
				R.drawable.bigbackground);
		Bitmap multiplayerBm = Bitmap.createBitmap(bigBackgroundBm, 640, 230,
				200, 80);
		multiplayer.setImageBitmap(multiplayerBm);

		Bitmap gameSettingBm = Bitmap.createBitmap(mainBg, 520, 195, 200, 80);
		gameSetting = new ImageView(this);
		gameSetting.setLayoutParams(new ViewGroup.LayoutParams(Util
				.getXScaledValue(260), Util.getYScaledValue(100)));
		gameSetting.setImageBitmap(gameSettingBm);

		Bitmap helpBm = Bitmap.createBitmap(mainBg, 500, 0, 200, 80);
		help = new ImageView(this);
		help.setLayoutParams(new ViewGroup.LayoutParams(Util
				.getXScaledValue(260), Util.getYScaledValue(100)));
		help.setImageBitmap(helpBm);

		linearLayout.addView(singleGame);
		linearLayout.addView(multiplayer);
		linearLayout.addView(gameSetting);
		linearLayout.addView(help);
		// singleGame.setBackgroundColor(Color.YELLOW);
		// gameSetting.setBackgroundColor(Color.BLUE);
		// help.setBackgroundColor(Color.RED);
		addButtonListeners();
	}

	private void addButtonListeners() {
		singleGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// -1代表不是网络对战
				panel.info.enemy.headerImage = -1;
				SystemState.isNetworkGame = false;
				startGame();
			}
		});
		help.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Kyodai.this, HelpActivity.class);
				startActivity(intent);
			}
		});
		gameSetting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingWindow = SettingWindow.showSettingPopUp(Kyodai.this);
			}
		});
		multiplayer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String ip = Util.getLocalIpAddress();
				if (ip == null || ip.equals("null")) {
					Toast.makeText(Kyodai.this, "您未连上网络,无法开始网络对战", 2000).show();
					return;
				}
				SystemState.isNetworkGame = true;
				new AlertDialog.Builder(Kyodai.this)
						.setTitle("是否作为服务器")
						.setMessage("是否为主机？如果是，请将您的IP地址告知子机，您的IP为" + ip)
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										new Server(Kyodai.this).start();
									}
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										Client.popUpServerAddressInput(Kyodai.this);
									}
								}).show();
			}
		});
	}

	public PopupWindow settingWindow;
	boolean isPopupMenuon = false;

	public void onWindowFocusChanged(boolean focus) {
		if (focus) {
			if (isRestart) {
				isRestart = false;
				final boolean isPause = ((Integer) (SystemState.state
						.get(SystemState.GAME_STATUS)) == SystemState.GAME_PAUSE);
				// 如果上次按了回退键，这次再进来
				final boolean isPlaying = ((Integer) (SystemState.state
						.get(SystemState.GAME_STATUS)) == SystemState.GAME_PLAYING);
				if (isPause || isPlaying) {
					SurfaceHolder holder = panel.getHolder();
					Canvas canvas = holder.lockCanvas();
					canvas.drawBitmap(panel.getBackgroundBitmap(), null,
							new Rect(0, 0, panel.width / 2, panel.height), null);
					holder.unlockCanvasAndPost(canvas);
					holder.lockCanvas(new Rect());
					holder.unlockCanvasAndPost(canvas);
					panel.drawStatus();
					panel.drawSnail(panel.getSnailPosition());
					panel.drawButtons();
					if (!isToolTipShow)
						alertDialog.show();
				}
			}
		}
	}

	public void onRestart() {
		super.onRestart();
		isRestart = true;
	}

	boolean isRestart = false;

	boolean isToolTipShow;

	public void showExitTips(boolean show) {
		if(gameOverWindow != null)
			return;
		if (settingWindow != null) {
			if (settingWindow.isShowing()) {
				Toast.makeText(Kyodai.this, "请先关闭设置窗口", 1500).show();
				return;
			}
		}
		isToolTipShow = show;
		final boolean isPlaying = ((Integer) (SystemState.state
				.get(SystemState.GAME_STATUS)) == SystemState.GAME_PLAYING);
		// 首先先暂停游戏
		if (isPlaying) {
			task.cancel();
			SystemState.state.set(SystemState.GAME_STATUS,
					SystemState.GAME_PAUSE);
		}
		String message = isPlaying ? "游戏已暂停,退出游戏？" : "退出连连看?";
		alertDialog = new AlertDialog.Builder(Kyodai.this)
				.setTitle("是否退出")
				.setMessage(message)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (!isPlaying) {
							if (player != null)
								player.stop();
							// 防止内存泄漏
							if (settingWindow != null)
								settingWindow.dismiss();
							Kyodai.this.finish();
						} else {
							if (SystemState.isNetworkGame) {
								ObjectOutputStream bos = (ObjectOutputStream) SystemState.state
										.get(SystemState.OUPUTSTREAM);
								Parcel p = new Parcel();
								p.type = InformationConstants.OVERTIME;
								try {
									bos.writeObject(p);
									bos.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							endGame();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (isPlaying) {
							// 继续倒计时
							startCountDown();
						}
					}
				}).create(); // 创建对话框
		if (show)
			alertDialog.show(); // 显示对话框
	}

	AlertDialog alertDialog;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.showExitTips(true);
			return false;
		}
		return false;
	}

	public void endGame() {
		if (SystemState.isNetworkGame) {
			CommonThread thread = (CommonThread) SystemState.state
					.get(SystemState.COMMONTHREAD);
			if (thread != null) {
				try {
					if (thread.bin != null)
						thread.bin.close();
					if (thread.bos != null)
						thread.bos.close();
				} catch (Exception e) {
				} finally {
					thread.close();
				}
			}
		}

		MediaPlayer currentMusic = (MediaPlayer) SystemState.state
				.get(SystemState.CURRENT_MUSIC);
		if (!currentMusic.isLooping())
			currentMusic.stop();
		currentMusic.release();
		if (task != null) {
			task.cancel();
		}
		if (panel.hop != null) {
			panel.hop.cancel();
		}
		gameOverWindow = null;
		finish();
		SystemState.state.set(SystemState.GAME_STATUS,
				SystemState.GAME_NOT_STARTED);
		Intent intent = new Intent(Kyodai.this, Kyodai.class);
		startActivity(intent);
	}

	public void startGame() {
		Animation animation = AnimationUtils.loadAnimation(Kyodai.this,
				R.anim.game_start);
		animation.setFillAfter(true);
		panel.startAnimation(animation);
		panel.isTouchable = true;
		panel.info.smileFaceCount = 1;
		linearLayout.removeAllViews();
		gameOverWindow = null;
		startGameMusic();
		Timer tempTimer = new Timer();
		// 500ms之后通知游戏开始
		tempTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 0x102;
				handler.sendMessage(message);
			}
		}, 500);
	}

	private void startGameMusic() {
		if (player != null) {
			player.stop();
			player.release();
		}
		int random = (int) (Math.random() * 2);
		MediaPlayer background = null;
		if (random == 0) {
			background = MediaPlayer.create(this, R.raw.music0);
		} else if (random == 1) {
			background = MediaPlayer.create(this, R.raw.music1);
		}
		background.setLooping(true);
		background.start();
		SystemState.state.set(SystemState.CURRENT_MUSIC, background);
	}

	public void onPause() {
		// pause game
		final boolean isPlaying = ((Integer) (SystemState.state
				.get(SystemState.GAME_STATUS)) == SystemState.GAME_PLAYING);
		// 首先先暂停游戏
		if (isPlaying) {
			showExitTips(false);
		}
		super.onPause();
	}

	public void startCountDown() {
		SystemState.state
				.set(SystemState.GAME_STATUS, SystemState.GAME_PLAYING);

		task = new Timer();
		int speed = Util.getSpeed(Kyodai.this);
		task.schedule(new TimerTask() {
			@Override
			public void run() {
				GameMessageHandler.getInstance().sendEmptyMessage(0x110);
			}
		}, 0, speed);
	}

	public void onShake() {
		final boolean isPlaying = ((Integer) (SystemState.state
				.get(SystemState.GAME_STATUS)) == SystemState.GAME_PLAYING);
		if (isPlaying) {
			panel.rearrange();
		}
	}
}