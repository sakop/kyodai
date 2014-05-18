package com.sakop.llk;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Pair;

public class PictureManager {

	public static final String SNAIL = "snail";
	public static final String STATUS_BACKGROUND = "status background";
	public static final String NOBODY = "nobody";
	public static final String VS = "vs";
	private static final PictureManager instance = new PictureManager();

	private PictureManager() {
	}

	public static PictureManager getInstance() {
		return instance;
	}

	private Map<Integer, Bitmap> pics = new HashMap<Integer, Bitmap>();

	public Bitmap getCachedBitmap(Context context, int key) {
		Bitmap ret = null;
		if ((ret = pics.get(key)) == null) {
			ret = BitmapFactory.decodeResource(context.getResources(), key);
			pics.put(key, ret);
		}
		return ret;
	}

	private static Bitmap[] cards = new Bitmap[16];

	public Bitmap getCards(Context context, int index) {
		if (cards[index] == null) {
			initAllCards(context);
		}
		return cards[index];
	}

	public int getRandomCardIndex() {
		while (true) {
			int random = (int) (Math.random() * cards.length);
			if (random != 2 && random != 14)
				return random;
		}
	}

	private void initAllCards(Context context) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.game_cards);
		for (int i = 0; i < 8; i++) {
			if (i == 0) {
				cards[i] = Bitmap.createBitmap(bitmap, 825, i * 60 + 3, 62, 60);
			}
			if (i == 1 || i == 2)
				cards[i] = Bitmap.createBitmap(bitmap, 825, i * 60 + 5, 62, 60);
			else
				cards[i] = Bitmap.createBitmap(bitmap, 825, i * 60 + 6, 62, 60);
		}

		for (int i = 8; i < 16; i++) {
			if (i == 13) {
				cards[i] = Bitmap.createBitmap(bitmap, 825 + 62,
						(i - 8) * 60 + 25, 60, 60);
			}
			if (i == 14) {
				cards[i] = Bitmap.createBitmap(bitmap, 825 + 62,
						(i - 8) * 60 + 20, 60, 60);
			}
			if (i == 8) {
				cards[i] = Bitmap.createBitmap(bitmap, 825 + 65,
						(i - 8) * 60 + 3, 60, 60);
			}
			if (i == 9) {
				cards[i] = Bitmap.createBitmap(bitmap, 825 + 62,
						(i - 8) * 60 + 3, 60, 60);
			}

			else {
				cards[i] = Bitmap.createBitmap(bitmap, 825 + 62,
						(i - 8) * 60 + 2, 60, 60);
			}
		}
	}

	private Pair<Bitmap, Bitmap> smileFace1, smileFace2;

	public Pair<Bitmap, Bitmap> getSmileFace1(Context context) {
		if (smileFace1 == null) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.status);
			Bitmap firstHalf = Bitmap.createBitmap(bm, 0, 0, 155, 300, null,
					true);
			Matrix matrix = new Matrix();
			Camera camera = new Camera();
			camera.rotateY(180);
			camera.getMatrix(matrix);
			Bitmap secondHalf = Bitmap.createBitmap(bm, 0, 0, 155, 300, matrix,
					true);
			smileFace1 = new Pair<Bitmap, Bitmap>(firstHalf, secondHalf);
			bm.recycle();
		}
		return smileFace1;
	}

	public Bitmap getHeaderImage(Context context, int index) {
		Bitmap bm = getCachedBitmap(context, R.drawable.headers);
		int headerSize = 40;
		return Bitmap.createBitmap(bm, 62 + index * headerSize, 0, headerSize,
				headerSize);
	}

	private static Bitmap backButtonImage, settingImage;

	public Bitmap getBackButtonImage(Context context) {
		if (backButtonImage == null) {
			loadFunctionImages(context);
		}
		return backButtonImage;
	}

	public Bitmap getSettingButtonImage(Context context) {
		if (settingImage == null) {
			loadFunctionImages(context);
		}
		return settingImage;
	}

	private Bitmap seekBarBm = null;

	public Bitmap getSeekBarBm(Context context) {
		if (seekBarBm == null) {
			loadFunctionImages(context);
		}
		return seekBarBm;
	}

	private Bitmap thumbBm = null;

	public Bitmap getThumbBm(Context context) {
		if (thumbBm == null) {
			loadFunctionImages(context);
		}
		return thumbBm;
	}

	// 网络连接时的上，下装饰图和中间的显示面板
	private Bitmap upperDecoration, lowerDecoration, networkBackground,
			cancelBm;

	private Bitmap confirmBm;

	private void loadFunctionImages(Context context) {
		Bitmap bm = getCachedBitmap(context, R.drawable.common_res1);
		backButtonImage = Bitmap.createBitmap(bm, 984, 310, 40, 20);
		settingImage = Bitmap.createBitmap(bm, 995, 350, 29, 30);
		settingBackground = Bitmap.createBitmap(bm, 15, 20, 280, 320);
		seekBarBm = Bitmap.createBitmap(bm, 25, 400, 130, 20);
		thumbBm = Bitmap.createBitmap(bm, 102, 455, 20, 20);
		upperDecoration = Bitmap.createBitmap(bm, 300, 360, 310, 40);
		lowerDecoration = Bitmap.createBitmap(bm, 630, 290, 310, 40);
		networkBackground = Bitmap.createBitmap(bm, 303, 0, 310, 285);
		cancelBm = Bitmap.createBitmap(bm, 920, 135, 104, 35);
		confirmBm = Bitmap.createBitmap(bm, 188, 445, 98, 40);
		bm.recycle();
	}

	public Bitmap getNetworkSettingBackground(Context context) {
		if (networkBackground == null) {
			loadFunctionImages(context);
		}
		return networkBackground;
	}

	public Bitmap getCancelBm(Context context) {
		if (cancelBm == null) {
			loadFunctionImages(context);
		}
		return cancelBm;
	}

	public Bitmap getConfirmBm(Context context) {
		if (confirmBm == null) {
			loadFunctionImages(context);
		}
		return confirmBm;
	}

	public Bitmap getUpperDecoration(Context context) {
		if (upperDecoration == null) {
			loadFunctionImages(context);
		}
		return upperDecoration;
	}

	public Bitmap getLowerDecoration(Context context) {
		if (lowerDecoration == null) {
			loadFunctionImages(context);
		}
		return lowerDecoration;
	}

	private Bitmap settingBackground;

	public Bitmap getSettingBackground(Context context) {
		if (settingBackground == null)
			loadFunctionImages(context);
		return settingBackground;
	}

	private Map<String, Bitmap> map = new HashMap<String, Bitmap>();

	public void addImage(String name, Bitmap bm) {
		map.put(name, bm);
	}

	public Bitmap getImage(String name) {
		return map.get(name);
	}

	private Bitmap bucketWin, bucketLose, win, lose;

	public Bitmap getBucketWinBm(Context context) {
		if (bucketWin == null) {
			loadAllGameWinLosePics(context);
		}
		return bucketWin;
	}

	public Bitmap getBucketLoseBm(Context context) {
		if (bucketLose == null) {
			loadAllGameWinLosePics(context);
		}
		return bucketLose;
	}

	public Bitmap getWinBm(Context context) {
		if (win == null) {
			loadAllGameWinLosePics2(context);
		}
		return win;
	}

	public Bitmap getLoseBm(Context context) {
		if (lose == null) {
			loadAllGameWinLosePics(context);
		}
		return lose;
	}

	private Bitmap clearAllPics, opponentClearAllPics;

	private void loadAllGameWinLosePics(Context context) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.status);
		bucketWin = Bitmap.createBitmap(bm, 150, 320, 225, 135);
		bucketLose = Bitmap.createBitmap(bm, 720, 325, 225, 95);
		lose = Bitmap.createBitmap(bm, 535, 230, 105, 75);
		clearAllPics = Bitmap.createBitmap(bm, 790, 285, 195, 22);
		opponentClearAllPics = Bitmap.createBitmap(bm, 395, 480, 195, 32);
		bm.recycle();
	}

	public Bitmap getClearAllPicsBM(Context context) {
		if (clearAllPics == null) {
			loadAllGameWinLosePics(context);
		}
		return clearAllPics;
	}

	public Bitmap getOpponentClearAllPicsBM(Context context) {
		if (opponentClearAllPics == null) {
			loadAllGameWinLosePics(context);
		}
		return opponentClearAllPics;
	}

	private Bitmap opponentTimeOutBm;

	public Bitmap getOpponentTimeOutBm(Context context) {
		if (opponentTimeOutBm == null) {
			loadAllGameWinLosePics2(context);
		}
		return opponentTimeOutBm;
	}

	public Bitmap getTimeConsumingBm(Context context) {
		if (timeConsuming == null) {
			loadAllGameWinLosePics2(context);
		}
		return timeConsuming;
	}

	private Bitmap timeConsuming, timeOutBm, opponentTimeoutBm;

	private void loadAllGameWinLosePics2(Context context) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.vs);
		win = Bitmap.createBitmap(bm, 610, 240, 100, 60);
		opponentTimeOutBm = Bitmap.createBitmap(bm, 450, 205, 92, 28);
		timeConsuming = Bitmap.createBitmap(bm, 610, 300, 70, 30);
		timeOutBm = Bitmap.createBitmap(bm, 0, 482, 50, 22);
		opponentTimeoutBm = Bitmap.createBitmap(bm, 450, 190, 90, 28);
		bm.recycle();
	}

	public Bitmap getOpponentTimeoutBm(Context context) {
		if (opponentTimeoutBm == null) {
			loadAllGameWinLosePics2(context);
		}
		return opponentTimeoutBm;
	}

	public Bitmap getTimeoutBm(Context context) {
		if (timeOutBm == null) {
			loadAllGameWinLosePics2(context);
		}
		return timeOutBm;
	}

	// public Bitmap getMixedFigureBitmap(Context context, int number) {
	// if (number >= 10) {
	//
	// } else {
	//
	// }
	// return null;
	// }
	//
	// private Bitmap[] getBitmapFigures(Context context, String number) {
	// int firstBit = Integer.parseInt(number.substring(0, 1));
	// int secondBit = Integer.parseInt(number.substring(1));
	// Bitmap[] ret = new Bitmap[2];
	// ret[0] = getFigureBitmap(context, firstBit);
	// ret[1] = getFigureBitmap(context, secondBit);
	// return ret;
	// }
	//
	// public Bitmap getFigureBitmap(Context context, int figure) {
	// // 8 * 22
	// if (figureBitmaps[figure] == null) {
	// loadFigureBitmaps(context);
	// }
	// return figureBitmaps[figure];
	// }
	//
	// private int figureWidth = 80, figureHeight = 22;
	//
	// private void loadFigureBitmaps(Context context) {
	// Bitmap commonRes = getCachedBitmap(context, R.drawable.common_res2);
	// figureBitmaps[1] = Bitmap.createBitmap(commonRes, 400, 140,
	// figureWidth, figureHeight);
	// figureBitmaps[2] = Bitmap.createBitmap(commonRes, 414, 136,
	// figureWidth, figureHeight);
	//
	// }
	//
	// private Bitmap[] figureBitmaps = new Bitmap[10];
}
