/**
 * 
 */
package com.stanleylog.java.util.concurrent;

import java.util.concurrent.CountDownLatch;


/**
 * @author Stanley
 *
 */
public class CountDownLatchExample {
	
	private final CountDownLatch keepAliveLatch = new CountDownLatch(1);
	
	private final Thread keepAliveThread;
	/**
	 * 
	 */
	public CountDownLatchExample() {
		// TODO Auto-generated constructor stub
		
		keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                	System.out.println("start");
                    keepAliveLatch.await();
                } catch (InterruptedException e) {
                    // bail out
                }
            }
        }, "elasticsearch[keepAlive/" + 1 + "]");
		
        keepAliveThread.setDaemon(false);
        
        // keep this thread alive (non daemon thread) until we shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	System.out.println("shudown");
                keepAliveLatch.countDown();
            }
        });
		
	}
	
	
	public static void main(String[] args) {
		
		CountDownLatchExample test = new CountDownLatchExample();
		
		
	}
	
}
