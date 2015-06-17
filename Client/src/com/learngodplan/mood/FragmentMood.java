package com.learngodplan.mood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.home.MainActivity;
import com.learngodplan.webservice.LoadMoodRunnable;

public class FragmentMood extends Fragment {
        public static  FragmentTransaction transaction;
        
		private ListView moodLv;
		private ImageButton refreshBt;
		private ImageButton newMoodBt;
		
		private SimpleAdapter spAdp;
		private String moodStr;
		public static Handler mHandler;
		
	    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        
	    //用于生成Map
	    private String[] moodContent={"","","","","","","","","",""};
	    private String[] moodDate={"","","","","","","","","",""};
	    private String[] moodName={"","","","","","","","","",""};
	    
		public void initViewMember(View v){
			moodLv = (ListView)v.findViewById(R.id.moodList);
			refreshBt = (ImageButton)v.findViewById(R.id.refreshButton);
			refreshBt.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					//按下刷新键刷新页面
					getDataFromServer();
				}
			});
			newMoodBt = (ImageButton)v.findViewById(R.id.newMoodButton);
			newMoodBt.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					//进入发表心情的页面
					Intent it = new Intent(MainActivity.mainAcContext, ProposeNewMood.class);
					startActivity(it);
				}
			});
		}
	    
		public void getDataFromServer(){			
			Thread thr = new Thread(new LoadMoodRunnable());
			thr.start();
		}
		
		public void initMap(){
			if(moodStr == null){
				Log.d("d", "moodStr is null");
			}
			
			//根据"|"拆分服务器返回的心情串
			String[] firstSplit = moodStr.split("\\|");
			Log.d("xxx","hhhh");
			//根据“&”拆分每条心情的 用户 时间 内容
			Log.d("firstSplit.length", String.valueOf(firstSplit.length));
			
			for(int j = 0; j < firstSplit.length; j++){
				String[] temp = firstSplit[j].split("&");
				Log.d("temp length", String.valueOf(temp.length));
				
				moodName[j] = "心情来自:\n" + temp[0];
				moodDate[j] = "发布于:\n" + temp[1];
				moodContent[j] = temp[2];
			}
			
			items = new ArrayList<Map<String, Object>>();  
			for (int i = 0; i < firstSplit.length; i++) {  
			    Map<String, Object> listItem = new HashMap<String, Object>();  
			    listItem.put("moodName", moodName[i]);  
			    listItem.put("moodDate", moodDate[i]);  
			    listItem.put("moodContent", moodContent[i]);
			    
			    items.add(listItem);  
			}
		}
		
		public void initHandler(){
			mHandler = new Handler(){
				@Override
				public void handleMessage(Message msg){
					if(msg.what == LoadMoodRunnable.NOT_CONECT){
						Toast.makeText(MainActivity.mainAcContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(msg.what == LoadMoodRunnable.LOAD_SUCCESS){
						moodStr = msg.obj.toString();
						Log.d("moodStr in msg", moodStr);
					
						initMap();
						
				        /*SimpleAdapter的参数说明 
				         * 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要 
				         * 第二个参数表示生成一个Map(String ,Object)列表选项 
				         * 第三个参数表示界面布局的id  表示该文件作为列表项的组件 
				         * 第四个参数表示该Map对象的哪些key对应value来生成列表项 
				         * 第五个参数表示来填充的组件 Map对象key对应的资源一依次填充组件 顺序有对应关系 
				         * */
						spAdp = new SimpleAdapter(
								               MainActivity.mainAcContext, 
								               items, 
								               R.layout.mood_list_item, 
								               new String[]{"moodName", "moodDate", "moodContent"}, 
								               new int[] {R.id.moodFrom, R.id.moodTime, R.id.moodContent }
								        );
						
						moodLv.setAdapter(spAdp);
					}
					else{
						Toast.makeText(MainActivity.mainAcContext, "加载失败请检查网络", Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_mood, container,false);
	        ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
	        acb.setTitle("心情墙");
			
			initViewMember(view);
			initHandler();
			getDataFromServer();
			
			return view;
		}	
		
}