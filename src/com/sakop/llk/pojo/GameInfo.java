package com.sakop.llk.pojo;

import java.io.Serializable;

import com.sakop.llk.view.CardView;

public class GameInfo implements Serializable {
	private static final long serialVersionUID = 7818394874824900501L;
	public CardView[][] cards;
	public int speed;
	public PlayerInfo me = new PlayerInfo();
	public PlayerInfo enemy = new PlayerInfo();
	public Integer smileFaceCount = 0;
}

