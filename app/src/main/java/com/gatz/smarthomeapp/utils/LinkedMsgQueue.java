package com.gatz.smarthomeapp.utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LinkedMsgQueue<E> {

	private ConcurrentLinkedQueue<E> list = new ConcurrentLinkedQueue<E>();
	private final Object lock = new Object();

	public void put(E e) {
		list.add(e);
		 synchronized(lock){
             lock.notifyAll();
         }
	}

	public E poll() throws InterruptedException {
		//E e = list.poll();
		while (list.size() == 0)
			synchronized (lock) {
				lock.wait();
			}
		return list.poll();
	}
	
	public E peek() throws InterruptedException {
		return list.peek();
	}

	public void removeAll(){
		list = new ConcurrentLinkedQueue<E>();
	}
	public Object getLock() {
		return lock;
	}
}
