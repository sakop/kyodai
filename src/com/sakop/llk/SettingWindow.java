package com.sakop.llk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.algo.GUIConstant;

public class SettingWindow {

	@SuppressWarnings("deprecation")
	public static PopupWindow showSettingPopUp(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		layout.setBackgroundDrawable(new BitmapDrawable(PictureManager
				.getInstance().getSettingBackground(context)));
		final PopupWindow popUp = new PopupWindow(layout,
				Util.getXScaledValue(420), Util.getYScaledValue(600));

		layout.addView(getSettingContents(popUp, context));
		popUp.showAtLocation(layout, Gravity.CENTER, Util.getXScaledValue(0),
				Util.getYScaledValue(10));
		return popUp;
	}

	private static int yDisplacement = 40;
	private static View sView;

	// 获得popUp窗口中的所有内容
	public static View getSettingContents(final PopupWindow popUp,
			final Context context) {
		// 读系统信息
		sView = new View(context) {
			public void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				AudioManager manager = (AudioManager) context
						.getSystemService(Activity.AUDIO_SERVICE);
				int currentVolume = manager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				int max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				// 获取声音
				final int volumeX = (int) (currentVolume * 1.0f / max
						* Util.getXScaledValue(220) + Util.getXScaledValue(120));

				int currentSpeed = Util.getSpeed(context);
				final int speedX = (int) ((currentSpeed - 250) * 1.0f / 750
						* Util.getXScaledValue(220) + Util.getXScaledValue(120));
				Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
				Paint redP = new Paint(Paint.ANTI_ALIAS_FLAG);
				Paint optionP = new Paint(Paint.ANTI_ALIAS_FLAG);
				redP.setColor(Color.RED);

				optionP.setTextSize(Util.getYScaledValue(36));
				p.setTextSize(Util.getYScaledValue(32));
				redP.setTextSize(Util.getYScaledValue(32));
				canvas.drawText("选项", Util.getXScaledValue(170),
						Util.getYScaledValue(60), optionP);
				canvas.drawText("关闭", Util.getXScaledValue(185),
						Util.getYScaledValue(570), p);
				canvas.drawText("声音", Util.getXScaledValue(40),
						Util.getYScaledValue(100 + yDisplacement), p);
				canvas.drawText("速度", Util.getXScaledValue(40),
						Util.getYScaledValue(180 + yDisplacement), p);
				canvas.drawText("昵称", Util.getXScaledValue(40),
						Util.getYScaledValue(260 + yDisplacement), p);

				String nickname = Util.getNickName(context);
				canvas.drawText(nickname, Util.getXScaledValue(120),
						Util.getYScaledValue(260 + yDisplacement), p);
				canvas.drawText("更改", Util.getXScaledValue(300),
						Util.getYScaledValue(260 + yDisplacement), redP);

				canvas.drawText("头像", Util.getXScaledValue(40),
						Util.getYScaledValue(340 + yDisplacement), p);

				int index = Util.getHeaderImageIndex(context);
				Bitmap headerImage = PictureManager.getInstance()
						.getHeaderImage(context, index);
				canvas.drawBitmap(
						headerImage,
						null,
						new Rect(Util.getXScaledValue(125), Util
								.getYScaledValue(310 + yDisplacement), Util
								.getXScaledValue(125)
								+ Util.getYScaledValue(60), Util
								.getYScaledValue(310 + yDisplacement)
								+ Util.getYScaledValue(60)), null);

				PictureManager pm = PictureManager.getInstance();
				// 声音的线
				canvas.drawBitmap(
						pm.getSeekBarBm(context),
						null,
						new Rect(Util.getXScaledValue(120), Util
								.getYScaledValue(70 + yDisplacement), Util
								.getXScaledValue(370), Util
								.getYScaledValue(90 + yDisplacement)), null);
				canvas.drawBitmap(
						pm.getThumbBm(context),
						null,
						new Rect(volumeX, Util
								.getYScaledValue(75 + yDisplacement), volumeX
								+ Util.getXScaledValue(30), Util
								.getYScaledValue(95 + yDisplacement)), null);

				// 速度的线
				canvas.drawBitmap(
						pm.getSeekBarBm(context),
						null,
						new Rect(Util.getXScaledValue(120), Util
								.getYScaledValue(150 + yDisplacement), Util
								.getXScaledValue(370), Util
								.getYScaledValue(170 + yDisplacement)), null);
				canvas.drawBitmap(
						pm.getThumbBm(context),
						null,
						new Rect(speedX, Util
								.getYScaledValue(155 + yDisplacement), speedX
								+ Util.getXScaledValue(30), Util
								.getYScaledValue(175 + yDisplacement)), null);
			}
		};
		sView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(final View v, MotionEvent event) {
				Log.d("", "kyodai:" + event.getX() + "," + event.getY());
				// 关闭按钮
				int x = (int) event.getX();
				int y = (int) event.getY();
				if (event.getX() >= Util.getXScaledValue(205)
						&& event.getX() <= Util.getXScaledValue(260)) {
					if (event.getY() >= Util.getYScaledValue(550)
							&& event.getY() <= Util.getYScaledValue(585)) {
						((LinearLayout) popUp.getContentView())
								.removeAllViews();
						if (SystemState.state.getGameStatus() == SystemState.GAME_PAUSE) {
							((Kyodai) context).startCountDown();
						}
						popUp.dismiss();
					}
				}

				// 调节声音
				if (y >= Util.getYScaledValue(80 + yDisplacement)
						&& y <= Util.getYScaledValue(110 + yDisplacement)) {
					if (x >= Util.getXScaledValue(110)
							&& x <= Util.getXScaledValue(370)) {
						int volumeX = x;
						if (volumeX > Util.getXScaledValue(340)) {
							volumeX = Util.getXScaledValue(340);
						}
						if (volumeX < Util.getXScaledValue(110)) {
							volumeX = Util.getXScaledValue(120);
						}
						adjustVolume(volumeX);
						v.invalidate();
					}
				}

				// 调节速度
				if (y >= Util.getYScaledValue(140 + yDisplacement)
						&& y <= Util.getYScaledValue(180 + yDisplacement)) {
					if (x >= Util.getXScaledValue(110)
							&& x <= Util.getXScaledValue(370)) {
						int speedX = x;
						if (speedX > Util.getXScaledValue(340)) {
							speedX = Util.getXScaledValue(340);
						}
						if (speedX < Util.getXScaledValue(120)) {
							speedX = Util.getXScaledValue(120);
						}
						adjustSpeed(speedX);
						v.invalidate();
					}
				}

				// 更改玩家名字
				if (y >= Util.getYScaledValue(240 + yDisplacement)
						&& y <= Util.getYScaledValue(270 + yDisplacement)) {
					if (x >= Util.getXScaledValue(300)
							&& x <= Util.getXScaledValue(350)) {
						LinearLayout layout = new LinearLayout(context);
						layout.setPadding(Util.getXScaledValue(10), 0, 0, 0);
						layout.setLayoutParams(new ViewGroup.LayoutParams(-2,
								-2));
						layout.setOrientation(0);
						TextView text = new TextView(context);
						text.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
						text.setText("更换昵称");
						final EditText editor = new EditText(context);
						editor.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								10) });
						editor.setSingleLine(true);
						editor.setLayoutParams(new ViewGroup.LayoutParams(-2,
								-2));
						final SharedPreferences sp = context
								.getSharedPreferences("kyodai",
										Kyodai.MODE_PRIVATE);
						String nickname = sp.getString("nickname", "玩家1");
						editor.setText(nickname);
						Button ok = new Button(context);
						ok.setText("确定更改");
						ok.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
						layout.addView(text);
						layout.addView(editor);
						layout.addView(ok);
						final AlertDialog dialog = new AlertDialog.Builder(
								context).setView(layout).setTitle("更换昵称")
								.show();
						ok.setOnClickListener(new OnClickListener() {
							public void onClick(View view) {
								String text = editor.getText().toString();
								if (text.trim().length() == 0) {
									Toast.makeText(context, "请输入合法的昵称", 200)
											.show();
									return;
								} else {
									Util.setNickName(context, text.trim());
									dialog.dismiss();
									v.invalidate();
								}
							}
						});
					}
				}

				if (y >= Util.getYScaledValue(310 + yDisplacement)
						&& y <= Util.getYScaledValue(310 + yDisplacement)
								+ Util.getYScaledValue(60)) {
					if (x >= Util.getXScaledValue(130)
							&& x <= Util.getXScaledValue(130)
									+ +Util.getYScaledValue(60)) {
						if (adapter == null) {
							adapter = new BaseAdapter() {

								public View getView(int position,
										View convertView, ViewGroup parent) {
									ImageView view = new ImageView(context);
									view.setLayoutParams(new ListView.LayoutParams(
											Util.getYScaledValue(60), Util
													.getYScaledValue(60)));
									Bitmap bm = PictureManager.getInstance()
											.getHeaderImage(context, position);
									view.setImageBitmap(bm);
									return view;
								}

								public long getItemId(int position) {
									return position;
								}

								public Object getItem(int position) {
									return position;
								}

								public int getCount() {
									return GUIConstant.HEADER_IMAGE_COUNT;
								}
							};
						}

						GridView gv = new GridView(context);
						gv.setNumColumns(4);
						gv.setVerticalSpacing(10);
						gv.setHorizontalSpacing(5);
						gv.setAdapter(adapter);
						gv.setPadding(Util.getXScaledValue(60),
								Util.getYScaledValue(5), 0,
								Util.getYScaledValue(5));
						final AlertDialog dialog = new AlertDialog.Builder(
								context).setView(gv).show();
						gv.setOnItemClickListener(new OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								if (SystemState.state.getGameStatus() != SystemState.GAME_NOT_STARTED) {
									Toast.makeText(context, "游戏中头像不能改变", 200)
											.show();
									return;
								}
								Util.setHeaderImage(context, position + "");
								dialog.dismiss();
								sView.invalidate();
							}
						});

					}
				}
				return false;
			}

			private BaseAdapter adapter = null;

			private void adjustSpeed(int volumeX) {
				float speedRatio = (volumeX - Util.getXScaledValue(120)) * 1.0f
						/ Util.getXScaledValue(220);
				int speed = (int) (250 + 750 * speedRatio);
				Util.setSpeed(context, speed);
				String text = "游戏速度已经更改至" + speed + "毫秒一个小蜗牛";
				Toast.makeText(context, text, 10).show();
			}

			// 重置滑块位置，调整音量
			private void adjustVolume(int volumeX) {
				AudioManager manager = (AudioManager) context
						.getSystemService(Activity.AUDIO_SERVICE);
				float ratio = (volumeX - Util.getXScaledValue(120)) * 1.0f
						/ Util.getXScaledValue(220);
				int max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int currentVolume = Math.round(max * ratio);
				manager.setStreamVolume(AudioManager.STREAM_MUSIC,
						currentVolume, AudioManager.FLAG_SHOW_UI);
			}
		});
		sView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		return sView;
	}
}
