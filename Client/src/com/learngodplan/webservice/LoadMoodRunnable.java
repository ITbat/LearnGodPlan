package com.learngodplan.webservice;

import android.os.Message;
import android.util.Log;

import com.learngodplan.mood.FragmentMood;

public class LoadMoodRunnable implements Runnable {
	public static int NOT_CONECT = 00;
	public static int LOAD_SUCCESS = 11;
	public static int LOAD_FAIL = 22;
	
	private int dataNum = 10;
	private String moodStr;
	private String request;
	
	public LoadMoodRunnable(){}
	
	@Override
	public void run() {	
		try{
			 request = "Load&"+ String.valueOf(dataNum);
			ZeroMQSocket zSocket = new ZeroMQSocket(request);
	        
			String buffer =  zSocket.SendRequest();
			
        	moodStr = buffer;
        	Log.d("load mood response", buffer);
	        checkMessageFromServer(buffer);
            
		 } catch(Exception e){
        	 Log.d("error","未知的错误");
         }
	}
	
	//检查从服务器获取的数据,决定返回给handler的内容
	public void checkMessageFromServer(String bf){
		//加载成功
		if(!bf.equals("LF")){
			Message.obtain(FragmentMood.mHandler, LOAD_SUCCESS, -1, -1, moodStr).sendToTarget();
		}
		else{
			Log.d("else", bf);
			Message.obtain(FragmentMood.mHandler, LOAD_FAIL, -1, -1, null).sendToTarget();
		}
	}

}
