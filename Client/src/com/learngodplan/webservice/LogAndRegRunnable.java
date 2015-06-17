package com.learngodplan.webservice;

import com.learngodplan.mood.LoginOrRegistActivity;

public class LogAndRegRunnable implements Runnable {
	public static  int LOG_SUCCESS = 11;
	public static int WRONG_NAME = 22;
	public static int WRONG_PASS = 33;
	public static int REG_SUCCESS = 44;
	public static int NOT_CONECT = 55;
	public static int USER_NAME_EXIST = 66;
	
	private String name;
	private String pass;
	private String request;
	public static int actionFlag;// 0 is log, 1 is reg
	
	public LogAndRegRunnable(String n, String p, int f){
		name = n;
		pass = p;
		actionFlag = f;
	}
	
	@Override
	public void run() {	
		try{
            //向服务器发送信息注册或登陆信息 
			switch(actionFlag){
				case 0:{
		            request = "SignIn&"+ name + "&"+ pass;
				}break;
				case 1:{
		            request = "SignUp&"+ name + "&"+ pass;
				}break;
		     }
			
			 ZeroMQSocket zSocket = new ZeroMQSocket(request);
		        
			 String buffer =  zSocket.SendRequest();
           
	        switch(actionFlag){
		        case 0:{
			        //根据服务器信息设置登陆信息回传给登陆界面
			        if(buffer.equals("LIS")){
			            //发送消息 修改UI线程中的组件  
			            LoginOrRegistActivity.mHandler.obtainMessage(LOG_SUCCESS, -1, -1, 1).sendToTarget();
			        }
			        else if(buffer.equals("UDE")){
			        	 LoginOrRegistActivity.mHandler.obtainMessage(WRONG_NAME, -1, -1, 1).sendToTarget();
			        }
			        else if(buffer.equals("WP")){
			        	 LoginOrRegistActivity.mHandler.obtainMessage(WRONG_PASS, -1, -1, 1).sendToTarget();
			        }
		        };break;
		        case 1:{
			        //根据服务器信息设置注册结果
			        if(buffer.equals("SS")){
			            //发送消息 修改UI线程中的组件  
			            LoginOrRegistActivity.mHandler.obtainMessage(REG_SUCCESS, -1, -1, 1).sendToTarget();
			        }
			        else{
			        	//用户名已存在
				        if(buffer.equals("UHE")){
				            LoginOrRegistActivity.mHandler.obtainMessage(USER_NAME_EXIST, -1, -1, 1).sendToTarget();
				        }
			        }
		        };break;
	        }
            
		} catch(Exception e){
			
		}
		 
	}

}
