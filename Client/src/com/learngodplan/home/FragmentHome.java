package com.learngodplan.home;

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
import com.learngodplan.plan.FragmentPlan;
import com.learngodplan.plan.InstantTaskActivity;
import com.learngodplan.plan.NewPlanActivity;
import com.learngodplan.plan.PlanInfomationActivity;

public class FragmentHome extends Fragment {
	private ImageButton newPlanBt;
	private ExpandableListAdapter adp;
	
	private TaskDBO taskDBO;
	private List<Map<String, String>> groupData;
	private String [] groupFrom = {"taskHead", "taskNum"};
	private int[] groupTo = { R.id.taskHeader, R.id.numOfTask };
	
	private List<List<Map<String, String>>> childData;
    private String[] childFrom = { "taskName" };
    private int[] childTo = { R.id.taskContent };
    
    private ArrayList<Task> sortedTaskArray = new ArrayList<Task>();
    //View成员
    private ExpandableListView expList;
    
    //获取时间区间包含当前日期的未完成任务,根据优先级排序
    public void getAndSortUnfinishedTask(){
        String[] startDateArray;
        String[] endDateArray;
        //获取当前日期
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH) + 1;
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	
    	//获取全部任务
        ArrayList<Task> dbTaskArray  = taskDBO.getAllTask();
        if(dbTaskArray == null){
        	Log.d("dbTaskArray == null", "dbTaskArray == null");
        	dbTaskArray = new ArrayList<Task>();
        }
        Log.d("test", "test1");
        //遍历任务,将当前日期内有效的未完成的任务放入sortedTaskArray中
		for(int i = 0; i < dbTaskArray.size(); i++){
			if(dbTaskArray.get(i).isFinished == 0 || dbTaskArray.get(i).isFinished == 3){
				startDateArray = dbTaskArray.get(i).tStartTime.split("-");
				Log.d("test start year", String.valueOf(startDateArray[0]));
				endDateArray = dbTaskArray.get(i).tEndTime.split("-");
				//如果当前任务的区间包含当前时间就添加到sortedTaskArray
                if(compareDate(startDateArray, endDateArray, year, month, day, dbTaskArray.get(i))){
    				sortedTaskArray.add(dbTaskArray.get(i));
                }
			}
		}
        Log.d("test", "test2'");
		
