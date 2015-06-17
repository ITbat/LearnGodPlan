package com.learngodplan.plan;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.db.Food;
import com.learngodplan.db.FoodDBO;
import com.learngodplan.db.Task;
import com.learngodplan.db.TaskDBO;
import com.learngodplan.home.MainActivity;
import com.learngodplan.studymonitor.CompulsoryService;
import com.learngodplan.studymonitor.VibratorTool;

public class InstantTaskActivity extends ActionBarActivity implements OnChronometerTickListener{
	//0为当前显示的是start，1为当前显示的是pause
    private int buttonPressFlag = 0;
    private int planTime;
    private boolean timeSelectFlag = false;
    private Chronometer ch;
	private ImageView bottomBtIv;
	private ImageView halfView;
	private ImageView oneView;
	private ImageView twoView;
	private ImageView threeView;
	private ImageView chestView;
	//判断service是否已经开启的flag，防止重复开启
	public static boolean serviceFlag;
	//监听用户是否有打开其他应用的服务
	
	//计时变量
    private int miss = 0;
	//按下暂停按钮时候的miss值
    private int pauseMiss = 0;
    //当前执行的task
    private Task currentTask;
    private TaskDBO tDBO;
    
    private FoodDBO foodDBO;
    private ArrayList<Food> allFood;
    
    @Override
    protected void onPause(){
    	super.onPause();
    	if(buttonPressFlag == 1){
        	Log.d("test onPause", "test onPause");
        	if(!serviceFlag){
        		Log.d("d", "start service");
            	startService(new Intent(InstantTaskActivity.this, CompulsoryService.class));
            	serviceFlag = true;
        	}
    	}
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if(serviceFlag){
        	Intent stopIntent = new Intent(InstantTaskActivity.this, CompulsoryService.class);
        	stopService(stopIntent);
        	serviceFlag = false;
        	Log.d("d", "onDestroy complete");
    	}
    }
    
