package com.learngodplan.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.learngodplan.R;
import com.learngodplan.home.MainActivity;

public class PlanInfomationActivity extends Activity {
	private SimpleAdapter spAdp;
	private StringTask strTask;
	private ListView lv;	
    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
    
    public void getTaskDataFromIntent(){
    	Log.d("test", "getTaskDataFromInent begin");
        strTask= new StringTask();
        
        Intent it = this.getIntent();
        Bundle bd = it.getBundleExtra("taskBundle");
        
        strTask.tName = bd.getString("taskName");
        strTask.tPriority = bd.getString("taskPriority");
        strTask.tPlanTime = bd.getString("taskPlanTime");
        strTask.tTotalTime = bd.getString("taskTotalTime");
        strTask.isFinished = bd.getString("taskIsFinished");
        strTask.tStartTime = bd.getString("taskStartTime");
        strTask.tEndTime = bd.getString("taskEndTime");
    	Log.d("test", "getTaskDataFromInent end");
    }
    
    //初始化spAdp
    public void initSimpleAdapter(){
    	 Map<String, Object> tempMap = new HashMap<String, Object>();
    	 tempMap.put("leftText", "名称");
    	 tempMap.put("rightText", strTask.tName);
         items.add(tempMap);

         
         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "开始时间");
         tempMap.put("rightText",strTask.tStartTime);
         items.add(tempMap);
         
         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "结束时间");
         tempMap.put("rightText", strTask.tEndTime);
         items.add(tempMap);
        		 
         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "优先级");
         tempMap.put("rightText", strTask.tPriority);
         items.add(tempMap);
        		 
         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "计划用时");
         tempMap.put("rightText", strTask.tPlanTime);
         items.add(tempMap);
        		 
         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "累计用时");
         tempMap.put("rightText", strTask.tTotalTime);
         items.add(tempMap);
         

         tempMap = new HashMap<String, Object>();
         tempMap.put("leftText", "任务状态");
         if(strTask.isFinished == null){
        	 Log.d("null", "isFinished is null");
         }
         switch(strTask.isFinished){
             case"0":tempMap.put("rightText", "进行中");break;
	         case"1":tempMap.put("rightText", "已完成");break;
	         case"2":tempMap.put("rightText", "已过期");break;
	         case"3":tempMap.put("rightText", "进行中");break;
	         default:tempMap.put("rightText", "进行中");break;
         }
         items.add(tempMap);
    }
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.plan_detail);
		
        ActionBar acb = ((Activity) PlanInfomationActivity.this).getActionBar();
        acb.setTitle("计划详情");
		
		lv = (ListView)this.findViewById(R.id.planDetailListView);
		getTaskDataFromIntent();

		initSimpleAdapter();
        /*SimpleAdapter的参数说明 
         * 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要 
         * 第二个参数表示生成一个Map(String ,Object)列表选项 
         * 第三个参数表示界面布局的id  表示该文件作为列表项的组件 
         * 第四个参数表示该Map对象的哪些key对应value来生成列表项 
         * 第五个参数表示来填充的组件 Map对象key对应的资源一依次填充组件 顺序有对应关系 
         * */
		spAdp = new SimpleAdapter(
				               this, 
				               items, 
				               R.layout.plan_detail_item, 
				               new String[]{"leftText", "rightText"}, 
				               new int[] {R.id.leftText, R.id.rightText }
				        );
		lv.setAdapter(spAdp);
	}
	
}
