package com.learngodplan.webservice;

import android.os.Handler;

import com.learngodplan.mood.ProposeNewMood;

public class SendMoodRunnable implements Runnable {
	public static int NOT_CONECT = 00;
	public static int PROPOSE_SUCCESS = 11;
	public static int PROPOSE_FAIL = 22;
	
	private String name;
	private String date;
	private String mood;
	private String request;
	
	public static Handler mHandler;
	
	public SendMoodRunnable(String n, String d, String m){
		name = n;
		date = d;
		mood = m;
	}
	
	@Override
	public void run() {	
		try{			
			 request = "Store&"+name+"&" + date + "&" + mood;

			 ZeroMQSocket zSocket = new ZeroMQSocket(request);
	        
			 String buffer =  zSocket.SendRequest();
			
	         //根据服务器发来的信息，根据信息设置handler的信息提示发送结果
	         if(buffer.equals("SS")){
	        	ProposeNewMood.mHandler.obtainMessage(PROPOSE_SUCCESS, -1, -1, 1).sendToTarget();
	         }
	         else{
	        	ProposeNewMood.mHandler.obtainMessage(PROPOSE_FAIL, -1, -1, 1).sendToTarget();
	         }
            
		}catch(Exception e){
        	
		}
		 
	}

}