	@Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shortplan);
		
		ActionBar acb = this.getActionBar();
		acb.setTitle("即时学习任务");
		
		foodDBO = new FoodDBO(MainActivity.mainAcContext);
		allFood = foodDBO.getAllFood();
		
		//tf = Typeface.createFromAsset(getAssets(), "DigitalClock.ttf");
		ch = (Chronometer)findViewById(R.id.chronometer);
		//ch.setTypeface(tf);
		ch.setOnChronometerTickListener(this);
		ch.setText("00:00:00");
        bottomBtIv = (ImageView)findViewById(R.id.shortPlanBottomButton);
        halfView = (ImageView)findViewById(R.id.halfBt);
        oneView = (ImageView)findViewById(R.id.oneBt);
        twoView = (ImageView)findViewById(R.id.twoBt);
        threeView = (ImageView)findViewById(R.id.threeBt);
        chestView = (ImageView)findViewById(R.id.shortPlanChest);
        
        //获取来自Fragment的Task数据
        getTaskDataFromIntent();
        //初始化数据库接口
 	    tDBO = new TaskDBO(MainActivity.mainAcContext);
	}
	
    public void getTaskDataFromIntent(){
        currentTask = new Task();
        Intent it = this.getIntent();
        Bundle bd = it.getBundleExtra("taskBundle");
        
        currentTask.tId = bd.getInt("taskId");
        currentTask.tName = bd.getString("taskName");
        currentTask.tPriority = bd.getInt("taskPriority");
        currentTask.tPlanTime = bd.getInt("taskPlanTime");
        currentTask.tTotalTime = bd.getInt("taskToalTime");
        currentTask.isFinished = bd.getInt("isFInished");
        currentTask.tStartTime = bd.getString("taskStartTime");
        currentTask.tEndTime = bd.getString("taskEndTime");
    }
    
	public void onBottomButtonClick(View v){
		if(buttonPressFlag == 0){
			if(timeSelectFlag){
					//当前按下的是start，开始计时
					buttonPressFlag = 1;
					bottomBtIv.setImageResource(R.drawable.shortplan_bt_pause);		
					if(pauseMiss == 0){}
					else{
						ch.setText(formatMiss(pauseMiss + 1));
						Log.d("test setText", formatMiss(pauseMiss + 1));
					}
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run() {
							ch.start();
						}
					}, 1000);
			}
			else{
				Toast.makeText(this, "请在屏幕上方选择时长", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			//当前按下的是pause，暂停计时器
			buttonPressFlag = 0;
			pauseMiss = miss;
			Log.d("test", formatMiss(pauseMiss));
			bottomBtIv.setImageResource(R.drawable.shortplan_bt_start);
			String tempText = (String) ch.getText();
			ch.setBase(convertStrTimeToLong(ch.getText().toString()));
			ch.stop();
			ch.setText(tempText);
		}
	}
	
	public void onHalfClick(View v){
		resetButton();
		planTime = 3;
		timeSelectFlag = true;
		halfView.setImageResource(R.drawable.shortplan_press_bt_half);
	}
	
	public void onOneClick(View v){
		resetButton();
		planTime = 3600;
		timeSelectFlag = true;
		oneView.setImageResource(R.drawable.shortplan_press_bt_one);
	}
	
	public void onTwoClick(View v){
		resetButton();
		planTime = 5400;
		timeSelectFlag = true;
		twoView.setImageResource(R.drawable.shortplan_press_bt_two);
	}
	
	public void onThreeClick(View v){
		resetButton();
		planTime = 7200;
		timeSelectFlag = true;
        threeView.setImageResource(R.drawable.shortplan_press_bt_three);
	}
	
	//点击宝箱之后，弹出随机奖励的食物，重置所有按钮，隐藏宝箱
	public void onChestClick(View v){
		LayoutInflater popInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popView = 	popInflater.inflate(R.layout.petfood_popwindow, null);
		ImageView foodView = (ImageView)popView.findViewById(R.id.popFoodView);
		TextView foodText = (TextView)popView.findViewById(R.id.popFoodText);
		
		int foodType = (int)(Math.random()*4);
		//随机获得奖励，并更新数据库
		switch (foodType){
	        case 0:{ 
	        	foodView.setImageResource(R.drawable.food1);
	        	foodText.setText("+20饥饿值");
	        	allFood.get(0).foodNum += 1;
	        	foodDBO.update(allFood.get(0));
	        }break;
		    case 1:{ 
		    	foodView.setImageResource(R.drawable.food2);
		    	foodText.setText("+25 饥饿值");
	        	allFood.get(1).foodNum += 1;
	        	foodDBO.update(allFood.get(1));
		    	}break;
		    case 2: {
		    	foodView.setImageResource(R.drawable.food2);
		    	foodText.setText("+30 饥饿值");
	        	allFood.get(2).foodNum += 1;
	        	foodDBO.update(allFood.get(2));
	        	}break;
		    case 3: {
		    	foodView.setImageResource(R.drawable.food4);
		    	foodText.setText("+35 饥饿值");
	        	allFood.get(3).foodNum += 1;
	        	foodDBO.update(allFood.get(3));
		    	}break;
		    case 4: {
		    	foodView.setImageResource(R.drawable.food5);
		    	foodText.setText("+40 饥饿值");
	        	allFood.get(4).foodNum += 1;
	        	foodDBO.update(allFood.get(4));
		    	}break;
		}
		
		AlertDialog.Builder awardWindow = new AlertDialog.Builder(InstantTaskActivity.this);
		awardWindow.setTitle("获得饲料奖励");
		awardWindow.setView(popView);
		awardWindow.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//确认获得的全部奖励后,返回首页
				chestView.setVisibility(View.GONE);
				bottomBtIv.setVisibility(View.VISIBLE);
				resetButton();
				//test
				planTime = 3600 * 3;
				currentTask.tTotalTime += planTime / 3600;
				//判断是否已近完成了任务,如果完成，则更新Task的isFinished否则的话只更新Task的TotalTime
				if(currentTask.tPlanTime <= currentTask.tTotalTime){
					currentTask.isFinished = 1;
				}
				tDBO.update(currentTask);
				
				Intent it = new Intent(InstantTaskActivity.this, MainActivity.class);
				startActivity(it);
			}
		});
		awardWindow.show();
	}
	
	//清空全部计时数据，重置按钮
	public void resetButton(){
		halfView.setImageResource(R.drawable.shortplan_bt_halfhour);
		oneView.setImageResource(R.drawable.shortplan_bt_onehour);
		twoView.setImageResource(R.drawable.shortplan_bt_twohour);
		threeView.setImageResource(R.drawable.shortplan_bt_threehour);
		bottomBtIv.setImageResource(R.drawable.shortplan_bt_start);
		ch.setText("00:00:00");
		ch.stop();
		miss = 0;
		planTime = 0;
		buttonPressFlag = 0;
		timeSelectFlag = false;
	}
	
	@Override
	public void onChronometerTick(Chronometer ch) {
		Log.d("log miss", String.valueOf(miss));
		
		//如果计时到了，就播放宝箱动画,隐藏底部按钮
		if(miss  >= planTime){
			Toast.makeText(this,"over"+String.valueOf(planTime), Toast.LENGTH_SHORT).show();
			ch.stop();
		    ch.setText(formatMiss(miss));
		    bottomBtIv.setVisibility(View.INVISIBLE);
		    chestView.setBackgroundResource(R.layout.chest_animation);
		    chestView.setVisibility(View.VISIBLE);
			AnimationDrawable anim = (AnimationDrawable)chestView.getBackground();
			anim.start();
			long[] vibrateArray = {500, 200, 500, 200, 500, 200, 500, 200, 500, 200};
			VibratorTool.Vibrate(this, vibrateArray, false);
			return;
		}
		miss++;
	    ch.setText(formatMiss(miss));
	}
	 
	public static String formatMiss(int miss){     
	        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
	        String mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
	        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
	        return hh+":"+mm+":"+ss;      
	 }

	/**
	 * 将String类型的时间转换成long,如：12:01:08
	 * @param strTime String类型的时间
	 * @return long类型的时间
	 * */
	protected long convertStrTimeToLong(String strTime) {
	    String []timeArry=strTime.split(":");
	    long longTime= 0;
	    if (timeArry.length==2) {//如果时间是MM:SS格式
	        longTime=Integer.parseInt(timeArry[0])*1000*60+Integer.parseInt(timeArry[1])*1000;
	    }else if (timeArry.length==3){//如果时间是HH:MM:SS格式
	        longTime=Integer.parseInt(timeArry[0])*1000*60*60+Integer.parseInt(timeArry[1])
	              *1000*60+Integer.parseInt(timeArry[2])*1000 ;
	    }            
	    return SystemClock.elapsedRealtime()-longTime;
	}
	
}
