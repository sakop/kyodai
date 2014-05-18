package com.sakop.llk.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Message;

import com.sakop.llk.GameMessageHandler;
import com.sakop.llk.R;
import com.sakop.llk.SystemState;
import com.sakop.llk.activities.Kyodai;
import com.sakop.llk.view.GamePanel;

public abstract class CommonThread {
	public ObjectInputStream bin;
	public ObjectOutputStream bos;

	protected void startListening() {
		try {
			while (true) {
				Parcel parcel = (Parcel) bin.readObject();
				if (parcel.type == InformationConstants.EXIT_GAME) {
					Kyodai kyodai = (Kyodai) SystemState.state
							.get(SystemState.GAME_ACTIVITY);
					kyodai.endGame();
					break;
				} else if (parcel.type == InformationConstants.CARD_COUNT) {
					Message message = new Message();
					message.what = 0x5;
					message.obj = parcel.extraInfo;
					GameMessageHandler.getInstance().sendMessage(message);
				}
				// ¶Ô·½³¬Ê±
				else if (parcel.type == InformationConstants.OVERTIME) {
					Message message = new Message();
					message.what = 0x6;
					GameMessageHandler.getInstance().sendMessage(message);
				} else if (parcel.type == InformationConstants.SMILE_FACE) {
					
					GamePanel panel = (GamePanel) SystemState.state
							.get(SystemState.GAME_PANEL);
					panel.showSmileFace();
				}
			}
		} catch (Exception e) {
		}
	}

	public abstract void close();
}
