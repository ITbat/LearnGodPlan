package com.learngodplan.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import com.learngodplan.home.MainActivity;

public class AlarmMethod {
	public static AlarmManager al;
	
	//设置闹钟
	public static void setAlarm(String name, int id, int year, int month, int day, int hour, int minute){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis()); 
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day); 
        c.set(Calendar.HOUR_OF_DAY, hour); 
        c.set(Calendar.MINUTE, minute); 
        c.set(Calendar.SECOND, 0); 
        c.set(Calendar.MILLISECOND, 0);
        
        //传递任务的id，在任务提醒时用来获得任务名称
        Intent intent = new Intent(MainActivity.mainAcContext, AlarmActivity.class);
        Bundle bd = new Bundle();
        bd.putString("taskName", name);
        intent.putExtra("taskNameBundle", bd);

        PendingIntent pi = PendingIntent.getActivity(MainActivity.mainAcContext, id, intent, 0);
        al = (AlarmManager)MainActivity.mainAcContext.getSystemService(Service.ALARM_SERVICE);
        al.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	}
	
	//取消闹钟
	public static void cancelAlarm(int id){
        Intent intent = new Intent(MainActivity.mainAcContext, AlarmActivity.class);
		PendingIntent pi = PendingIntent.getActivity(MainActivity.mainAcContext, id, intent, 0);
        al = (AlarmManager)MainActivity.mainAcContext.getSystemService(Service.ALARM_SERVICE);
        al.cancel(pi);
	}
}
