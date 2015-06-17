package com.learngodplan.mood;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.home.MainActivity;
import com.learngodplan.webservice.SendMoodRunnable;

public class ProposeNewMood extends Activity {
	public static Handler mHandler;
	private EditText moodText;
	private String name;
	private String date;
	private String mood;
	
	public String dbg = "debug";
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {     
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.new_mood_layout);  
        
        ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
        acb.setTitle("新心情");
        
        moodText = (EditText)findViewById(R.id.moodText);
		mHandler = new Handler(){
		    @Override
		    public void handleMessage(Message msg){
		    	if(msg.what == SendMoodRunnable.NOT_CONECT){
		    		Toast.makeText(ProposeNewMood.this, "连接失败请检查网络", Toast.LENGTH_SHORT).show();
		    	    return;
		    	}

		    	if(msg.what == SendMoodRunnable.PROPOSE_SUCCESS){
		    		Toast.makeText(ProposeNewMood.this, "发送成功", Toast.LENGTH_SHORT).show();
		    		moodText.setText("");
		    	    return;
		    	}
		    	
		    	if(msg.what == SendMoodRunnable.PROPOSE_FAIL){
		    		Toast.makeText(ProposeNewMood.this, "发送失败请检查网络", Toast.LENGTH_SHORT).show();
		    	    return;
		    	}
		    }
		};
	}
    
    //初始化要发送的数据
    public void initDataSent(){
    	mood = moodText.getText().toString();
    	//读取保存的用户信息
        SharedPreferences preferences = getSharedPreferences("userInfo",   Activity.MODE_PRIVATE);  
        name = preferences.getString("name", ""); 
        //获取当前日期
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH) + 1;
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	int hour = ca.get(Calendar.HOUR_OF_DAY);
    	int minute = ca.get(Calendar.MINUTE);
        date = year + "-" + month + "-" + day + "-" + hour + ":" + minute;
    }
    
    public void onSendMoodClick(View v){
    	initDataSent();
    	//检查心情是否为空，包含&
    	if(mood.trim().isEmpty() || mood.contains("&")){
    		Toast.makeText(ProposeNewMood.this, "心情内容不能为空或包含'&' ", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	else{
    		//发送心情
    		Thread thread = new Thread(new SendMoodRunnable(name, date, mood));
    		thread.start();
    	}
    }
    
}
