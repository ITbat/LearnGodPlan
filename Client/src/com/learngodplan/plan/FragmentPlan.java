package com.learngodplan.plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.alarm.AlarmMethod;
import com.learngodplan.db.Task;
import com.learngodplan.db.TaskDBO;
import com.learngodplan.home.MainActivity;

public class FragmentPlan extends Fragment {
	private ExpandableListAdapter adp;
	
	private TaskDBO taskDBO;
	private List<Map<String, String>> groupData;
	private String [] groupFrom = {"taskHead", "taskNum"};
	private int[] groupTo = { R.id.taskHeader, R.id.numOfTask };
	
	private List<List<Map<String, String>>> childData;
    private String[] childFrom = { "taskName" };
    private int[] childTo = { R.id.taskContent };
    
    //View成员
    private ExpandableListView expList;
	//数据库的全部任务数组
    private ArrayList<Task> dbTaskArray;
    
	public void inistializeAdapterMember(){
        dbTaskArray = new ArrayList<Task>();	
        dbTaskArray  = taskDBO.getAllTask();
        if(dbTaskArray == null){
        	Log.d("dbTaskArray == null", "dbTaskArray == null");
        	dbTaskArray = new ArrayList<Task>();
        }
        
		//初始化childData
		childData = new ArrayList<List<Map<String, String>>>();
		ArrayList<Map<String, String>> groupFinish = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> groupUnfinish = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> groupUnfinishOutDate = new ArrayList<Map<String, String>>();
		
		Log.d("d", String.valueOf(dbTaskArray.size())+"this is data num");
		//遍历从taskDBO数据库得到的数组根据isFinished分组
		for(int i = 0; i < dbTaskArray.size(); i++){
			Map<String, String> child = new HashMap<String, String>();
			child.put("taskId", String.valueOf(dbTaskArray.get(i).tId));
			child.put("taskName", dbTaskArray.get(i).tName);
			child.put("taskPriority",String.valueOf(dbTaskArray.get(i).tPriority));
			child.put("taskPlanTime", String.valueOf(dbTaskArray.get(i).tPlanTime));
			child.put("taskTotalTime", String.valueOf(dbTaskArray.get(i).tTotalTime));
			child.put("taskStartTime", dbTaskArray.get(i).tStartTime);
			child.put("taskEndTime", dbTaskArray.get(i).tEndTime);
			
			if(dbTaskArray.get(i).isFinished == 2){
				//过期计划直接被筛选出来,无需更新标志位
				child.put("taskIsFinished", String.valueOf(dbTaskArray.get(i).isFinished));
			}
			else if(dbTaskArray.get(i).isFinished == 1){
				//已完成也会被直接筛选出来
				child.put("taskIsFinished", String.valueOf(dbTaskArray.get(i).isFinished));
			}
			else{
				//剩下未完成的要再次判断
				if(dbTaskArray.get(i).isFinished == 0 || dbTaskArray.get(i).isFinished == 3){
					//判断读取的计划在当前是否已经过期且未完成，更新数据库内容
					if(isOutDate(dbTaskArray.get(i).tEndTime) ){
						//过期,更新数据库，map中放入新的isFinished标志位2
						Log.d("test update 1", "test update 1");
						//int or long?
						taskDBO.updateIsFinished((int) dbTaskArray.get(i).tId, 2);
						dbTaskArray.get(i).isFinished = 2;
						Log.d("test update 1", "test update 1");
						child.put("taskIsFinished", "2");
					}
					else{
						//没有过期
						child.put("taskIsFinished", String.valueOf(dbTaskArray.get(i).isFinished));
					}
				}
			}
          
			Log.e(dbTaskArray.get(i).tName, "isFinished"+String.valueOf(dbTaskArray.get(i).isFinished));

			if(dbTaskArray.get(i).isFinished == 1){
				groupFinish.add(child);
			}
			else if(dbTaskArray.get(i).isFinished == 0 || dbTaskArray.get(i).isFinished == 3) {
				//isFinished = 0为未结束计划，3为已设置闹钟的未完成计划
				groupUnfinish.add(child);
			}
			else{
				//过期未完成计划 isFinished = 2
				groupUnfinishOutDate.add(child);
			}
		}//end for loop
		
		//遍历完成,依次添加到childData
		childData.add(groupUnfinish);
		childData.add(groupUnfinishOutDate);
		childData.add(groupFinish);
		
		//初始化groupData
		Map<String, String> finishMap = new HashMap<String, String>();
		finishMap.put("taskHead", "已完计划");
		finishMap.put("taskNum", String.valueOf(childData.get(2).size()));
		Map<String, String> unfinishMap = new HashMap<String, String>();
		unfinishMap.put("taskHead", "进行中的计划");
		unfinishMap.put("taskNum", String.valueOf(childData.get(0).size()));
		Map<String, String> outdateMap = new HashMap<String, String>();
		outdateMap.put("taskHead", "过期的未完成计划");
		outdateMap.put("taskNum", String.valueOf(childData.get(1).size()));
		
		groupData = new ArrayList<Map<String, String>>(); 
		groupData.add(unfinishMap);
		groupData.add(outdateMap);
		groupData.add(finishMap);
	}
	
