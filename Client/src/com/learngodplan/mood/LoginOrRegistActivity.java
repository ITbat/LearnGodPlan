package com.learngodplan.mood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.webservice.LogAndRegRunnable;

public class LoginOrRegistActivity extends Activity {
	private EditText nameText;
	private EditText passText;
	private String name;
	private String pass;
	
	private int actionFlag;
	
	public static Context logRegContext;
	public static Handler mHandler;
	
	 protected void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.login_or_register);  
	        
	        logRegContext = this;
	        //初始化视图成员
	        nameText = (EditText)findViewById(R.id.username);
	        passText = (EditText)findViewById(R.id.password);
	        
			mHandler = new Handler(){
				    @Override
				    public void handleMessage(Message msg){
			    	   if(msg.what == LogAndRegRunnable.NOT_CONECT){
			    		   Toast.makeText(LoginOrRegistActivity.logRegContext, "请检查网路连接", Toast.LENGTH_SHORT).show();
			    		   return;
			    	   }
			    	   
				    	switch(actionFlag){
					    	case 0:{
						    	  if(msg.what == LogAndRegRunnable.LOG_SUCCESS){
						    		  Toast.makeText(LoginOrRegistActivity.logRegContext, "登陆成功", Toast.LENGTH_SHORT).show();
						    		  //登陆成功，保存用户信息, 跳转到心情墙
						    		  SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);  
						    		  Editor editor = sharedPreferences.edit();//获取编辑器  
						    		  editor.putBoolean("hasLog",	true);
						    		  editor.putString("name", name);  
						    		  editor.putString("password", pass);  
						    		  editor.commit();//提交修改  
						    		  
						              Intent intent = new Intent();
						              // Set result and finish this Activity
						              setResult(Activity.RESULT_OK, intent);
						              finish();
						    		  return;
						    	  }
						    	  if(msg.what == LogAndRegRunnable.WRONG_NAME){
						    		  Toast.makeText(LoginOrRegistActivity.logRegContext, "用户名不存在", Toast.LENGTH_SHORT).show();
						    		  return;
						    	  }
						    	  
					    		 if(msg.what == LogAndRegRunnable.WRONG_PASS){
					    			Toast.makeText(LoginOrRegistActivity.logRegContext, "密码错误", Toast.LENGTH_SHORT).show();
					    			return;
					    		 }
					    	};break;
					    	
					    	case 1:{
					    		 if(msg.what == LogAndRegRunnable.REG_SUCCESS){
					    			 Toast.makeText(LoginOrRegistActivity.logRegContext, "注册成功", Toast.LENGTH_SHORT).show();
					    			 //注册成功,保存用户信息,跳转到心情墙
						    		  SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);  
						    		  Editor editor = sharedPreferences.edit();//获取编辑器  
						    		  editor.putBoolean("hasLog",	true);
						    		  editor.putString("name", name);  
						    		  editor.putString("password", pass);  
						    		  editor.commit();//提交修改  
						              Intent intent = new Intent();
						              // Set result and finish this Activity
						              setResult(Activity.RESULT_OK, intent);
						              finish();
					    			 
					    			  return;
					    		 }
					    		 if(msg.what == LogAndRegRunnable.USER_NAME_EXIST){
					    			 Toast.makeText(LoginOrRegistActivity.logRegContext, "用户名已存在", Toast.LENGTH_SHORT).show();
					    			 return;
					    		 }
					    	};break;
				    	}
				    	
				    }
		   };
	 }
	 
	 public void getUserInfo(){
		 name = nameText.getText().toString();
		 pass = passText.getText().toString();
	 }
	 
	 //检查用户信息,不能为空，用户名不能只有空格，“&”,密码不能有"&"
	 public boolean checkInfo(){
		 if(name.trim().isEmpty() || pass.trim().isEmpty()){
			 Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
			 return false;
		 }
		 else{
			 if(name.contains("&") || pass.contains("&")){
				 Toast.makeText(this, "用户名或密码不能包含'&'", Toast.LENGTH_SHORT).show();
				 return false;
			 }
			 else{
				 return true;
			 }
		 }
	 }
	 
	 
	 public void onLogClick(View v){
		 getUserInfo();
		 if(checkInfo()){
			 actionFlag = 0;
			 new Thread(new LogAndRegRunnable(name, pass, 0)).start();
		 }
	 }
	 
	 public void onRegClick(View v){
		 getUserInfo();
		 if(checkInfo()){
			 actionFlag = 1;
			 new Thread(new LogAndRegRunnable(name, pass, 1)).start();
		 }
	 }
}