        //对sortedTaskArray进行快速排序
		_quickSort(sortedTaskArray, 0, sortedTaskArray.size() - 1);
    }
    
    public int getMiddle(ArrayList<Task> sortedTaskArray2, int low, int high) {
    	Task tempTask =  sortedTaskArray2.get(low);
		int tmp = tempTask.tPriority;    //数组的第一个作为中轴
		while (low < high) {
			while (low < high && sortedTaskArray2.get(high).tPriority <=  tmp) {
				high--;
			}
			sortedTaskArray2.set(low, sortedTaskArray2.get(high));
			while (low < high && sortedTaskArray2.get(low).tPriority > tmp) {
				low++;
			}
			sortedTaskArray2.set(high, sortedTaskArray2.get(low));   //比中轴大的记录移到高端
		}
		sortedTaskArray2.set(low, tempTask);//中轴记录到尾
		return low; //返回中轴的位置
	}
    
    public void _quickSort(ArrayList<Task> sortedTaskArray2, int low, int high) {
		if (low < high) {
			int middle = getMiddle(sortedTaskArray2, low, high);  //将list数组进行一分为二
			_quickSort(sortedTaskArray2, low, middle - 1);//对低字表进行递归排序
			_quickSort(sortedTaskArray2, middle + 1, high);//对高字表进行递归排序
		}
	}


	//对比日期的函数
	private boolean compareDate(String[] startDateArray, String[] endDateArray, int year, int month, int day, Task task) {
       if(year < Integer.parseInt(startDateArray[0]) || year > Integer.parseInt(endDateArray[0]) ){
    	   return false;
       }
      if(year == Integer.parseInt(startDateArray[0]) && month < Integer.parseInt(startDateArray[1]) ){
    	  return false;
      }
      if(year == Integer.parseInt(endDateArray[0]) && month > Integer.parseInt(endDateArray[1]) ){
    	  return false;
      }
      if( year == Integer.parseInt(startDateArray[0]) && month == Integer.parseInt(startDateArray[1]) && day < Integer.parseInt(startDateArray[2]) ){
    	  return false;
      }
      if(year == Integer.parseInt(endDateArray[0]) && month == Integer.parseInt(endDateArray[1]) && day > Integer.parseInt(endDateArray[2]) ){
    	  return false;
      }
	   return true;
	}

	public void inistializeAdapterMember(){
		ArrayList<Map<String, String>> groupUnfinish = new ArrayList<Map<String, String>>();
		//初始化childData
		childData = new ArrayList<List<Map<String, String>>>();

		getAndSortUnfinishedTask();

		//遍历sortedTaskArray加入childData
		for(int i = 0; i < sortedTaskArray.size(); i++){
			Map<String, String> child = new HashMap<String, String>();
           
			child.put("taskId", String.valueOf(sortedTaskArray.get(i).tId));
			child.put("taskName", sortedTaskArray.get(i).tName);
			child.put("taskPriority",String.valueOf(sortedTaskArray.get(i).tPriority));
			child.put("taskPlanTime", String.valueOf(sortedTaskArray.get(i).tPlanTime));
			child.put("taskTotalTime", String.valueOf(sortedTaskArray.get(i).tTotalTime));
			child.put("taskStartTime", sortedTaskArray.get(i).tStartTime);
			child.put("taskEndTime", sortedTaskArray.get(i).tEndTime);
			child.put("isFinished", String.valueOf(sortedTaskArray.get(i).isFinished));
			Log.d(sortedTaskArray.get(i).tName, String.valueOf(sortedTaskArray.get(i).isFinished));
		
		    groupUnfinish.add(child);
		}//end for loop
		
		//遍历完成,依次添加到childData
		childData.add(groupUnfinish);
		
		//初始化groupData
		Map<String, String> unfinishMap = new HashMap<String, String>();
		unfinishMap.put("taskHead", "今日任务");
		unfinishMap.put("taskNum", String.valueOf(childData.get(0).size()));
		
		groupData = new ArrayList<Map<String, String>>(); 
		groupData.add(unfinishMap);
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
				        int task_isfinished = Integer.parseInt(childData.get(0).get(childPosition).get("isFinished"));
				        
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
								Log.d("x","xx");
								int childTaskId = Integer.parseInt(childData.get(0).get(childPosition).get("taskId"));
								Log.d("x","xxxxx");
								
								if(Integer.parseInt(childData.get(0).get(childPosition).get("isFinished")) == 3 ){
									//更新Map内的值
									Log.d("x","xxxxxxx");
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
								childData.get(0).get(childPosition).remove("isFinished");
								childData.get(0).get(childPosition).put("isFinished", "3");
								//更新任务的isFinished为3表示该任务已近被设置了闹钟
								taskDBO.updateIsFinished(childTaskId, 3);
							}
						} ).show();
					}
                	
                });
            }
            else{
            	//如果是已完成任务组，就隐藏设置时间按钮和完成按钮
            	startTaskButton.setVisibility(View.GONE);
            	setTime.setVisibility(View.GONE);
            }
			
			return v;
		}
	}
    
	public void initList(View v){
		   expList = (ExpandableListView)v.findViewById(R.id.homeExpandableView);
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
		                        		   for(int i = 0; i < sortedTaskArray.size(); i++){
		                        			   if(sortedTaskArray.get(i).isFinished == 0 || sortedTaskArray.get(i).isFinished == 3){
		                        				  taskDBO.delete(sortedTaskArray.get(i).tId);
		                        			   }
		                        		   }
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
	                               FragmentHome fgHome = new FragmentHome();    
	                               transaction.replace(R.id.content, fgHome);
	                               transaction.show(fgHome); 
	                               transaction.commit();
		                       }
		                   })
		                   .setNegativeButton("取消", null)
		                   .show();
		               }
		               return true;
	           }
			   
		   });
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
	                bd.putString("taskIsFinished", String.valueOf(childData.get(groupPosition).get(childPosition).get("taskIsFinished")));
                    it.putExtra("taskBundle", bd);
                    
                    Log.d("test", "single click in home");
                    startActivity(it);
                    return true;
	            }
	        });
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container,false);
		taskDBO = new TaskDBO(MainActivity.mainAcContext);
		
        ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
        acb.setTitle("首页");
        
		initList(view);
	
	    newPlanBt = (ImageButton)view.findViewById(R.id.home_new_plan);
	    newPlanBt.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
            Intent it = new Intent(MainActivity.mainAcContext, NewPlanActivity.class);
            startActivity(it);
		}
		
	    });
	    return view;
	}
}