	//日期判断,过期返回真
	private boolean isOutDate(String strEnd) {
        //获取当前日期
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH) + 1;
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	Log.e("day of month", String.valueOf(day));
    	Log.d("strEnd", strEnd);
    	
        String[] endDateArray = strEnd.split("-");
        Log.d("endyear",endDateArray[0]);
        Log.d("endMOnth",endDateArray[1]);
        Log.d("endday",endDateArray[2]);
        
		if(year > Integer.parseInt(endDateArray[0])){
			  return true;
		}
		else if(year == Integer.parseInt(endDateArray[0]) && month > Integer.parseInt(endDateArray[1])){
			return true;
		}
		else if(year == Integer.parseInt(endDateArray[0]) && month ==  Integer.parseInt(endDateArray[1]) && day > Integer.parseInt(endDateArray[2])){
		    return true;	
		}
		return false;
	}
	
	private class MyExpandableListAdapter extends SimpleExpandableListAdapter{

		public MyExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData,
				int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData, int childLayout, int lastChildLayout,
				String[] childFrom, int[] childTo) {
			
			super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout,
					lastChildLayout, childFrom, childTo);
		}
		
		@Override
		public View getChildView(int groupPosition,
	            final int childPosition, boolean isLastChild,
	            View convertView, ViewGroup parent){
			
            View v  = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            ImageButton startTaskButton = (ImageButton) v.findViewById(R.id.itemStartButton);
            final ImageButton setTime = (ImageButton) v.findViewById(R.id.itemSetTimeButton);
            
            if(groupPosition == 0){
                startTaskButton.setVisibility(View.VISIBLE);
                setTime.setVisibility(View.VISIBLE);
                //设置开始任务的按钮事件
                startTaskButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
				        Log.d("taskId", String.valueOf(childData.get(0).get(childPosition).get("taskId")));
				        
				        //需要封装到intent的消息内容
						int task_id = Integer.parseInt(childData.get(0).get(childPosition).get("taskId"));
				        String task_name = childData.get(0).get(childPosition).get("taskName");
				        int task_priority = Integer.parseInt(childData.get(0).get(childPosition).get("taskPriority"));
				        int task_plan_time = Integer.parseInt(childData.get(0).get(childPosition).get("taskPlanTime"));
				        int task_total_time = Integer.parseInt(childData.get(0).get(childPosition).get("taskTotalTime"));
				        String task_start_time = childData.get(0).get(childPosition).get("taskStartTime");
				        String task_end_time = childData.get(0).get(childPosition).get("taskEndTime");
				        int task_isfinished = Integer.parseInt(childData.get(0).get(childPosition).get("taskIsFinished"));
				        
						Intent it = new Intent(MainActivity.mainAcContext, InstantTaskActivity.class);
						//绑定任务的全部属性,传递给InstantTaskActivity
						Bundle taskBundle = new Bundle();
						taskBundle.putInt("taskId", task_id);
						taskBundle.putString("taskName", task_name);
						taskBundle.putInt("taskPriority", task_priority);
						taskBundle.putInt("taskPlanTime", task_plan_time);
						taskBundle.putInt("taskTotalTime", task_total_time);
						taskBundle.putString("taskStartTime", task_start_time);
						taskBundle.putString("taskEndTime", task_end_time);
						taskBundle.putInt("taskIsFinished", task_isfinished);
						
						it.putExtra("taskBundle", taskBundle);
						
						startActivity(it);
					}
                });
                
                setTime.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						View view = View.inflate(MainActivity.mainAcContext, R.layout.set_alarm_layout, null);
						final DatePicker dp = (DatePicker)view.findViewById(R.id.taskDatePicker);
						final TimePicker tp = (TimePicker)view.findViewById(R.id.taskTimePicker);
						
						new AlertDialog.Builder(MainActivity.mainAcContext)
						.setTitle("设置闹钟")
						.setView(view)
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String childTaskName = childData.get(0).get(childPosition).get("taskName");
								int childTaskId = Integer.parseInt(childData.get(0).get(childPosition).get("taskId"));
								
								if(Integer.parseInt(childData.get(0).get(childPosition).get("taskIsFinished")) == 3 ){
									//更新Map内的值
									childData.get(0).get(childPosition).remove("taskIsFinished");
									childData.get(0).get(childPosition).put("taskIsFinished", "0");
									
									taskDBO.updateIsFinished(childTaskId, 0);
									AlarmMethod.cancelAlarm(childTaskId);
									Toast.makeText(
											MainActivity.mainAcContext, 
											"已取消任务<"+childTaskName+">的闹钟", 
											Toast.LENGTH_SHORT
									).show();
								}
								else{
									return;
								}
							}
						})
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String childTaskName = childData.get(0).get(childPosition).get("taskName");
								int childTaskId = Integer.parseInt(childData.get(0).get(childPosition).get("taskId"));
								AlarmMethod.setAlarm( childTaskName, childTaskId, dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
								Toast.makeText(MainActivity.mainAcContext, "任务提醒设置成功", Toast.LENGTH_SHORT).show();
								//更新数组的值
								childData.get(0).get(childPosition).remove("taskIsFinished");
								childData.get(0).get(childPosition).put("taskIsFinished", "3");
								//更新任务的isFinished为3表示该任务已近被设置了闹钟
								taskDBO.updateIsFinished(childTaskId, 3);
							}
						} ).show();
					}
                	
                });
            }
            else{
            	//如果是其他任务组，就隐藏设置时间按钮和完成按钮
            	startTaskButton.setVisibility(View.GONE);
            	setTime.setVisibility(View.GONE);
            }
			
			return v;
		}
	}
	
	//初始化可折叠listview的单击和长按事件
	public void initListClickEven(View view){
		   expList = (ExpandableListView)view.findViewById(R.id.extendableListView);
		   //长按事件
		   expList.setOnItemLongClickListener( new OnItemLongClickListener(){
	           @Override
	           public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		           	ExpandableListView listView = (ExpandableListView) parent;
		           	long pos = listView.getExpandableListPosition(position);
		           	final int groupPos = ExpandableListView.getPackedPositionGroup(pos);
		           	final int childPos = ExpandableListView.getPackedPositionChild(pos);
		           	/* 长按group时 */
		               if (childPos == -1) {
		                   new AlertDialog.Builder(MainActivity.mainAcContext)
		                       .setTitle("提示")
		                       .setIcon(R.drawable.delete_task)
		                       .setMessage("确认清除该目录下的全部任务吗？")
		                       .setPositiveButton("确定",new  android.content.DialogInterface.OnClickListener() {
		                           @Override
		                           public void onClick(DialogInterface dialog, int which) {
		                        	   //String tempId = childData.get(groupPos).remove(childPos).get("task_id");
	                                   //删除全部未完成任务
		                        	   if(groupPos == 0){
		                        		   for(int i = 0; i < dbTaskArray.size(); i++){
		                        			   if(dbTaskArray.get(i).isFinished == 0 || dbTaskArray.get(i).isFinished == 3){
		                        				  taskDBO.delete(dbTaskArray.get(i).tId);
		                        			   }
		                        		   }
		                        	   }//删除全部过期任务
		                        	   else if(groupPos == 1){
		                        		   for(int i = 0; i < dbTaskArray.size(); i++){
		                        			   if(dbTaskArray.get(i).isFinished == 2){
		                        				  taskDBO.delete(dbTaskArray.get(i).tId);
		                        			   }
		                        		   }
		                        		  // ((BaseExpandableListAdapter) adp).notifyDataSetChanged();
		                        	   }
		                        	   else{
		                        		   //删除全部已完成任务
		                        		   for(int i = 0; i < dbTaskArray.size(); i++){
		                        			   if(dbTaskArray.get(i).isFinished == 1){
		                        				  taskDBO.delete(dbTaskArray.get(i).tId);
		                        			   }
		                        		   }
		                        		   //((BaseExpandableListAdapter) adp).notifyDataSetChanged();
		                        	   }
		                        	   
		                        	   //刷新当前的Fragment
		                               FragmentTransaction transaction = MainActivity.fManager.beginTransaction();
		                               FragmentPlan fgPlan = new FragmentPlan();    
		                               transaction.replace(R.id.content, fgPlan);
		                               transaction.show(fgPlan); 
		                               transaction.commit();
		                           }
		                       })
		                       .setNegativeButton("取消", null)
		                       .show();
		               /* 长按child时 */
		               } else {
		                   new AlertDialog.Builder(MainActivity.mainAcContext)
		                   .setTitle("提示")
		                   .setIcon(R.drawable.delete_task)
		                   .setMessage("确认清除选中任务")
		                   .setPositiveButton("确定",new  android.content.DialogInterface.OnClickListener() {
		                       @Override
		                       public void onClick(DialogInterface dialog, int which) {
		                           int task_id = Integer.parseInt(childData.get(groupPos).remove(childPos).get("taskId"));
	                               //删除选中的任务
		                           taskDBO.delete(task_id);
		                           
		                           Log.d("tset", "test001'");
	                        	   //刷新当前的Fragment
	                               FragmentTransaction transaction = MainActivity.fManager.beginTransaction();
	                               FragmentPlan fgPlan = new FragmentPlan();    
	                               transaction.replace(R.id.content, fgPlan);
	                               transaction.show(fgPlan); 
	                               transaction.commit();
		                       }
		                   })
		                   .setNegativeButton("取消", null)
		                   .show();
		               }
		               return true;
	           }
			   
		   });
		   
	        //单击事件,查看任务详情
	        expList.setOnChildClickListener(new OnChildClickListener() {
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                    //封装Task的数据传递给PlanInfomationActivity,跳转到计划信息查看页
	            	Intent it = new Intent(MainActivity.mainAcContext, PlanInfomationActivity.class);
	            	Bundle bd = new Bundle();
	            	
	            	bd.putString("taskName", childData.get(groupPosition).get(childPosition).get("taskName"));
	            	bd.putString("taskPriority", childData.get(groupPosition).get(childPosition).get("taskPriority"));
	            	bd.putString("taskStartTime", childData.get(groupPosition).get(childPosition).get("taskStartTime"));
	            	bd.putString("taskEndTime", childData.get(groupPosition).get(childPosition).get("taskEndTime"));
                    bd.putString("taskPlanTime", childData.get(groupPosition).get(childPosition).get("taskPlanTime"));
	                bd.putString("taskTotalTime", childData.get(groupPosition).get(childPosition).get("taskTotalTime"));
	                bd.putString("taskIsFinished", childData.get(groupPosition).get(childPosition).get("taskIsFinished"));
                    it.putExtra("taskBundle", bd);
                    startActivity(it);
                    return true;
	            }
	        });
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	   View view = inflater.inflate(R.layout.fragment_plan, container,false);
	   
       ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
       acb.setTitle("全部计划");
	   
	   taskDBO = new TaskDBO(MainActivity.mainAcContext);
	   initListClickEven(view);
	   inistializeAdapterMember();
	   
       adp = new MyExpandableListAdapter(
		    	MainActivity.mainAcContext, 
		    	groupData, 
		    	R.layout.task_header_layout,
		    	R.layout.task_header_layout,
		    	groupFrom,
		    	groupTo,
		    	childData,
		    	R.layout.plan_task_item_layout, 
		    	R.layout.plan_task_item_layout,
		    	childFrom,
		    	childTo);
		expList.setAdapter(adp);
		expList.expandGroup(0);
		expList.expandGroup(1);
		
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
}