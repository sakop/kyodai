package com.sakop.llk.algo;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sakop.llk.view.CardView;

public class TwoElementsQueue {
	Queue<CardView> queue = new ConcurrentLinkedQueue<CardView>();
	private CardView second;

	public void enque(CardView c) {
		if (queue.size() == 2) {
			queue.poll();
		}
		queue.add(c);
		if (queue.size() == 2) {
			second = c;
		}
	}

	public void clear() {
		queue.clear();
	}

	public int size() {
		return queue.size();
	}

	public CardView getFirst() {
		return queue.peek();
	}

	public CardView getSecond() {
		if (size() != 2)
			throw new IllegalStateException("The 2nd element does not exist");
		return second;
	}
}
