package com.macyoo.main;

import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.javagamesfactory.nioservers.StringBasedClient;
import org.javagamesfactory.nioservers.iMessageProcessor;
import org.json.JSONObject;

import com.macyoo.util.Count;


public class Main  implements iMessageProcessor {
	
	String message[] = {"Hello!", "Pagi!", "How do u do?" } ;
	Logger logger;
	StringBasedClient stringClient;
	Random rand;
	JSONObject json;
	// 가나다라...zaq321!@# zaq321!@#

	
	Thread	thread;	
	
	static int 	maxCount=20;
	static int 	maxThr=2;
	
	public Main() {
						
		logger = Logger.getLogger( getClass() ) ;
		stringClient = new StringBasedClient();
		stringClient.setMessageProcessor(this);
		rand = new Random();
		
		synchronized (logger) {
			//logger.error("Started....");	
		}
		
	}
	void connecting() {
		
		stringClient.setServerHostname("127.0.0.1");
		stringClient.setServerPort(9090);
		
		try {
			stringClient.connect();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if ( stringClient.isConnected() ) {
			//System.out.println("connected...");
		}
		else {
			System.out.println("not connected...");
		}
	}
	
	void setThread(Thread thread) {
		this.thread = thread;
	}

	void connectSendMsg() {

		// connect
		connecting();
		
		// send
		for( int i = 0 ; i < maxCount; i++ ) {
			try {
				rand.nextInt(2);
				
				json = new JSONObject()
					.put("cmd", "chat").put("msg", message[rand.nextInt(3)] );
				
				if ( stringClient.isConnected() ) {
					stringClient.sendMessage( json.toString()  );
					
					synchronized (logger) { 
						Count.sendCount ++;
						//if( Count.sendCount == (maxCount-1) || Count.sendCount == (maxCount/2) )
							System.out.println( "sendMessage cnt("+Count.sendCount+")["+json.toString() );
					}

				}
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// sleep()
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if ( stringClient.isConnected() ) {
			stringClient.close();	
		}
		
	}

	public void receiveMessage(String message) {
		synchronized (logger) {
			Count.receiveCount++;
			
			if( Count.receiveCount == (maxThr-1) || Count.receiveCount == (maxThr/2) )
				logger.error("the["+ thread.getName() 
						+"]receiveMessage cnt["+ Count.receiveCount +"] : " + message );
		}

	}

	public static void main(String[] args)  {
		
		if ( args.length > 1 ) {
			maxThr = Integer.parseInt( args[0] );
			maxCount = Integer.parseInt( args[1] );
		}
		
		for( int i=0; i < maxThr ; i++ ) {
			
			new Thread( "A"+ i ){
				public void run() {
					Main m = new Main();
					m.setThread(this);
					m.connectSendMsg();
				}
			}.start();
		}
	}
}
