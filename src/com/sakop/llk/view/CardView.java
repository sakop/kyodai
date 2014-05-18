package com.sakop.llk.view;

import java.io.Serializable;

public class CardView implements Serializable{

	private static final long serialVersionUID = -1015147142159279782L;
	public int x, y;
	private int background;

	public CardView( int background) {
		this.background = background;
	}

	public int getBackground() {
		return background;
	}
	
	public static void exchangePosition(CardView c1,CardView c2){
		int x1 = c1.x;
		int y1 = c1.y;
		c1.x = c2.x;
		c1.y = c2.y;
		c2.x = x1;
		c2.y = y1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardView other = (CardView) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	
}
