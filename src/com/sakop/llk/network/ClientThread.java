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
import com.sakop.llk.SystemState;
import com.sakop.llk.Util;
import com.sakop.llk.pojo.GameInfo;
import com.sakop.llk.view.GamePanel;

public class ClientThread extends CommonThread implements Runnable {
	private Client client;

	public ClientThread(Client client) {
		this.client = client;
	}

	public void close() {
		if (client.socket != null) {
			try {
				client.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			client.socket = new Socket(client.serverAddr, Server.SERVER_PORT);
			SystemState.state.set(SystemState.COMMONTHREAD, this);
			bin = new ObjectInputStream(new BufferedInputStream(
					client.socket.getInputStream()));
			// 读
			final GameInfo info = (GameInfo) bin.readObject();
			GamePanel panel = (GamePanel) SystemState.state
					.get(SystemState.GAME_PANEL);
			panel.info.cards = info.cards;
			panel.info.speed = info.speed;
			Util.setSpeed(client.context, info.speed);
			panel.info.enemy.cardCount = info.enemy.cardCount;
			panel.info.enemy.headerImage = info.enemy.headerImage;
			panel.info.enemy.nickname = info.enemy.nickname;
			panel.info.me.cardCount = info.enemy.cardCount;

			bos = new ObjectOutputStream(new BufferedOutputStream(
					client.socket.getOutputStream()));
			// 自己的头像和昵称
			GameInfo writeToEnemy = new GameInfo();
			writeToEnemy.enemy.nickname = Util.getNickName(client.context);
			writeToEnemy.enemy.headerImage = Util
					.getHeaderImageIndex(client.context);
			bos.writeObject(writeToEnemy);
			bos.flush();

			SystemState.state.set(SystemState.OUPUTSTREAM, bos);

			if (client.timer != null) {
				client.timer.cancel();
			}
			client.hintText = "玩家" + info.enemy.nickname + "加入游戏";
			client.hintText2 = "游戏速度为:" + info.speed + "ms一个小蜗牛";
			client.hintText3 = "游戏马上开始";
			client.image.postInvalidate();
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
					if (client.window != null)
						client.window.dismiss();
					Message message = new Message();
					message.what = 0x11111;
					GameMessageHandler.getInstance().sendMessage(message);
				}
			});

			startListening();

		} catch (Exception e) {
			close();
			e.printStackTrace();
			if (client.window != null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				client.window.dismiss();
			}
			if (client.timer != null) {
				client.timer.cancel();
			}
			Message message = new Message();
			message.what = 0x12345;
			message.obj = "操作失败,原因为:" + e.getMessage();
			GameMessageHandler.getInstance().sendMessage(message);
		}
	}

}
