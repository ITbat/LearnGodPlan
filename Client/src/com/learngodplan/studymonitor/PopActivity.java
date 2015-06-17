package com.learngodplan.studymonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.learngodplan.R;
import com.learngodplan.plan.InstantTaskActivity;

public class PopActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_activity_layout);
    }
    
    public void onImWrongClick(View v){
    	finish();
    	//跳转到桌面
    	Intent intent=new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_HOME);
    	startActivity(intent);
    }
    
    public void onIChangeMindClick(View v){
    	AlertDialog al = new AlertDialog.Builder(PopActivity.this)
    	                         .setTitle("警告")
    	                         .setMessage("此时退出将不能得到当前进行的任务的奖励，确定要退出吗？")
    	                         .setPositiveButton("地球人已近阻止不了我玩手机了", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog, int which) {
								    	Intent stopServiceIntent = new Intent(PopActivity.this, CompulsoryService.class);
								    	stopService(stopServiceIntent);
								    	
								    	InstantTaskActivity.serviceFlag = false;
								    	finish();
										
								    	//跳转到桌面
								    	Intent intent=new Intent(Intent.ACTION_MAIN);
								    	intent.addCategory(Intent.CATEGORY_HOME);
								    	startActivity(intent);
									}
    	                        	 
    	                         })
    	                         .setNegativeButton("不不不,我是个爱学习的好孩子", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
								    	finish();
								    	//跳转到桌面
								    	Intent intent=new Intent(Intent.ACTION_MAIN);
								    	intent.addCategory(Intent.CATEGORY_HOME);
								    	startActivity(intent);
									}
								})
    	                         .show();
    }
}
