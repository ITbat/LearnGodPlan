package com.learngodplan.plan;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.db.Task;
import com.learngodplan.db.TaskDBO;
import com.learngodplan.home.MainActivity;

public class NewPlanActivity extends Activity {
	//View成员
	private EditText taskNameEdit;
	private EditText taskTimeEdit;
	private DatePicker startDatePicker;
	private DatePicker endDatePicker;
	private ImageView star1;
	private ImageView star2;
	private ImageView star3;
	private ImageView star4;
	private ImageView star5;
	
	//数据成员
	private int myPriority = 0;
	private TaskDBO taskDBO;
	private String taskName;
	private int taskTime;
	private String taskStartTime;
	private String taskEndTime;
	
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endYear;
	private int endMonth;
	private int endDay;
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.new_plan);  
        
        ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
        acb.setTitle("新建计划");
        
        initViewMember();
        initDataMember();
    }
    
    //初始化所有View
    public void initViewMember(){    	
    	taskNameEdit = (EditText)this.findViewById(R.id.newplan_taskNameEdit);
    	taskTimeEdit = (EditText)this.findViewById(R.id.newplan_taskTimeEdit);
    	star1 = (ImageView)this.findViewById(R.id.priorityStar1);
    	star2 = (ImageView)this.findViewById(R.id.priorityStar2);
    	star3 = (ImageView)this.findViewById(R.id.priorityStar3);
    	star4 = (ImageView)this.findViewById(R.id.priorityStar4);
    	star5 = (ImageView)this.findViewById(R.id.priorityStar5);
    	
    	 // 获取当前的年月日
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH);
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	//默认开始时间和结束时间都是当前日期
		startYear = year;
		startMonth = month+1;
		startDay = day;
		endYear = year;
		endMonth = month+1;
		endDay = day;
		taskStartTime = String.valueOf(year) 
									+ "-" 
									+ String.valueOf(month+1) 
									+ "-" 
									+ String.valueOf(day);
		taskEndTime = String.valueOf(year) 
									+ "-" 
									+ String.valueOf(month+1) 
									+ "-" 
									+ String.valueOf(day);
		Toast.makeText(this, "当前的时间为"+taskStartTime, Toast.LENGTH_SHORT).show();
		
    	startDatePicker = (DatePicker)this.findViewById(R.id.startDatePicker);
    	startDatePicker.init(year, month, day, new OnDateChangedListener(){
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				taskStartTime = String.valueOf(year) 
						                  + "-" 
						                  + String.valueOf(monthOfYear+1) 
						                  + "-" 
						                  + String.valueOf(dayOfMonth);
				startYear = year;
				startMonth = monthOfYear+1;
				startDay = dayOfMonth;
			}
    	});
    	
    	endDatePicker = (DatePicker)this.findViewById(R.id.endDatePicker);
    	endDatePicker.init(year, month, day, new OnDateChangedListener(){
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				taskEndTime = String.valueOf(year) 
		                  + "-" 
		                  + String.valueOf(monthOfYear+1) 
		                  + "-" 
		                  + String.valueOf(dayOfMonth);
				endYear = year;
				endMonth = monthOfYear+1;
				endDay = dayOfMonth;
			}
    	});
    	
    }//end initViewMember
    
    public void initDataMember(){
    	taskDBO = new TaskDBO(this);
    }
    
    
    public void onStarClick(View v){
    	resetAllStar();
        Log.e("d", "call here");
		switch(v.getId()){
			case R.id.priorityStar5: myPriority = 5; break;
			case R.id.priorityStar4: myPriority = 4; break;
			case R.id.priorityStar3: myPriority = 3; break;
			case R.id.priorityStar2: myPriority = 2; break;
			case R.id.priorityStar1: myPriority = 1; break;
        }
		Log.d("d", String.valueOf(myPriority));
		
		switch(v.getId()){
			case R.id.priorityStar5: star5.setImageResource(R.drawable.newplan_star_select);
			case R.id.priorityStar4: star4.setImageResource(R.drawable.newplan_star_select);
			case R.id.priorityStar3: star3.setImageResource(R.drawable.newplan_star_select);
			case R.id.priorityStar2: star2.setImageResource(R.drawable.newplan_star_select);
			case R.id.priorityStar1: star1.setImageResource(R.drawable.newplan_star_select);
	     }
    }
    
    public void resetAllStar(){
    	star5.setImageResource(R.drawable.newplan_star);
    	star4.setImageResource(R.drawable.newplan_star);
    	star3.setImageResource(R.drawable.newplan_star);
    	star2.setImageResource(R.drawable.newplan_star);
    	star1.setImageResource(R.drawable.newplan_star);
    }
    
   
    public void onAddClick(View v){    	
        if(taskTimeEdit.getText().toString().length() == 0 || taskTimeEdit.getText().toString().trim().isEmpty()){
    		Toast.makeText(this, "计划时长不能为空 或 0", Toast.LENGTH_SHORT).show();
    		return;
        }
        
		taskName = taskNameEdit.getText().toString();
        taskTime =Integer.parseInt((taskTimeEdit.getText().toString()));

    	if(myPriority == 0){
    		Toast.makeText(this, "请为计划选择优先级", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(taskName.trim().isEmpty()){
    		Toast.makeText(this, "计划名称不能为空", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(taskTime == 0){
    		Toast.makeText(this, "计划时长不能为空 或 0", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(startYear > endYear){
    		Toast.makeText(this, "起始日期不能晚于终止日期", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
		if(startYear == endYear && startMonth > endMonth){
    		Toast.makeText(this, "起始日期不能晚于终止日期", Toast.LENGTH_SHORT).show();
    		return;
		}
		
		if(startYear == endYear && startMonth == endMonth && startDay > endDay){
    		Toast.makeText(this, "起始日期不能晚于终止日期", Toast.LENGTH_SHORT).show();
    		return;
		}
		getDataAndInsertIntoDB();

    }
    
    //获取task的全部属性，插入数据库
    public void getDataAndInsertIntoDB(){
		Log.d("task Start Time", taskStartTime);
		Log.d("task end  Time", taskEndTime);
    	//debug
		Log.e("debug", taskName);
		
        Task temp  = new Task();
        
        temp.tName = taskName;
        temp.tPriority = myPriority;
        temp.isFinished = 0;
        temp.tPlanTime = taskTime;
        temp.tTotalTime = 0;
        temp.tStartTime = taskStartTime;
        temp.tEndTime = taskEndTime;

        taskDBO.insert(temp);
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        
        //添加成功后跳转到首页
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }
}
