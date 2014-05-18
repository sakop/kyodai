package com.sakop.llk.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import android.os.Message;

import com.sakop.llk.GameMessageHandler;
import com.sakop.llk.R;
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.pojo.GameInfo;
import com.sakop.llk.view.GamePanel;

public class ServerThread extends CommonThread implements Runnable {
	Server server;
	Socket client;

	public ServerThread(Server server) {
		this.server = server;
	}

	public void close() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (server.ss != null) {
					try {
						server.ss.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void run() {
		try {
			SystemState.isServer = true;
			client = server.ss.accept();
			SystemState.state.set(SystemState.COMMONTHREAD, this);
			bos = new ObjectOutputStream(new BufferedOutputStream(
					client.getOutputStream()));
			// ==================开始写
			GamePanel panel = (GamePanel) SystemState.state
					.get(SystemState.GAME_PANEL);
			final GameInfo info = panel.info;

			GameInfo enemyInfo = new GameInfo();
			enemyInfo.enemy.cardCount = info.me.cardCount;
			info.enemy.cardCount = info.me.cardCount;
			enemyInfo.cards = info.cards;
			info.speed = Util.getSpeed(server.context);
			enemyInfo.speed = info.speed;
			Util.setSpeed(server.context, enemyInfo.speed);
			enemyInfo.enemy.headerImage = Util
					.getHeaderImageIndex(server.context);
			enemyInfo.enemy.nickname = Util.getNickName(server.context);

			// 有多少张牌
			bos.writeObject(enemyInfo);
			bos.flush();

			// panel.enemyRemainigCount = ((Kyodai) context).cardCount;

			// =============读
			bin = new ObjectInputStream(new BufferedInputStream(
					client.getInputStream()));
			GameInfo readEnemyInfo = (GameInfo) bin.readObject();
			info.enemy.headerImage = readEnemyInfo.enemy.headerImage;
			info.enemy.nickname = readEnemyInfo.enemy.nickname;

			SystemState.state.set(SystemState.OUPUTSTREAM, bos);

			if (server.timer != null) {
				server.timer.cancel();
			}
			server.hintText = "玩家" + info.enemy.nickname + "加入游戏";
			server.hintText2 = "游戏速度为:" + info.speed + "ms一个小蜗牛";
			server.hintText3 = "游戏马上开始";
			server.image.postInvalidate();
			// after 2seconds,game starts
			ThreadPoolExecutor exec = (ThreadPoolExecutor) SystemState.state
					.get(SystemState.THREAD_POOL);
			exec.execute(new Runnable() {

				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (server.window != null)
						server.window.dismiss();
					Message message = new Message();
					message.what = 0x11111;
					GameMessageHandler.getInstance().sendMessage(message);
				}
			});
			startListening();

		} catch (Exception e) {
			e.printStackTrace();
			close();
			if (server.window != null) {
				server.window.dismiss();
			}
			if (server.timer != null) {
				server.timer.cancel();
			}

			Message message = new Message();
			message.what = 0x12345;
			message.obj = "网络失败,原因为:" + e.getMessage();
			if (e.getMessage().contains("Try again"))
				message.obj = "等待对手超时";
			GameMessageHandler.getInstance().sendMessage(message);
			if (server.ss != null) {
				try {
					server.ss.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
