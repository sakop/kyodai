package com.sakop.llk.algo;


public interface GameStatusConstants {
	//如果30秒内消除不了一组牌，game over
	public final static int time_scale = 30;
	//两次消除中必须满足的最长时间差，不然连击结束
	public final static int MAX_INTERVAL = 3000;
	//外挂的速度
	public final static int AUTO_DELETE_SPEED = 10;
}
