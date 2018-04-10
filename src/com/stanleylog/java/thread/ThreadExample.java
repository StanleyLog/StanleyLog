package com.stanleylog.java.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ThreadExample extends Thread {

	private int _id;
	private int _sleepTime;
	private int cnt;
	private String s = "";
	private Map<String, String> map;

	public ThreadExample(int id, int sleepTime, Map<String, String> map) {
		// TODO Auto-generated constructor stub
		_id = id;
		_sleepTime = sleepTime;
		this.map = map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("default", String.valueOf(1));
		
		// TODO Auto-generated method stub
		Thread t1 = new ThreadExample(1, 1000, map);
		Thread t2 = new ThreadExample(2, 2000, map);
		Thread t3 = new ThreadExample(3, 3000, map);

		t1.start();
		t2.start();
		t3.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
//				System.out.println(Thread.currentThread().getName() + ":" +  ++cnt);
//				s = s + cnt;
//				System.out.println(Thread.currentThread().getName() + ":" +  s);
//				Thread.sleep(_sleepTime);
				map.put(Thread.currentThread().getName(), String.valueOf(Thread.currentThread().getId()));
//				if(map.entrySet().size() == 2)
				System.out.println(Thread.currentThread().getName() +": "+ map.entrySet().size());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
